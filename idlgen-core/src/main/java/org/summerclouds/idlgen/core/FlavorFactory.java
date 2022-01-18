package org.summerclouds.idlgen.core;

import de.mhus.lib.errors.UsageException;

public class FlavorFactory {

	public static Flavor create(String name) {
		name = name.trim().toLowerCase();
		if ("java".equals(name))
			return new JavaFlavor();
		throw new UsageException("flovor unknown",name);
	}

}
