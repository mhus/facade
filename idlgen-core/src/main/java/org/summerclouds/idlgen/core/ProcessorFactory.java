package org.summerclouds.idlgen.core;

import de.mhus.lib.errors.UsageException;

public class ProcessorFactory {

	public static Processor create(String name) {
		name = name.trim().toLowerCase();
		if ("fm".equals(name) || "freemarker".equals(name))
			return new FreeMarkerProcessor();
		if ("dummy".equals(name))
			return new DummyProcessor();
		throw new UsageException("unknown processor",name);
	}

}
