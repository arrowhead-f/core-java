/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.deviceregistry;

import eu.arrowhead.common.ArrowheadMain;
import eu.arrowhead.common.misc.CoreSystem;
import eu.arrowhead.core.deviceregistry.filter.DeviceRegACF;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;

public class DeviceRegistryMain extends ArrowheadMain {

  private DeviceRegistryMain(String[] args) {

    Set<Class<?>> classes = new HashSet<>(Arrays.asList(
    																	DeviceRegistryResource.class, 
    																	DeviceRegistryApi.class,
    																	DeviceRegACF.class,
    																	OpenApiResource.class,
    																	DeviceRegistryMain.class));
    String[] packages = {
    		"eu.arrowhead.common.exception", 
    		"eu.arrowhead.common.json", 
    		"eu.arrowhead.common.filter", 
				"eu.arrowhead.core.systemregistry"};
    init(CoreSystem.DEVICEREGISTRY, args, classes, packages);


    listenForInput();
  }
	protected void adaptServer(HttpServer server) {
		final HttpHandler httpHandler = new CLStaticHttpHandler(HttpServer.class.getClassLoader(), "/swagger/");
		server.getServerConfiguration().addHttpHandler(httpHandler, "/swagger");
	}

	@GET
	@Path("/swagger")
	public Response swagger(@Context UriInfo uriInfo) throws URISyntaxException {
		final URI uri = new URI(uriInfo.getBaseUri().toString() + "/swagger/");
		return Response.status(Response.Status.TEMPORARY_REDIRECT).contentLocation(uri).build();
	}

  public static void main(String[] args) {
    new DeviceRegistryMain(args);
  }
}
