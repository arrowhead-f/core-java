/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.core.deviceregistry;

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

import eu.arrowhead.common.DatabaseManager;
import eu.arrowhead.common.database.ArrowheadDevice;
import eu.arrowhead.common.exception.DataNotFoundException;
import eu.arrowhead.core.deviceregistry.model.DeviceRegistryEntry;

@Path("deviceregistry/mgmt")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DeviceRegistryApi {

	private final Logger log = Logger.getLogger(DeviceRegistryApi.class.getName());
	private final DatabaseManager databaseManager;

	public DeviceRegistryApi() throws ExceptionInInitializerError {
		super();
		databaseManager = DatabaseManager.getInstance();
		log.info(DeviceRegistryApi.class.getSimpleName() + " created");
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return "systemregistry/mgmt got it";
	}

	@GET
	@Path("id/{id}")
	public DeviceRegistryEntry getDeviceRegEntry(@PathParam("id") long id) {
		return databaseManager.get(DeviceRegistryEntry.class, id).orElseThrow(() -> new DataNotFoundException("DeviceRegistryEntry not found with id: " + id));
	}

	@GET
	@Path("all")
	public Response getAllSystems() {
		List<DeviceRegistryEntry> providedSystems = databaseManager.getAll(DeviceRegistryEntry.class, null);

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
		List<DeviceRegistryEntry> providedSystems = databaseManager.getAll(DeviceRegistryEntry.class, null);
		if (providedSystems.isEmpty()) {
			log.info("removeAllSystems had no effect");
			return Response.status(Status.NO_CONTENT).build();
		}
		for (DeviceRegistryEntry entry : providedSystems) {
			databaseManager.delete(entry);
		}
		log.info("removeAllSystems returns successfully");
		return Response.status(Status.OK).build();
	}

	@GET
	@Path("deviceId/{deviceId}")
	public List<DeviceRegistryEntry> getAllByDevice(@PathParam("deviceId") long deviceId) {
		ArrowheadDevice system = databaseManager.get(ArrowheadDevice.class, deviceId).<DataNotFoundException>orElseThrow(() -> {
			log.info("getAllByProvider throws DataNotFoundException");
			throw new DataNotFoundException("There are no Device Registry entries with the requested ArrowheadDevice in the database.");
		});

		final HashMap<String, Object> restrictionMap = new HashMap<>();
		restrictionMap.put("providedDevice", system);
		List<DeviceRegistryEntry> srList = databaseManager.getAll(DeviceRegistryEntry.class, restrictionMap);
		if (srList.isEmpty()) {
			log.info("getAllByProvider throws DataNotFoundException");
			throw new DataNotFoundException("There are no Device Registry entries with the requested ArrowheadDevice in the database.");
		}

		log.info("getAllByProvider returns " + srList.size() + " entries");
		return srList;
	}

	@GET
	@Path("deviceName/{deviceName}")
	public List<DeviceRegistryEntry> getAllBySystem(@PathParam("deviceName") String deviceName) {
		final HashMap<String, Object> restrictionMap = new HashMap<>();
		restrictionMap.put("deviceName", deviceName);
		ArrowheadDevice system = databaseManager.get(ArrowheadDevice.class, restrictionMap);
		if (system == null) {
			log.info("getAllBySystem throws DataNotFoundException");
			throw new DataNotFoundException("There are no Device Registry entries with the requested ArrowheadDevice in the database.");
		}

		restrictionMap.clear();
		restrictionMap.put("providedDevice", system);
		List<DeviceRegistryEntry> srList = databaseManager.getAll(DeviceRegistryEntry.class, restrictionMap);
		if (srList.isEmpty()) {
			log.info("getAllBySystem throws DataNotFoundException");
			throw new DataNotFoundException("There are no System Registry entries with the requested ArrowheadService in the database.");
		}

		log.info("getAllBySystem returns " + srList.size() + " entries");
		return srList;
	}

	@PUT
	@Path("update")
	public Response updateDeviceRegistryEntry(@Valid DeviceRegistryEntry entry) {
		final HashMap<String, Object> restrictionMap = new HashMap<>();
		restrictionMap.put("deviceName", entry.getProvidedDevice().getDeviceName());
		DeviceRegistryEntry device = databaseManager.get(DeviceRegistryEntry.class, restrictionMap);
		if (device == null) {
			log.info("updateSystemRegistryEntry throws DataNotFoundException");
			throw new DataNotFoundException("Requested System Registry entry not found in the database.");
		}

		restrictionMap.clear();
		restrictionMap.put("providedDevice", device);
		DeviceRegistryEntry retreivedEntry = databaseManager.get(DeviceRegistryEntry.class, restrictionMap);
		if (retreivedEntry == null) {
			log.info("updateDeviceRegistryEntry throws DataNotFoundException");
			throw new DataNotFoundException("Requested Device Registry entry not found in the database.");
		}
		retreivedEntry.setMacAddress(entry.getMacAddress());
		retreivedEntry.setEndOfValidity(entry.getEndOfValidity());
		retreivedEntry = databaseManager.merge(retreivedEntry);

		log.info("updateDeviceRegistryEntry successfully returns.");
		return Response.status(Status.ACCEPTED).entity(retreivedEntry).build();
	}

}
