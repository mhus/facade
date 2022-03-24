package org.summerclouds.idlgen.core;

import java.io.File;

import org.summerclouds.common.core.log.MLog;
import org.summerclouds.common.core.node.MProperties;
import org.summerclouds.common.core.yaml.YMap;

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
