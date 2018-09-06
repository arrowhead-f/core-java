/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.core.systemregistry;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import eu.arrowhead.common.DatabaseManager;
import eu.arrowhead.common.RegistryService;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.core.systemregistry.model.SystemRegistryEntry;

public class SystemRegistryService implements RegistryService<SystemRegistryEntry> {

	private final Logger log = Logger.getLogger(SystemRegistryService.class.getName());
	private final DatabaseManager databaseManager;

	public SystemRegistryService() throws ExceptionInInitializerError {
		try {
			databaseManager = DatabaseManager.getInstance();
		} catch (Throwable ex) {
			log.fatal("Failed to initialize DatabaseManager: " + ex.getMessage(), ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	protected ArrowheadException logAndCreateException(final Exception e) {
		log.error(e.getMessage(), e);
		return new ArrowheadException(e.getMessage(), Status.INTERNAL_SERVER_ERROR.getStatusCode(), e);
	}

	protected void verifyNotNull(final Long id) {
		if (id == null) {
			throw new ArrowheadException("The given identifier is null", Status.BAD_REQUEST.getStatusCode());
		}
	}

	protected void verifyNotNull(final SystemRegistryEntry entry) {
		if (entry == null) {
			throw new ArrowheadException("The given entry is null", Status.BAD_REQUEST.getStatusCode());
		}
	}

	public SystemRegistryEntry lookup(final Long id) throws EntityNotFoundException, ArrowheadException {
		final SystemRegistryEntry returnValue;

		try {
			verifyNotNull(id);

			Optional<SystemRegistryEntry> optional = databaseManager.get(SystemRegistryEntry.class, id);
			returnValue = optional.orElseThrow(() -> {
				final String message = "The requested entity does not exist";
				log.warn(message);
				return new EntityNotFoundException(message);
			});
		} catch (final ArrowheadException e) {
			log.warn(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw logAndCreateException(e);
		}

		return returnValue;
	}

	public SystemRegistryEntry publish(final SystemRegistryEntry entity) throws ArrowheadException {
		final SystemRegistryEntry returnValue;

		try {
			verifyNotNull(entity);
			returnValue = databaseManager.save(entity);
		} catch (final ArrowheadException e) {
			log.warn(e.getMessage(), e);
			throw e;
		} catch (final Exception e) {
			throw logAndCreateException(e);
		}

		return returnValue;
	}

	public SystemRegistryEntry unpublish(final SystemRegistryEntry entity)
			throws EntityNotFoundException, ArrowheadException {
		final SystemRegistryEntry returnValue;

		try {
			verifyNotNull(entity);

			databaseManager.delete(entity);
			returnValue = entity;
		} catch (final ArrowheadException e) {
			log.warn(e.getMessage(), e);
			throw e;
		} catch (final Exception e) {
			throw logAndCreateException(e);
		}
		return returnValue;
	}

}
