package org.summerclouds.idlgen.core;

import java.io.File;
import java.io.IOException;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.yaml.YMap;

public interface Processor {

	void process(File file, String template, MProperties prop);

	void init(File dir, YMap config) throws IOException;

}
