/*
 * Copyright (c) 2018 AITIA International Inc.
 *
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.serviceregistry;

import eu.arrowhead.common.database.ServiceRegistryEntry;
import eu.arrowhead.common.exception.BadPayloadException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.log4j.Logger;

@Path("serviceregistry")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServiceRegistryResource {

  private static final Logger log = Logger.getLogger(ServiceRegistryResource.class.getName());

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getIt() {
    return "This is the Service Registry Arrowhead Core System.";
  }

  @POST
  @Path("register")
  public Response registerService(ServiceRegistryEntry entry) {
    if (!entry.isValid()) {
      log.error("registerService throws BadPayloadException");
      throw new BadPayloadException("ServiceRegistryEntry has missing/incomplete mandatory field(s).", Status.BAD_REQUEST.getStatusCode());
    }

    return Response.status(Status.CREATED).entity(ServiceRegistryService.registerService(entry)).build();
  }

  @PUT
  @Path("remove")
  public Response removeService(ServiceRegistryEntry entry) {
    if (!entry.isValid()) {
      log.error("removeService throws BadPayloadException");
      throw new BadPayloadException("Bad payload: ServiceRegistryEntry has missing/incomplete mandatory field(s).",
                                    Status.BAD_REQUEST.getStatusCode());
    }

    ServiceRegistryEntry removedEntry = ServiceRegistryService.removeService(entry);
    if (removedEntry != null) {
      return Response.status(Status.OK).entity(removedEntry).build();
    } else {
      return Response.status(Status.NO_CONTENT).entity(entry).build();
    }
  }

}