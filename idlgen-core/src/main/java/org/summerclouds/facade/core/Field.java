package org.summerclouds.facade.core;

import org.summerclouds.common.core.node.MProperties;
import org.summerclouds.common.core.yaml.YMap;

public class Field {
	public enum SEQUENCE {SINGLE, ARRAY, MAP}

	private String name;
	private String type;
	private SEQUENCE sequence;
	private YMap def;
	private String alias;
	private Struct struct;
	private FieldDefinition fieldDefinition;
	private MProperties properties = new MProperties();

	public Field(YMap def, String name, String type, SEQUENCE sequence) {
		this.def = def;
		this.name = name;
		this.type = type;
		this.sequence = sequence;
		if (name != null)
			properties.setString("_name", name);
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public SEQUENCE getSequence() {
		return sequence;
	}

	public void dump() {
		System.out.println("    " + name + " : " + sequence + " of " + type + " " + (struct == null ? "" : "(" + struct.getName() + ")"  ) );
	}
	
	public String getAlias() {
		return alias;
	}

	public void doAlias(String org) {
		if (alias != null) return;
		alias = type;
		type = org;
	}

	public void setStruct(Struct struct) {
		this.struct = struct;
	}
	
	public Struct getStruct() {
		return struct;
	}
	
	public YMap getDefinition() {
		return def;
	}

	public FieldDefinition getFieldDefinition() {
		return fieldDefinition;
	}

	public void setFieldDefinition(FieldDefinition field) {
		this.fieldDefinition = field;
	}

	public boolean isField() {
		return fieldDefinition != null;
	}
	
	public boolean isStruct() {
		return struct != null;
	}

	public MProperties getProperties() {
		return properties;
	}
	
}
