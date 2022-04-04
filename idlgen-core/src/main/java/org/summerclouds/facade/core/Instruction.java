package org.summerclouds.facade.core;

import org.summerclouds.common.core.yaml.YMap;

public class Instruction {

	public enum TARGET {GENERAL,MODULE,STRUCT,SERVICE}
	private YMap def;
	private TARGET target;
	private String path;
	private String template;

	public Instruction(YMap def, TARGET target, String path, String template) {
		this.def = def;
		this.target = target;
		this.path =path;
		this.template = template;
	}

	public YMap getDef() {
		return def;
	}

	public TARGET getTarget() {
		return target;
	}

	public String getPath() {
		return path;
	}

	public String getTemplate() {
		return template;
	}

}
