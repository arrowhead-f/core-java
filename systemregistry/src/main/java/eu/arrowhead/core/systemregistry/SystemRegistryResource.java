/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.core.systemregistry;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import eu.arrowhead.common.RegistryResource;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.core.systemregistry.model.SystemRegistryEntry;

@Path("systemregistry")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SystemRegistryResource implements RegistryResource<SystemRegistryEntry, Response> {
	private final Logger log = Logger.getLogger(SystemRegistryResource.class.getName());
	private final SystemRegistryService registryService;

	public SystemRegistryResource() throws ExceptionInInitializerError {
		super();
		registryService = new SystemRegistryService();
		log.info(SystemRegistryResource.class.getSimpleName() + " created");
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping() {
		return Response.status(Response.Status.OK).entity("This is the System Registry Arrowhead Core System.").build();
	}

	@GET
	@Path(LOOKUP_PATH)
	public Response lookup(@Context final UriInfo uriInfo, @PathParam("id") final Long id) {
		SystemRegistryEntry returnValue;
		Response response;

		try {
			log.info(String.format("request: GET %s", uriInfo.getPath()));
			returnValue = registryService.lookup(id);
			response = createSuccessResponse(returnValue, Status.OK);
		} catch (final EntityNotFoundException e) {
			response = createNotFoundResponse();
		} catch (final ArrowheadException e) {
			response = createArrowheadResponse(e);
		} catch (final Exception e) {
			response = createGenericErrorResponse(e);
		}

		log.info(String.format("response: GET %s - %d", uriInfo.getPath(), response.getStatus()));
		return response;
	}

	@POST
	@Path(PUBLISH_PATH)
	public Response publish(@Context final UriInfo uriInfo, final SystemRegistryEntry entry) {
		SystemRegistryEntry returnValue;
		Response response;

		try {
			log.info(String.format("request: POST %s - body: %s", uriInfo.getPath(), entry));
			returnValue = registryService.publish(entry);
			response = createSuccessResponse(returnValue, Status.CREATED);
		} catch (final ArrowheadException e) {
			response = createArrowheadResponse(e);
		} catch (final Exception e) {
			response = createGenericErrorResponse(e);
		}

		log.info(String.format("response: POST %s - %d", uriInfo.getPath(), response.getStatus()));
		return response;
	}

	@POST
	@Path(UNPUBLISH_PATH)
	public Response unpublish(@Context final UriInfo uriInfo, final SystemRegistryEntry entry) {
		SystemRegistryEntry returnValue;
		Response response;

		try {
			log.info(String.format("request: POST %s - body: %s", uriInfo.getPath(), entry));
			returnValue = registryService.unpublish(entry);
			response = createSuccessResponse(returnValue, Status.OK);
		} catch (final EntityNotFoundException e) {
			response = createNotFoundResponse();
		} catch (final ArrowheadException e) {
			response = createArrowheadResponse(e);
		} catch (final Exception e) {
			response = createGenericErrorResponse(e);
		}

		log.info(String.format("response: POST %s - %d", uriInfo.getPath(), response.getStatus()));
		return response;
	}

	protected Response createSuccessResponse(final SystemRegistryEntry entity, final Status status) {
		return Response.status(status).entity(entity).build();
	}

	protected Response createSuccessResponse(final List<SystemRegistryEntry> list, final Status status) {
		return Response.status(status).entity(new GenericEntity<>(list, SystemRegistryEntry.class)).build();
	}

	protected Response createNotFoundResponse() {
		return Response.status(Status.NOT_FOUND).entity("The requested entity was not found in the system.").build();
	}

	protected Response createArrowheadResponse(final ArrowheadException e) {
		return Response.status(e.getErrorCode()).entity(e.getMessage()).build();
	}

	protected Response createGenericErrorResponse(final Exception e) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
	}
}