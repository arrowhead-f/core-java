/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.deviceregistry;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import eu.arrowhead.common.RegistryResource;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.core.deviceregistry.model.DeviceRegistryEntry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("deviceregistry")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DeviceRegistryResource implements RegistryResource<DeviceRegistryEntry, Response> {

	private final Logger log = Logger.getLogger(DeviceRegistryResource.class.getName());
	private final DeviceRegistryService registryService;


	public DeviceRegistryResource() throws ExceptionInInitializerError {
		super();
		registryService = new DeviceRegistryService();
		log.info(DeviceRegistryResource.class.getSimpleName() + " created");
	}
	
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response ping() {
    return Response.status(Response.Status.OK).entity("This is the Device Registry Arrowhead Core System.").build();
  }
	
	@GET
	@Path(LOOKUP_PATH)
	@Operation(summary = "Searches a DeviceRegistryEntry by id", responses = {
	    @ApiResponse(content = @Content(schema = @Schema(implementation = DeviceRegistryEntry.class))) })
	public Response lookup(@PathParam("id") final long id) throws ArrowheadException {
		DeviceRegistryEntry returnValue;
		Response response;

		returnValue = registryService.lookup(id);
		response = Response.status(Status.OK).entity(returnValue).build();

		return response;
	}
	
	@POST
	@Path(PUBLISH_PATH)
	public Response publish(@Valid final DeviceRegistryEntry entry) throws ArrowheadException {
		DeviceRegistryEntry returnValue;
		Response response;

		returnValue = registryService.publish(entry);
		response = Response.status(Status.CREATED).entity(returnValue).build();

		return response;
	}

	@POST
	@Path(UNPUBLISH_PATH)
	public Response unpublish(@Valid final DeviceRegistryEntry entry) throws ArrowheadException {
		DeviceRegistryEntry returnValue;
		Response response;

		returnValue = registryService.unpublish(entry);
		response = Response.status(Status.OK).entity(returnValue).build();

		return response;
	}
}
