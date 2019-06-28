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
import eu.arrowhead.common.messages.SigMLMessage;
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
  //private static final DatabaseManager dm = DatabaseManager.getInstance();

  private static List<ProxyElement> endpoints = new ArrayList<>();

  static List<ProxyElement> getEndpoints(String systemName) {
    List<ProxyElement> res = new ArrayList<>();
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

  static boolean updateEndpoint(String serviceName, SigMLMessage msg) {
    Iterator<ProxyElement> epi = endpoints.iterator();

    while (epi.hasNext()) {
      ProxyElement pe = epi.next();
      if (serviceName.equals(pe.serviceName)) {
	System.out.println("Found endpoint: " + pe.serviceName);
	pe.msg = msg.e;
	System.out.println("Updating with: " + msg.e.toString());
        return true;
      }
    }
    return false;
  }

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

  static boolean addEndpoint(ProxyElement e) {
    endpoints.add(e);
    return true;
  }

  static boolean deleteEndpoint(String serviceName) { //XXX: do not support this now right now
    return false;
  }


  
}
