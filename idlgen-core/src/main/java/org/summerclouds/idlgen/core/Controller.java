package org.summerclouds.idlgen.core;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.summerclouds.common.core.node.IProperties;
import org.summerclouds.common.core.node.MProperties;

public class Controller {

	private MProperties properties = new MProperties();
	private File dir;
	private String templete;
	private Set<String> exclude = new HashSet<>();
	private Map<String, IProperties> include = new HashMap<>();
	private String model;
	private String target;
	
	public void setDir(File dir) {
		this.dir = dir;
	}

	public void setTemplate(String template) {
		this.templete = template;
	}

	public MProperties getProperties() {
		return properties;
	}

	public void addElementExclude(String name) {
		exclude.add(name);
		
	}

	public void addElementInclude(String key, IProperties config) {
		include.put(key, config);
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
