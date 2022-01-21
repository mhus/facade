package org.summerclouds.idlgen.core;

import java.io.File;
import java.io.IOException;

import org.summerclouds.idlgen.core.Instruction.TARGET;

import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.util.VersionRange;
import de.mhus.lib.core.yaml.MYaml;
import de.mhus.lib.core.yaml.YList;
import de.mhus.lib.core.yaml.YMap;
import de.mhus.lib.errors.UsageException;

public class GeneratorBuilder {

	private Generator gen;

	public GeneratorBuilder(Model model) {
		gen = new Generator(model);
	}
	
	public GeneratorBuilder(Generator gen) {
		this.gen = gen;
	}
	
	public GeneratorBuilder load(File config) throws IOException {
		if (config.isDirectory())
			config = new File(config, "idl-template.yaml");
		File dir = config.getParentFile();
		String content = MFile.readFile(config);
		YMap configE = MYaml.loadMapFromString(content);
		String name = configE.getString("name", "");
		gen.setName(name);
		String processorName  = configE.getString("processor", "fm");
		Processor processor = ProcessorFactory.create(processorName);
		YMap processorConfig = configE.getMap("processorConfiguration");
		if (processorConfig == null) processorConfig = new YMap(null);
		processor.init(dir, configE);
		gen.setProcessor(processor);
		String flavorName = configE.getString("flavor", "java");
		Flavor flavor = FlavorFactory.create(flavorName);
		gen.setFlavor(flavor);
		
		// definitions
		YList defE = configE.getList("definitions");
		if (defE != null) {
			for (String defTest : defE.toStringList()) {
				defTest = defTest.trim();
				VersionRange version = null;
				if (MString.isIndex(defTest, ' ')) {
					version = new VersionRange(MString.afterIndex(defTest, ' ').trim());
					defTest = MString.beforeIndex(defTest, ' ').trim();
				}
				boolean found = false;
				for (Definition def : gen.getModel().getDefinitions()) {
					if (def.getName().equals(defTest)) {
						if (version == null)
							found = true;
						else
						if (version.includes(def.getVersion()))
							found = true;
					}
				}
				if (!found)
					throw new UsageException("required definition not found",defTest,version);
			}
		}
		
		// properties
		YMap propertiesE = configE.getMap("properties");
		if (propertiesE != null && propertiesE.isMap()) {
			for (String key : propertiesE.getKeys()) {
				Object value = propertiesE.getObject(key);
				// do not overwrite !
				if (!gen.getModel().getProperties().containsKey(key))
					gen.getModel().getProperties().put(key, value);
			}
		}
		
		// generate
		YList instE = configE.getList("instructions");
		if (instE != null) {
			for (YMap map : instE.toMapList()) {
				Instruction.TARGET target = toTarget(map.getString("target", ""));
				String path = map.getString("path");
				String template = map.getString("template");
				Instruction inst = new Instruction(map,target,path,template);
				gen.addInstruction(inst);
			}
		}
		
		return this;
	}

	public Generator build() {
		
		// flavor
		for (Struct struct : gen.getModel().getStructs().values()) {
			gen.getFlavor().prepare( struct.getProperties() );
			for (Field f : struct.getFields())
				gen.getFlavor().prepare( f.getProperties() );
		}
		for (Service service : gen.getModel().getServices().values()) {
			gen.getFlavor().prepare( service.getProperties() );
			Field res = service.getResult();
			if (res != null)
				gen.getFlavor().prepare( res.getProperties() );
			for (Field p : service.getParameters())
				gen.getFlavor().prepare( p.getProperties() );
		}
		gen.getFlavor().prepare( gen.getModel().getProperties() );
		
		return gen;
	}
	
	private TARGET toTarget(String val) {
		val = val.trim().toLowerCase();
		if ("general".equals(val))
			return TARGET.GENERAL;
		if ("struct".equals(val))
			return TARGET.STRUCT;
		if ("service".equals(val))
			return TARGET.SERVICE;
		if ("module".equals(val))
			return TARGET.MODULE;
		throw new UsageException("unknown target",val);
	}

//	public Generator generator() {
//		return gen;
//	}

	
	
	
}
