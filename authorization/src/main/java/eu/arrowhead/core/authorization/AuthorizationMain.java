/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.authorization;

import eu.arrowhead.common.ArrowheadMain;
import eu.arrowhead.common.misc.CoreSystem;
import eu.arrowhead.common.misc.SecurityUtils;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

public class AuthorizationMain extends ArrowheadMain {

  public static boolean enableAuthForCloud;

  static PrivateKey privateKey;
  static PublicKey publicKey;

  private AuthorizationMain(String[] args) {
    KeyStore keyStore = SecurityUtils.loadKeyStore(props.getProperty("keystore"), props.getProperty("keystorepass"));
    privateKey = SecurityUtils.getPrivateKey(keyStore, props.getProperty("keystorepass"));
    publicKey = SecurityUtils.getFirstCertFromKeyStore(keyStore).getPublicKey();
    enableAuthForCloud = props.getBooleanProperty("enable_auth_for_cloud", false);

    String[] packages = {"eu.arrowhead.common", "eu.arrowhead.core.authorization"};
    init(CoreSystem.AUTHORIZATION, args, null, packages);

    listenForInput();
  }

  public static void main(String[] args) {
    new AuthorizationMain(args);
  }
}
