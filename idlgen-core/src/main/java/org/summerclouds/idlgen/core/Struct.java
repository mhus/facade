package org.summerclouds.idlgen.core;

import java.util.List;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.yaml.YMap;

public class Struct {

	private YMap def;
	private List<Field> fields;
	private String name;
	private MProperties properties;
	private boolean nested;

	public Struct(YMap def, String name, List<Field> fields, MProperties local, boolean nested) {
		this.def = def;
		this.fields = fields;
		this.name = name;
		this.nested = nested;
		this.properties = new MProperties(local);
		if (name != null)
			properties.setString("_name", name);
	}

	public MProperties getProperties() {
		return properties;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void dump() {
		System.out.println("  Fields:");
		for (Field field : fields)
			field.dump();
		System.out.println("  Properties:");
		properties.forEach((k,v) -> System.out.println("    " + k + "=" + v));
	}

	public YMap getDefinition() {
		return def;
	}

	public String getName() {
		return name;
	}

	public boolean isNested() {
		return nested;
	}

}
