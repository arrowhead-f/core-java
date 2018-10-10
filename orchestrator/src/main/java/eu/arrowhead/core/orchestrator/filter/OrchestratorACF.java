/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.orchestrator.filter;

import eu.arrowhead.common.Utility;
import eu.arrowhead.common.exception.AuthException;
import eu.arrowhead.common.filter.AccessControlFilter;
import eu.arrowhead.common.messages.ServiceRequestForm;
import eu.arrowhead.common.misc.SecurityUtils;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION) //2nd highest priority constant, this filter gets executed after the SecurityFilter
public class OrchestratorACF extends AccessControlFilter {

  @Override
  public boolean isClientAuthorized(String clientCN, String method, String requestTarget, String requestJson) {
    if (!SecurityUtils.isKeyStoreCNArrowheadValid(clientCN)) {
      log.info(clientCN + " is not valid common name, access denied!");
      return false;
    }

    String serverCN = (String) configuration.getProperty("server_common_name");
    String[] serverFields = serverCN.split("\\.", 2);

    String[] clientFields = clientCN.split("\\.", 2);
    if (requestTarget.contains("mgmt")) {
      // Only the local System Operator can use these methods
      return clientCN.equalsIgnoreCase("sysop." + serverFields[1]);
    } else if (requestTarget.contains("store")) {
      // Only requests from the local cloud are allowed
      return serverFields[1].equalsIgnoreCase(clientFields[1]);
    } else {
      ServiceRequestForm srf = Utility.fromJson(requestJson, ServiceRequestForm.class);

      // If this is an external service request, only the local Gatekeeper can send this method
      if (srf.getOrchestrationFlags().get("externalServiceRequest")) {
        return clientFields[0].equalsIgnoreCase("gatekeeper") && serverFields[1].equalsIgnoreCase(clientFields[1]);
      } else {
        // Otherwise all request from the local cloud are allowed
        String consumerName = srf.getRequesterSystem().getSystemName().replaceAll("_", "");
        if (!consumerName.equalsIgnoreCase(clientFields[0])) {
          // BUT the requester system has to be the same as the first part of the common name
          log.error("Requester system name and cert common name do not match!");
          throw new AuthException(
              "Requester system " + srf.getRequesterSystem().getSystemName() + " and cert common name (" + clientCN + ") do not match!");
        }

        return serverFields[1].equalsIgnoreCase(clientFields[1]);
      }
    }
  }

}
