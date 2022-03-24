package org.summerclouds.idlgen.core;

import org.summerclouds.common.core.error.UsageException;

public class FlavorFactory {

	public static Flavor create(String name) {
		name = name.trim().toLowerCase();
		if ("java".equals(name))
			return new JavaFlavor();
		throw new UsageException("flovor unknown",name);
	}

}
