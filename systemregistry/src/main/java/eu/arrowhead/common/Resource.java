package eu.arrowhead.common;

/**
 * This interface denotes a resource in the Arrowhead framework which is reachable by non-RMI methods.
 * 
 * @author Mario ZSILAK
 *
 * @param <R> The generic response type of requests. e.g. {@link javax.ws.rs.core.Response}
 */
public interface Resource<R> {

	/**
	 * A ping method to verify the general reachability of this resource.
	 * 
	 * @return A positive response with the given generic type.
	 */
	R ping();
}
