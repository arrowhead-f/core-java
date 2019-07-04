/*
 *  Copyright (c) 2018 Jens Eliasson, Luleå University of Technology
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.core.datamanager;

import eu.arrowhead.common.Utility;
import eu.arrowhead.common.exception.BadPayloadException;
import eu.arrowhead.common.messages.SenMLMessage;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.Gson;


/**
 * This is the REST resource for the DataManager Support Core System.
 */
@Path("datamanager")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DataManagerResource {

  private static final Logger log = Logger.getLogger(DataManagerResource.class.getName());

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getIt() {
    return "This is the DataManager Arrowhead Core System.";
  }


  /* Historian resources */

  @GET
  @Path("historian")
  @Produces("application/json")
  public Response getInfo(@Context ContainerRequestContext requestContext) {
    Gson gson = new Gson();

    ArrayList<String> systems = DataManagerService.getSystems();
    JsonObject answer = new JsonObject();
    JsonElement systemlist = gson.toJsonTree(systems);
    answer.add("systems", systemlist);

    String jsonStr = gson.toJson(answer);
    return Response.status(Status.OK).entity(jsonStr).type(MediaType.APPLICATION_JSON).build();
  }


  @GET
  @Path("historian/{systemName}")
  public Response getHist(@PathParam("systemName") String systemName) {
    return PutHist(systemName, "{\"op\": \"list\"}");
  }


  @PUT
  @Path("historian/{systemName}")
  @Consumes("application/json")
  @Produces("application/json")
  public Response PutHist(@PathParam("systemName") String systemName, String requestBody) {
    JsonParser parser= new JsonParser();
    try{
      JsonObject obj = parser.parse(requestBody).getAsJsonObject();

      String op = obj.get("op").getAsString();
      if(op.equals("list")){
	//System.out.println("OP: list");
	ArrayList<String> services = DataManagerService.getServicesFromSystem(systemName);
	/*for (String srv: services) {
	  System.out.println(":" +srv);
	}*/
	Gson gson = new Gson();
	JsonObject answer = new JsonObject();
	JsonElement servicelist = gson.toJsonTree(services);
	answer.add("services", servicelist);
	String jsonStr = gson.toJson(answer);
	//System.out.println("Asnwer: "+jsonStr);
	
	return Response.status(Status.OK).entity(jsonStr).type(MediaType.APPLICATION_JSON).build();
      } else if(op.equals("create")){
	//System.out.println("OP: CREATE");
	String srvName = obj.get("srvName").getAsString();
	String srvType = obj.get("srvType").getAsString();
	//System.out.println("Create SRV: "+srvName+" of type: "+srvType+" for: " + systemName);

	/* check if service already exists */
	ArrayList<String> services = DataManagerService.getServicesFromSystem(systemName);
	for (String srv: services) {
	  if(srv.equals(srvName)){
	      log.info("  service:" +srv + " already exists");
	      Gson gson = new Gson();
	      JsonObject answer = new JsonObject();
	      answer.addProperty("createResult", "Already exists");
	      String jsonStr = gson.toJson(answer);
	      return Response.status(Status.CONFLICT).entity(jsonStr).type(MediaType.APPLICATION_JSON).build();
	  }
	}

	/* create the service */
	boolean ret = DataManagerService.addServiceForSystem(systemName, srvName);
	if (ret==true)
	  return Response.status(Status.CREATED).entity("{}").type(MediaType.APPLICATION_JSON).build();
	else
	  return Response.status(500).entity("{\"x\": \"Could not create service\"}").type(MediaType.APPLICATION_JSON).build();

      } else if(op.equals("delete")){
	log.info("OP: DELETE");
	String srvName = obj.get("srvName").getAsString();
	String srvType = obj.get("srvType").getAsString();
	System.out.println("Delete SRV: "+srvName+" of type: "+srvType+" for: " + systemName);

	/* check if service exists */
	ArrayList<String> services = DataManagerService.getServicesFromSystem(systemName);
	boolean found = false;
	for (String srv: services) {
	  if(srv.equals(srvName)){
	    found = true;
	    break;
	  }
	}
	if (!found) {
          log.info("service:" +srvName + " does not exists");
	  Gson gson = new Gson();
	  JsonObject answer = new JsonObject();
	  answer.addProperty("createResult", "No such service");
	  String jsonStr = gson.toJson(answer);
	  return Response.status(Status.BAD_REQUEST).entity(jsonStr).type(MediaType.APPLICATION_JSON).build();
	}

	/* delete the service */
	boolean ret = DataManagerService.deleteServiceForSystem(systemName, srvName);
	if (ret==true)
	  return Response.status(Status.CREATED).entity("{}").type(MediaType.APPLICATION_JSON).build();
	else
	  return Response.status(500).entity("{\"x\": \"Could not delete service\"}").type(MediaType.APPLICATION_JSON).build();

      } else {
        log.debug("Unsupported operation: " + op);
	Response re = Response.status(300).build();
	return re;
      }
    } catch(Exception je){
      log.debug(je.toString());
      Response re = Response.status(300).build();
      return re;
    }

  }


  @GET
  @Path("historian/{systemName}/{serviceName}")
  @Produces("application/json")
  public Response getData(@PathParam("systemName") String systemName, @PathParam("serviceName") String serviceName, @QueryParam("count") @DefaultValue("1") String count_s, @Context UriInfo uriInfo) {
    int statusCode = 0;
    int count = Integer.parseInt(count_s);
     
    log.info("Historian GET for system '"+systemName+"', service '"+serviceName+"'"); 
    log.info("getData requested with count: " + count);

    MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
    int i=0;
    String sig;
    Vector<String> signals = new Vector<String>();
    do {
      sig = queryParams.getFirst("sig"+i);
      if (sig != null) {
        //System.out.println("sig["+i+"]: "+sig+"");
	signals.add(sig);
      }
      i++;
    } while (sig != null);
    if (signals.size() == 0)
      signals = null;
    
    Vector<SenMLMessage> ret = null;
    if(signals == null)
      ret = DataManagerService.fetchEndpoint(serviceName, count);
    else
      ret = DataManagerService.fetchEndpoint(serviceName, count, signals);

    return Response.status(Status.OK).entity(ret).build();
  }


  @PUT
  @Path("historian/{systemName}/{serviceName}")
  @Consumes("application/senml+json")
  public Response PutData(@PathParam("systemName") String systemName, @PathParam("serviceName") String serviceName, @Valid Vector<SenMLMessage> sml) {
    boolean statusCode = DataManagerService.createEndpoint(serviceName);
    log.info("Historian PUT for system '"+systemName+"', service '"+serviceName+"'"); 

    SenMLMessage head = sml.firstElement();
    if(head.getBt() == null)
      head.setBt((double)System.currentTimeMillis() / 1000.0);

    for(SenMLMessage s: sml) {
      //System.out.println("object" + s.toString());
      if(s.getT() == null && s.getBt() != null)
	s.setT(0.0);
    } 
    statusCode = DataManagerService.updateEndpoint(serviceName, sml);

    String jsonret = "{\"p\": "+ 0 +",\"x\": 0}";
    return Response.ok(jsonret, MediaType.APPLICATION_JSON).build();
  }

 
  /* Proxy Service */
  @GET
  @Path("proxy")
  @Produces("application/json")
  public Response getSystems() {
    Gson gson = new Gson();

    List<String> pes = ProxyService.getAllEndpoints();
    JsonObject answer = new JsonObject();
    JsonElement systemlist = gson.toJsonTree(pes);
    answer.add("systems", systemlist);

    String jsonStr = gson.toJson(answer);
    return Response.status(Status.OK).entity(jsonStr).type(MediaType.APPLICATION_JSON).build();
  }


  @GET
  @Path("proxy/{systemName}")
  @Produces("application/json")
  public Response proxyGet(@PathParam("systemName") String systemName) {
    int statusCode = 0;
    List<ProxyElement> pes = ProxyService.getEndpoints(systemName);
    if (pes.size() == 0) {
      log.info("proxy GET to systemName: " + systemName + " not found");
      return Response.status(Status.NOT_FOUND).build();
    }

    ArrayList<String> systems= new ArrayList<String>();
    for (ProxyElement pe: pes) {
      systems.add(pe.serviceName);
    }

    Gson gson = new Gson();
    JsonObject answer = new JsonObject();
    JsonElement servicelist = gson.toJsonTree(systems);
    answer.add("services", servicelist);
    String jsonStr = gson.toJson(answer);
    return Response.status(Status.OK).entity(jsonStr).type(MediaType.APPLICATION_JSON).build();
  }


  @PUT
  @Path("proxy/{systemName}")
  @Consumes("application/json")
  @Produces("application/json")
  public Response PutProxy(@PathParam("systemName") String systemName, String requestBody) {
    JsonParser parser= new JsonParser();
    try{
      JsonObject obj = parser.parse(requestBody).getAsJsonObject();

      String op = obj.get("op").getAsString();
      if(op.equals("list")){

	Response re = Response.status(200).build();
	return re;
      } else if(op.equals("create")){
	String srvName = obj.get("srvName").getAsString();
	String srvType = obj.get("srvType").getAsString();
	log.info("Create SRV: "+srvName+" of type: "+srvType+" for: " + systemName);

	/* check if service already exists */
	ArrayList<ProxyElement> services = ProxyService.getEndpoints(systemName);
	for (ProxyElement srv: services) {
	  if(srv.serviceName.equals(srvName)){
	    Gson gson = new Gson();
	    JsonObject answer = new JsonObject();
	    answer.addProperty("createResult", "Already exists");
	    String jsonStr = gson.toJson(answer);
	    return Response.status(Status.CONFLICT).entity(jsonStr).type(MediaType.APPLICATION_JSON).build();
	  }
	}

	/* create the service */
	boolean ret = ProxyService.addEndpoint(new ProxyElement(systemName, srvName));
	if (ret==true)
	  return Response.status(Status.CREATED).entity("{}").type(MediaType.APPLICATION_JSON).build();
	else
	  return Response.status(500).entity("{\"x\": \"Could not create service\"}").type(MediaType.APPLICATION_JSON).build();

      } else if(op.equals("delete")){

      } else {
	log.debug("Unsupported OP: " + op);
	Response re = Response.status(300).build();
	return re;
      }

    } catch(Exception je){
      log.error(je.toString());
      Response re = Response.status(300).build();
      return re;
    }
    Response re = Response.status(300).build();
    return re;
  }
 

  @GET
  @Path("proxy/{systemName}/{serviceName}")
  @Produces("application/json")
  public Response proxyGet(@PathParam("systemName") String systemName, @PathParam("serviceName") String serviceName) {
    int statusCode = 0;
    ProxyElement pe = ProxyService.getEndpoint(serviceName);
    if (pe == null) {
      log.info("proxy GET to serviceName: " + serviceName + " not found");
      return Response.status(Status.NOT_FOUND).build();
    }

    return Response.status(Status.OK).entity(pe.msg).build();
  }


  @PUT
  @Path("proxy/{systemName}/{serviceName}")
  @Consumes("application/senml+json")
  public Response proxyPut(@PathParam("systemName") String systemName, @PathParam("serviceName") String serviceName, @Valid Vector<SenMLMessage> sml) {
    ProxyElement pe = ProxyService.getEndpoint(serviceName);
    if (pe == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    //System.out.println("sml: "+ sml + "\t"+sml.toString());
    boolean statusCode = ProxyService.updateEndpoint(serviceName, sml);
    log.info("putData/SenML returned with status code: " + statusCode + " from: " + sml.get(0).getBn() + " at: " + sml.get(0).getBt());

    String jsonret = "{\"rc\": 0}";
    return Response.ok(jsonret, MediaType.APPLICATION_JSON).build();
  }

}
