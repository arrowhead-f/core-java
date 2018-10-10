package eu.arrowhead.common;

import javax.validation.Valid;

import eu.arrowhead.common.exception.ArrowheadException;

/**
 * A RegistryResource offers methods to publish, unpublish and lookup entities
 * of type <code>T</code>
 * 
 * @author mzsilak
 *
 * @param <T>
 *          The generic type of the entity which is handled by this registry
 * @param <R>
 *          The generic response type of requests. e.g.
 *          {@link javax.ws.rs.core.Response}
 */
public interface RegistryResource<T, R> extends Resource<R> {

	final static String LOOKUP_PATH = "/lookup/{id}";
	final static String PUBLISH_PATH = "/publish";
	final static String UNPUBLISH_PATH = "/unpublish";

	/**
	 * A lookup for a specific entity <code>T</code> which is identified by the
	 * parameter id.
	 * 
	 * @param id
	 *          The identifier of the entity
	 * @return <R> A single entity embedded in the generic response type
	 *         <code>R</code>
	 */
	R lookup(final long id) throws ArrowheadException;

	/**
	 * A publish method for a specific entity <code>T</code> which registers the
	 * entity to the registry.
	 * 
	 * @param entity
	 *          The entity which shall be registered
	 * @return <R> The registered entity embedded in the generic response type
	 *         <code>R</code>
	 */
	R publish(@Valid final T entity) throws ArrowheadException;

	/**
	 * A unpublish method for a specific entity <code>T</code> which removes the
	 * entity from the registry.
	 * 
	 * @param entity
	 *          The entity which shall be removed
	 * @return <R> The removed entity embedded in the generic response type
	 *         <code>R</code>
	 */
	R unpublish(@Valid final T entity) throws ArrowheadException;
}
