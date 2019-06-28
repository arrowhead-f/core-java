/*
 *  Copyright (c) 2018 Jens Eliasson, Luleå University of Technology
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.core.datamanager;

import java.util.Vector;
import eu.arrowhead.common.messages.SenMLMessage;

public class ProxyElement {

  public String systemName = null;     // i.e. tempSys-1._Audon-Thermometer−01._http._tcp._arrowhead.eu
  public String serviceName = null;           // i.e "_tempService1._tempSys-1. http._tcp._arrowhead.eu:8000"
  public String serviceType = null;    // _Tempreatre._http._tcp. etc...
  public Vector<SenMLMessage> msg = null;

  public ProxyElement(String systemName, String serviceName) {
    this.systemName = new String(systemName);
    this.serviceName = new String(serviceName);
    this.msg = null;
  }


  /**
   * @fn public ProxyElement(String name, Vector<SenMLMessage> senml)
   * @brief creates a new ProxyElement from a SenML message
   */
  public ProxyElement(String serviceName, Vector<SenMLMessage> senml) {
    this.serviceName = new String(serviceName);
    this.msg = senml;
  }


}

