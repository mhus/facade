package org.summerclouds.idlgen.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.mhus.lib.core.util.Version;

public class Definition {
	
	private String name;
	private Version version;
	private HashMap<String,FieldDefinition> fieldTypes = new HashMap<>();
	private HashMap<String,String> aliases = new HashMap<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Version getVersion() {
		return version;
	}
	public void setVersion(Version version) {
		this.version = version;
	}
	public void addFieldType(FieldDefinition definition) {
		fieldTypes.put(definition.getName(), definition);
	}
	public void addAlias(String from, String to) {
		aliases.put(from, to);
	}

	public Map<String,FieldDefinition> getFieldTypes() {
		return fieldTypes;
	}
	
	public Map<String,String> getAliases() {
		return aliases;
	}

	public void dump() {
		System.out.println("  Name: " + name);
		System.out.println("  Version: " + version);
		System.out.println("  Types:");
		fieldTypes.forEach((k,v) -> v.dump());
		System.out.println("  Aliases: ");
		aliases.forEach((k,v) -> System.out.println("    " + k + "=" + v));
	}
	
}
