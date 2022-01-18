package org.summerclouds.idlgen.core;

import java.io.File;

import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.yaml.YMap;

public class DummyProcessor extends MLog implements Processor {

	@Override
	public void process(File file, String template, MProperties prop) {
		log().i("process",file,template,prop);
	}

	@Override
	public void init(File dir, YMap config) {
		log().i("init",dir,config);
	}

}
