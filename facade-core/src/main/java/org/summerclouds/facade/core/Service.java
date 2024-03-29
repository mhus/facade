package org.summerclouds.facade.core;

import java.util.ArrayList;

import org.summerclouds.common.core.node.MProperties;
import org.summerclouds.common.core.yaml.YMap;

public class Service {

	private YMap def;
	private ArrayList<Field> parameters;
	private Field result;
	private String name;
	private MProperties properties;

	public Service(YMap def, String name, ArrayList<Field> parameters, Field result, MProperties local) {
		this.def = def;
		this.name = name;
		this.parameters = parameters;
		this.result = result;
		this.properties = new MProperties(local);
		if (name != null)
			properties.setString("_name", name);
	}

	public MProperties getProperties() {
		return properties;
	}
	
	public ArrayList<Field> getParameters() {
		return parameters;
	}

	public Field getResult() {
		return result;
	}
	
	public String getName() {
		return name;
	}

	public void dump() {
		System.out.println("  Parameters:");
		for (Field field : parameters)
			field.dump();
		System.out.println("  Result:"  );
		if (result == null)
			System.out.println("    void");
		else
			result.dump();
		System.out.println("  Properties:");
		properties.forEach((k,v) -> System.out.println("    " + k + "=" + v));
	}
	
	public YMap getDefinition() {
		return def;
	}

}
