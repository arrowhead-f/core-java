/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.deviceregistry;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response.Status;

import eu.arrowhead.common.DatabaseManager;
import eu.arrowhead.common.RegistryService;
import eu.arrowhead.common.database.ArrowheadDevice;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.DataNotFoundException;
import eu.arrowhead.core.deviceregistry.model.DeviceRegistryEntry;

public class DeviceRegistryService  implements RegistryService<DeviceRegistryEntry> {
	
	private final DatabaseManager databaseManager;

	public DeviceRegistryService() throws ExceptionInInitializerError {
		databaseManager = DatabaseManager.getInstance();
	}

	public DeviceRegistryEntry lookup(final long id) throws EntityNotFoundException, ArrowheadException {
		final DeviceRegistryEntry returnValue;

		try {
			Optional<DeviceRegistryEntry> optional = databaseManager.get(DeviceRegistryEntry.class, id);
			returnValue = optional.orElseThrow(() -> {
				return new DataNotFoundException("The requested entity does not exist", Status.NOT_FOUND.getStatusCode());
			});
		} catch (final ArrowheadException e) {
			throw e;
		} catch (Exception e) {
			throw new ArrowheadException(e.getMessage(), Status.NOT_FOUND.getStatusCode(), e);
		}

		return returnValue;
	}
	

	public DeviceRegistryEntry publish(final DeviceRegistryEntry entity) throws ArrowheadException {
		final DeviceRegistryEntry returnValue;

		try {
			entity.setProvidedDevice(resolve(entity.getProvidedDevice()));
			returnValue = databaseManager.save(entity);
		} catch (final ArrowheadException e) {
			throw e;
		} catch (Exception e) {
			throw new ArrowheadException(e.getMessage(), Status.NOT_FOUND.getStatusCode(), e);
		}

		return returnValue;
	}

	public DeviceRegistryEntry unpublish(final DeviceRegistryEntry entity) throws EntityNotFoundException, ArrowheadException {
		final DeviceRegistryEntry returnValue;

		try {
			databaseManager.delete(entity);
			returnValue = entity;
		} catch (final ArrowheadException e) {
			throw e;
		} catch (Exception e) {
			throw new ArrowheadException(e.getMessage(), Status.NOT_FOUND.getStatusCode(), e);
		}
		return returnValue;
	}

	protected ArrowheadDevice resolve(final ArrowheadDevice provider) {
		final ArrowheadDevice returnValue;

		if (provider.getId() != null) {
			Optional<ArrowheadDevice> optional = databaseManager.get(ArrowheadDevice.class, provider.getId());
			returnValue = optional.orElseThrow(() -> new ArrowheadException("ArrowheadDevice does not exist", Status.BAD_REQUEST.getStatusCode()));
		} else {
			returnValue = databaseManager.save(provider);
		}

		return returnValue;
	}
}
