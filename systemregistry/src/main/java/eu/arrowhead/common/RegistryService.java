package eu.arrowhead.common;

import javax.persistence.EntityNotFoundException;

import eu.arrowhead.common.exception.ArrowheadException;

public interface RegistryService<T>
{
	T lookup(final Long id) throws EntityNotFoundException, ArrowheadException;
	T publish(final T entity) throws ArrowheadException;
	T unpublish(final T entity) throws EntityNotFoundException, ArrowheadException;
}
