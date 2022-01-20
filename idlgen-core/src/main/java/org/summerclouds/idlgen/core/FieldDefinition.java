package org.summerclouds.idlgen.core;

import de.mhus.lib.core.yaml.YMap;

public class FieldDefinition {

	private String name;
	private String description;

	public FieldDefinition(YMap defE, String name) {
		this.name = name;
		this.description = defE.getString("format", null);
	}

	public void dump() {
		System.out.println("    " + name + ": " + description);
	}

	public String getName() {
		return name;
	}

}
