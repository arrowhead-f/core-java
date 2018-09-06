/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.core.systemregistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import eu.arrowhead.common.ArrowheadMain;
import eu.arrowhead.common.misc.CoreSystem;

public class SystemRegistryMain extends ArrowheadMain {
	private SystemRegistryMain(String[] args) {
		Set<Class<?>> classes = new HashSet<>(Arrays.asList(SystemRegistryResource.class, SystemRegistryApi.class));
		String[] packages = {
				"eu.arrowhead.common.exception", 
				"eu.arrowhead.common.json", 
				"eu.arrowhead.common.filter", 
				"eu.arrowhead.systemregistry", 
				"eu.arrowhead.systemregistry.model", 
				"eu.arrowhead.systemregistry.filter",
				"io.swagger.jaxrs2.integration.resources"};
		init(CoreSystem.SYSTEMREGISTRY, args, classes, packages);
		
		listenForInput();
	}

	public static void main(String[] args) {
		new SystemRegistryMain(args);
	}
}
