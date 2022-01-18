package org.summerclouds.idlgen.core;

import java.util.List;

import de.mhus.lib.core.yaml.YMap;

public class Struct {

	private YMap def;
	private List<Field> fields;
	private String name;

	public Struct(YMap def, String name, List<Field> fields) {
		this.def = def;
		this.fields = fields;
		this.name = name;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void dump() {
		System.out.println("  Fields:");
		for (Field field : fields)
			field.dump();
	}

	public YMap getDefinition() {
		return def;
	}

	public String getName() {
		return name;
	}

}
