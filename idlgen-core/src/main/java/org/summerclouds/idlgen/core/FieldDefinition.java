package org.summerclouds.idlgen.core;

import de.mhus.lib.core.yaml.YMap;

public class FieldDefinition {

	private String name;
	private String description;
	private YMap def;

	public FieldDefinition(YMap def, String name) {
		this.name = name;
		this.description = def.getString("format", null);
		this.def = def;
	}

	public void dump() {
		System.out.println("    " + name + ": " + description);
	}

	public String getName() {
		return name;
	}
	
	public YMap getDefinition() {
		return def;
	}

}
