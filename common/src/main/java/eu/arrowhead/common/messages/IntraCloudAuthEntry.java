/*
 * Copyright (c) 2018 AITIA International Inc.
 *
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.common.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.arrowhead.common.database.ArrowheadService;
import eu.arrowhead.common.database.ArrowheadSystem;
import java.util.ArrayList;

public class IntraCloudAuthEntry {

  private ArrowheadSystem consumer;
  private ArrayList<ArrowheadSystem> providerList = new ArrayList<>();
  private ArrayList<ArrowheadService> serviceList = new ArrayList<>();

  public IntraCloudAuthEntry() {
  }

  public IntraCloudAuthEntry(ArrowheadSystem consumer, ArrayList<ArrowheadSystem> providerList, ArrayList<ArrowheadService> serviceList) {
    this.consumer = consumer;
    this.providerList = providerList;
    this.serviceList = serviceList;
  }

  public ArrowheadSystem getConsumer() {
    return consumer;
  }

  public void setConsumer(ArrowheadSystem consumer) {
    this.consumer = consumer;
  }

  public ArrayList<ArrowheadSystem> getProviderList() {
    return providerList;
  }

  public void setProviderList(ArrayList<ArrowheadSystem> providerList) {
    this.providerList = providerList;
  }

  public ArrayList<ArrowheadService> getServiceList() {
    return serviceList;
  }

  public void setServiceList(ArrayList<ArrowheadService> serviceList) {
    this.serviceList = serviceList;
  }

  @JsonIgnore
  public boolean isValid() {
    if (consumer == null || serviceList.isEmpty() || providerList.isEmpty() || !consumer.isValidForDatabase()) {
      return false;
    }
    for (ArrowheadSystem provider : providerList) {
      if (!provider.isValidForDatabase()) {
        return false;
      }
    }
    for (ArrowheadService service : serviceList) {
      if (!service.isValidForDatabase()) {
        return false;
      }
    }
    return true;
  }

}