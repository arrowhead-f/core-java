/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.core.systemregistry;

import eu.arrowhead.common.DatabaseManager;
import eu.arrowhead.common.database.ArrowheadDevice;
import eu.arrowhead.common.database.ArrowheadSystem;
import eu.arrowhead.common.database.SystemRegistryEntry;
import eu.arrowhead.common.exception.DataNotFoundException;
import java.util.HashMap;
import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.log4j.Logger;

@Path("systemregistry/mgmt")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SystemRegistryApi {

  private final Logger log = Logger.getLogger(SystemRegistryApi.class.getName());
  private final DatabaseManager databaseManager;

  public SystemRegistryApi() throws ExceptionInInitializerError {
    super();
    databaseManager = DatabaseManager.getInstance();
    log.info(SystemRegistryApi.class.getSimpleName() + " created");
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getIt() {
    return "systemregistry/mgmt got it";
  }

  @GET
  @Path("id/{id}")
  public SystemRegistryEntry getServiceRegEntry(@PathParam("id") long id) {
    return databaseManager.get(SystemRegistryEntry.class, id)
                          .orElseThrow(() -> new DataNotFoundException("SystemRegistryEntry not found with id: " + id));
  }

  @GET
  @Path("all")
  public Response getAllSystems() {
    List<SystemRegistryEntry> providedSystems = databaseManager.getAll(SystemRegistryEntry.class, null);

    log.info("getAllSystems returns " + providedSystems.size() + " entries");
    if (providedSystems.isEmpty()) {
      return Response.status(Status.NO_CONTENT).entity(providedSystems).build();
    } else {
      return Response.status(Response.Status.OK).entity(providedSystems).build();
    }
  }

  @DELETE
  @Path("all")
  public Response removeAllSystems() {
    List<SystemRegistryEntry> providedSystems = databaseManager.getAll(SystemRegistryEntry.class, null);
    if (providedSystems.isEmpty()) {
      log.info("removeAllSystems had no effect");
      return Response.status(Status.NO_CONTENT).build();
    }
    for (SystemRegistryEntry entry : providedSystems) {
      databaseManager.delete(entry);
    }
    log.info("removeAllSystems returns successfully");
    return Response.status(Status.OK).build();
  }

  @GET
  @Path("deviceId/{deviceId}")
  public List<SystemRegistryEntry> getAllByProvider(@PathParam("deviceId") long deviceId) {
    ArrowheadDevice system = databaseManager.get(ArrowheadDevice.class, deviceId).<DataNotFoundException>orElseThrow(() -> {
      log.info("getAllByProvider throws DataNotFoundException");
      throw new DataNotFoundException("There are no System Registry entries with the requested ArrowheadDevice in the database.");
    });

    final HashMap<String, Object> restrictionMap = new HashMap<>();
    restrictionMap.put("provider", system);
    List<SystemRegistryEntry> srList = databaseManager.getAll(SystemRegistryEntry.class, restrictionMap);
    if (srList.isEmpty()) {
      log.info("getAllByProvider throws DataNotFoundException");
      throw new DataNotFoundException("There are no System Registry entries with the requested ArrowheadDevice in the database.");
    }

    log.info("getAllByProvider returns " + srList.size() + " entries");
    return srList;
  }

  @GET
  @Path("systemName/{systemName}")
  public List<SystemRegistryEntry> getAllBySystem(@PathParam("systemName") String systemName) {
    final HashMap<String, Object> restrictionMap = new HashMap<>();
    restrictionMap.put("systemName", systemName);
    ArrowheadSystem system = databaseManager.get(ArrowheadSystem.class, restrictionMap);
    if (system == null) {
      log.info("getAllBySystem throws DataNotFoundException");
      throw new DataNotFoundException("There are no System Registry entries with the requested ArrowheadService in the database.");
    }

    restrictionMap.clear();
    restrictionMap.put("providedSystem", system);
    List<SystemRegistryEntry> srList = databaseManager.getAll(SystemRegistryEntry.class, restrictionMap);
    if (srList.isEmpty()) {
      log.info("getAllBySystem throws DataNotFoundException");
      throw new DataNotFoundException("There are no System Registry entries with the requested ArrowheadService in the database.");
    }

    log.info("getAllBySystem returns " + srList.size() + " entries");
    return srList;
  }

  @PUT
  @Path("update")
  public Response updateSystemRegistryEntry(@Valid SystemRegistryEntry entry) {
    final HashMap<String, Object> restrictionMap = new HashMap<>();
    restrictionMap.put("systemName", entry.getProvidedSystem().getSystemName());
    SystemRegistryEntry system = databaseManager.get(SystemRegistryEntry.class, restrictionMap);
    if (system == null) {
      log.info("updateSystemRegistryEntry throws DataNotFoundException");
      throw new DataNotFoundException("Requested System Registry entry not found in the database.");
    }

    restrictionMap.clear();
    restrictionMap.put("deviceName", entry.getProvider().getDeviceName());
    ArrowheadDevice provider = databaseManager.get(ArrowheadDevice.class, restrictionMap);
    if (provider == null) {
      log.info("updateSystemRegistryEntry throws DataNotFoundException");
      throw new DataNotFoundException("Requested System Registry entry not found in the database.");
    }

    restrictionMap.clear();
    restrictionMap.put("provider", provider);
    restrictionMap.put("providedSystem", system);
    SystemRegistryEntry retreivedEntry = databaseManager.get(SystemRegistryEntry.class, restrictionMap);
    if (retreivedEntry == null) {
      log.info("updateSystemRegistryEntry throws DataNotFoundException");
      throw new DataNotFoundException("Requested System Registry entry not found in the database.");
    }
    retreivedEntry.setServiceUri(entry.getServiceUri());
    retreivedEntry.setEndOfValidity(entry.getEndOfValidity());
    retreivedEntry = databaseManager.merge(retreivedEntry);

    log.info("updateSystemRegistryEntry successfully returns.");
    return Response.status(Status.ACCEPTED).entity(retreivedEntry).build();
  }

}
