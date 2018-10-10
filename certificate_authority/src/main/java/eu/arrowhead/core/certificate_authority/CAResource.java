/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.certificate_authority;

import eu.arrowhead.common.exception.DataNotFoundException;
import eu.arrowhead.core.certificate_authority.model.CertificateSigningRequest;
import eu.arrowhead.core.certificate_authority.model.CertificateSigningResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("ca")
@Consumes(MediaType.APPLICATION_JSON)
public class CAResource {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getCloudCommonName() {
    return CAMain.cloudCN;
  }

  @GET
  @Path("auth")
  @Produces(MediaType.TEXT_PLAIN)
  public String getAuthPublicKey() {
    if (CAMain.encodedAuthPublicKey != null) {
      return CAMain.encodedAuthPublicKey;
    } else {
      throw new DataNotFoundException("Authorization public key could not be acquired (yet) from the Authorization Core System.");
    }
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public CertificateSigningResponse getSignedCertificate(CertificateSigningRequest request) {
    return CAService.signCertificate(request);
  }

}
