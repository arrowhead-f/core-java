/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.serviceregistry_sql.filter;

import eu.arrowhead.common.Utility;
import eu.arrowhead.common.database.ServiceRegistryEntry;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.filter.AccessControlFilter;
import eu.arrowhead.common.misc.SecurityUtils;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION) //2nd highest priority constant, this filter gets executed after the SecurityFilter
public class ServiceRegACF extends AccessControlFilter {

  @Override
  public boolean isClientAuthorized(String clientCN, String method, String requestTarget, String requestJson) {
    if (!SecurityUtils.isKeyStoreCNArrowheadValid(clientCN)) {
      log.info(clientCN + " is not valid common name, access denied!");
      return false;
    }

    String serverCN = (String) configuration.getProperty("server_common_name");
    String[] serverFields = serverCN.split("\\.", 2);

    if (requestTarget.contains("mgmt")) {
      //Only the local System Operator can use these methods
      return clientCN.equalsIgnoreCase("sysop." + serverFields[1]);
    } else if (requestTarget.endsWith("register") || requestTarget.endsWith("remove")) {

      // All requests from the local cloud are allowed
      ServiceRegistryEntry entry = Utility.fromJson(requestJson, ServiceRegistryEntry.class);
      String[] clientFields = clientCN.split("\\.", 2);

      String providerName = entry.getProvider().getSystemName().replaceAll("_", "");
      if (!providerName.equalsIgnoreCase(clientFields[0])) {
        // BUT a provider system can only register/remove its own services!
        log.error("Provider system name and cert common name do not match! SR registering/removing denied!");
        throw new AuthException("Provider system " + entry.getProvider().getSystemName() + " and cert common name (" + clientCN + ") do not match!",
                                Status.UNAUTHORIZED.getStatusCode());
      }

      return serverFields[1].equalsIgnoreCase(clientFields[1]);
    } else if (requestTarget.endsWith("query")) {

      // Only requests from the local Orchestrator and Gatekeeper are allowed
      return clientCN.equalsIgnoreCase("orchestrator." + serverFields[1]) || clientCN.equalsIgnoreCase("gatekeeper." + serverFields[1]);
    }

    return false;
  }

}
