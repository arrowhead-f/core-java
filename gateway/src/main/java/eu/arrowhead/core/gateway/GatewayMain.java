/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.gateway;

import eu.arrowhead.common.ArrowheadMain;
import eu.arrowhead.common.misc.CoreSystem;
import eu.arrowhead.common.misc.SecurityUtils;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Set;
import javax.net.ssl.SSLContext;

public class GatewayMain extends ArrowheadMain {

  static int minPort;
  static int maxPort;
  static PrivateKey privateKey;
  static SSLContext clientContext;
  public static SSLContext serverContext;

  private GatewayMain(String[] args) {
    String[] packages = {"eu.arrowhead.common.exception", "eu.arrowhead.common.json", "eu.arrowhead.common.filter", "eu.arrowhead.core.gateway"};
    init(CoreSystem.GATEWAY, args, null, packages);

    minPort = props.getIntProperty("min_port", 8000);
    maxPort = props.getIntProperty("max_port", 8100);
    listenForInput();
  }

  public static void main(String[] args) {
    new GatewayMain(args);
  }

  @Override
  protected void startSecureServer(Set<Class<?>> classes, String[] packages) {
    String keystorePath = props.getProperty("keystore");
    String keystorePass = props.getProperty("keystorepass");
    String truststorePath = props.getProperty("truststore");
    String truststorePass = props.getProperty("truststorepass");
    String trustPass = props.getProperty("trustpass");
    String masterArrowheadCertPath = props.getProperty("master_arrowhead_cert");

    KeyStore gatewayKeyStore = SecurityUtils.loadKeyStore(keystorePath, keystorePass);
    privateKey = SecurityUtils.getPrivateKey(gatewayKeyStore, keystorePass);

    clientContext = SecurityUtils.createMasterSSLContext(truststorePath, truststorePass, trustPass, masterArrowheadCertPath);
    serverContext = SecurityUtils.createSSLContextWithDummyTrustManager(keystorePath, keystorePass);
    super.startSecureServer(classes, packages);
  }

}
