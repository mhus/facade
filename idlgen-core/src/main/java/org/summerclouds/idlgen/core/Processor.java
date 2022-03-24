package org.summerclouds.idlgen.core;

import java.io.File;
import java.io.IOException;

import org.summerclouds.common.core.node.MProperties;
import org.summerclouds.common.core.yaml.YMap;

public interface Processor {

	void process(File file, String template, MProperties prop);

	void init(File dir, YMap config) throws IOException;

}
