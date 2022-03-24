package org.summerclouds.idlgen.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.summerclouds.common.core.node.MProperties;

public class Model {
	
	private ArrayList<Definition> definitions = new ArrayList<Definition>();
	private Definition definition = new Definition();
	private MProperties properties = new MProperties();
	private TreeMap<String, Service> services = new TreeMap<>();
	private TreeMap<String, Struct> structs = new TreeMap<>();
	
	public Definition getDefinition() {
		return definition;
	}

	public void addDefinition(Definition def) {
		definitions.add(def);
		for (FieldDefinition value : def.getFieldTypes().values())
			definition.addFieldType(value);
		for (Map.Entry<String, String> value : def.getAliases().entrySet())
			definition.addAlias(value.getKey(), value.getValue());
	}

	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	public void addService(String name, Service service) {
		services.put(name, service);
	}
	
	public void addStruct(String name, Struct struct) {
		structs.put(name, struct);
	}

	public void dump() {
		
		System.out.println("========= Properties ==============");
		properties.forEach((k,v) -> System.out.println(k + "=" + v));
		System.out.println("========= Definitions =============");
		for (Definition def : definitions)
			def.dump();
		System.out.println("========= Definition =============");
		definition.dump();
		System.out.println("========= Structures ==============");
		for (Entry<String, Struct> struct : structs.entrySet()) {
			System.out.println(">>> " + struct.getKey());
			struct.getValue().dump();
		}
		System.out.println("========= Services ================");
		for (Entry<String, Service> service : services.entrySet()) {
			System.out.println(">>> " + service.getKey());
			service.getValue().dump();
		}
		System.out.println("========= Modules =================");
		for (String module : getModules()) {
			System.out.println(">>> " + module);
			for (Struct struct : getModuleStructs(module)) {
				System.out.println("--- Struct: " + struct.getName());
//				struct.dump();
			}
			for (Service service : getModuleServices(module)) {
				System.out.println("--- Service: " + service.getName());
//				service.dump();
			}
		}
	}

	public Map<String, Struct> getStructs() {
		return structs;
	}

	public String getAliasFor(String type) {
		return definition.getAliases().get(type);
	}

	public Map<String, Service> getServices() {
		return services;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public MProperties getProperties() {
		return properties;
	}

	public Set<String> getModules() {
		TreeSet<String> set = new TreeSet<>();
		for (Entry<String, Struct> struct : structs.entrySet()) {
			String module = struct.getValue().getProperties().getString("module", null);
			if (module != null)
				set.add(module);
		}
		for (Entry<String, Service> service : services.entrySet()) {
			String module = service.getValue().getProperties().getString("module", null);
			if (module != null)
				set.add(module);
		}		
		return set;
	}
	
	public List<Struct> getModuleStructs(String name) {
		ArrayList<Struct> list = new ArrayList<>();
		for (Entry<String, Struct> struct : structs.entrySet()) {
			String module = struct.getValue().getProperties().getString("module", null);
			if (module != null && module.equals(name))
				list.add(struct.getValue());
		}
		return list;
	}

	public List<Service> getModuleServices(String name) {
		ArrayList<Service> list = new ArrayList<>();
		for (Entry<String, Service> service : services.entrySet()) {
			String module = service.getValue().getProperties().getString("module", null);
			if (module != null && module.equals(name))
				list.add(service.getValue());
		}
		return list;
	}

}
