/*
 *  Copyright (c) 2018 Jens Eliasson, Luleå University of Technology
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.core.datamanager;

import eu.arrowhead.common.DatabaseManager;
import eu.arrowhead.common.Utility;
import eu.arrowhead.common.database.ArrowheadSystem;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.messages.SenMLMessage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.apache.log4j.Logger;

final class ProxyService {
  private static final Logger log = Logger.getLogger(DataManagerResource.class.getName());
  private static List<ProxyElement> endpoints = new ArrayList<>();



  /**
   * @fn static List<ProxyElement> getAllEndpoints()
   *
   */
  static List<String> getAllEndpoints() {
    List<String> res = new ArrayList<>();
    Iterator<ProxyElement> epi = endpoints.iterator();

    while (epi.hasNext()) {
      ProxyElement pe = epi.next();
      res.add(pe.systemName);
    }
    return res;
  }


  /**
   * @fn static List<ProxyElement> getEndpoints(String systemName)
   *
   */
  static ArrayList<ProxyElement> getEndpoints(String systemName) {
    ArrayList<ProxyElement> res = new ArrayList<>();
    Iterator<ProxyElement> epi = endpoints.iterator();

    while (epi.hasNext()) {
      ProxyElement pe = epi.next();
      if (systemName.equals(pe.systemName)) {
	System.out.println("Found endpoint: " + pe.serviceName);
        res.add(pe);
      }
    }
    return res;
  }


  /**
   * @fn static ProxyElement getEndpoint(String serviceName)
   *
   */
  static ProxyElement getEndpoint(String serviceName) {
    Iterator<ProxyElement> epi = endpoints.iterator();

    while (epi.hasNext()) {
      ProxyElement curpe = epi.next();
      System.out.println("Found endpoint: " + curpe.serviceName);
      if (serviceName.equals(curpe.serviceName)) {
        return curpe;
      }
    }

    return null;
  }


  /**
   * @fn static boolean updateEndpoint(String serviceName, Vector<SenMLMessage> msg)
   * @brief
   *
   */
  static boolean updateEndpoint(String serviceName, Vector<SenMLMessage> msg) {
    Iterator<ProxyElement> epi = endpoints.iterator();

    while (epi.hasNext()) {
      ProxyElement pe = epi.next();
      if (serviceName.equals(pe.serviceName)) {
	System.out.println("Found endpoint: " + pe.serviceName);
	pe.msg = msg; //.get(0);
	System.out.println("Updating with: " + msg.toString());
        return true;
      }
    }
    return false;
  }


  /**
   * @fn static SenMLMessage fetchEndpoint(String serviceName)
   * @brief
   *
   */
  static SenMLMessage fetchEndpoint(String serviceName) {
    Iterator<ProxyElement> epi = endpoints.iterator();

    while (epi.hasNext()) {
      ProxyElement pe = epi.next();
      if (serviceName.equals(pe.serviceName)) {
	System.out.println("Found endpoint: " + pe.serviceName);
        return null; //pe.msg;
      }
    }
    System.out.println("Endpoint: " + serviceName + " not found");
    return null;
  }


  /**
   * @fn static boolean addEndpoint(ProxyElement e)
   * @brief
   *
   */
  static boolean addEndpoint(ProxyElement e) {
    for(ProxyElement tmp: endpoints) {
      if (tmp.serviceName.equals(e.serviceName)) // already exists
        return false;
    }
    endpoints.add(e);
    return true;
  }


  /**
   * @fn static boolean deleteEndpoint(String serviceName)
   * @brief
   *
   */
  static boolean deleteEndpoint(String serviceName) { //XXX: do not support this now right now
    return false;
  }

}
