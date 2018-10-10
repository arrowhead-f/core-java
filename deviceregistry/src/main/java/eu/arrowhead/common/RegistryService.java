package eu.arrowhead.common;

import javax.validation.Valid;

import eu.arrowhead.common.exception.ArrowheadException;

public interface RegistryService<T>
{
	T lookup(final long id) throws ArrowheadException;
	T publish(@Valid final T entity) throws ArrowheadException;
	T unpublish(@Valid final T entity) throws ArrowheadException;
}
