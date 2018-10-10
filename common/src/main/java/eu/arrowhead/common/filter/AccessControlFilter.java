/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.common.filter;

import eu.arrowhead.common.Utility;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.misc.SecurityUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import org.apache.log4j.Logger;

public abstract class AccessControlFilter implements ContainerRequestFilter {

  protected static final Logger log = Logger.getLogger(AccessControlFilter.class.getName());
  @Context
  protected Configuration configuration;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    SecurityContext sc = requestContext.getSecurityContext();
    if (sc.isSecure()) {
      String requestTarget = Utility.stripEndSlash(requestContext.getUriInfo().getRequestUri().toString());
      String requestJson = Utility.getRequestPayload(requestContext.getEntityStream());
      String commonName = SecurityUtils.getCertCNFromSubject(sc.getUserPrincipal().getName());
      if (!isClientAuthorized(commonName, requestContext.getMethod(), requestTarget, requestJson)) {
        log.error(commonName + " is unauthorized to access " + requestTarget);
        throw new AuthException(commonName + " is unauthorized to access " + requestTarget);
      }
      InputStream in = new ByteArrayInputStream(requestJson.getBytes(StandardCharsets.UTF_8));
      requestContext.setEntityStream(in);
    }
  }

  public boolean isClientAuthorized(String clientCN, String method, String requestTarget, String requestJson) {
    String serverCN = (String) configuration.getProperty("server_common_name");
    String[] serverFields = serverCN.split("\\.", 2);
    // serverFields contains: coreSystemName, cloudName.operator.arrowhead.eu

    //All requests from the local cloud are allowed
    return SecurityUtils.isKeyStoreCNArrowheadValid(clientCN, serverFields[1]);
  }
}
