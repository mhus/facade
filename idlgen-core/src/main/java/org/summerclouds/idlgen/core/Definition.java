package org.summerclouds.idlgen.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.mhus.lib.core.util.Version;

public class Definition {
	
	private String name;
	private Version version;
	private HashSet<String> fieldTypes = new HashSet<>();
	private HashMap<String,String> aliases = new HashMap<>();
	private HashMap<String,String> formats = new HashMap<>();
	
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
	public void addFieldType(String fieldType) {
		fieldTypes.add(fieldType);
	}
	public void addAlias(String from, String to) {
		aliases.put(from, to);
	}
	public void addFormat(String field, String format) {
		formats.put(field, format);
	}

	public Set<String> getFieldTypes() {
		return fieldTypes;
	}
	
	public Map<String,String> getAliases() {
		return aliases;
	}

	public Map<String,String> getFormats() {
		return formats;
	}
	public void dump() {
		System.out.println("  Name: " + name);
		System.out.println("  Version: " + version);
		System.out.println("  Types:");
		fieldTypes.forEach(f -> System.out.println("    " + f));
		System.out.println("  Aliases: ");
		aliases.forEach((k,v) -> System.out.println("    " + k + "=" + v));
		System.out.println("  Formats:");
		formats.forEach((k,v) -> System.out.println("    " + k + "=" + v));
	}
	
}
