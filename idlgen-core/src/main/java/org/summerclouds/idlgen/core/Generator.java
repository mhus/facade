package org.summerclouds.idlgen.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.summerclouds.common.core.error.MException;
import org.summerclouds.common.core.log.MLog;
import org.summerclouds.common.core.node.MProperties;
import org.summerclouds.common.core.parser.StringCompiler;

public class Generator extends MLog {

	private Model model;
	private String name;
	private ArrayList<Instruction> instructions = new ArrayList<Instruction>();
	private Processor processor = null;
	private Flavor flavor;
	private File targetDir;
	
	public Generator(Model model) {
		this.model = model;
	}
		
	public void generate() throws MException {
		for (Instruction inst : instructions) {
			switch (inst.getTarget()) {
			case GENERAL:
				generateGlobal(inst);
				break;
			case MODULE:
				generateModule(inst);
				break;
			case SERVICE:
				generateService(inst);
				break;
			case STRUCT:
				generateStruct(inst);
				break;
			default:
				break;
			
			}
		}
	}

	private void generateModule(Instruction inst) throws MException {
		for (String module : model.getModules()) {
			List<Struct> structs = model.getModuleStructs(module);
			List<Service> services = model.getModuleServices(module);
			
			// minimum one service or struct must be set
			MProperties properties = null;
			if (structs.size() > 0)
				properties = structs.get(0).getProperties();
			else
				properties = services.get(0).getProperties();
			
			MProperties prop = new MProperties(model.getProperties());
			prop.setString("_name", module);
			prop.put("_model", model);
			prop.put("_inst", inst.getDef());
			prop.put("_structs", structs);
			prop.put("_services", services);
			prop.put("_properties", properties);
			
			String path = StringCompiler.compile(inst.getPath()).execute(prop);
			File d = new File(targetDir, path);
			log().i("Create module",module,d);
			processor.process(d,inst.getTemplate(), prop);
		}
	}
	
	private void generateStruct(Instruction inst) throws MException {
		for (Struct struct : model.getStructs().values()) {
			String name = struct.getName();
			MProperties prop = new MProperties(model.getProperties());
			prop.putAll(struct.getProperties());
			prop.setString("_name", name);
			prop.put("_model", model);
			prop.put("_inst", inst.getDef());
			prop.put("_def", struct.getDefinition());
			prop.put("_fields", struct.getFields());
			prop.put("_struct", struct);
			prop.put("_object", struct);
			
			String path = StringCompiler.compile(inst.getPath()).execute(prop);
			File d = new File(targetDir, path);
			log().i("Create struct",d);
			processor.process(d,inst.getTemplate(), prop);
		}
	}

	private void generateService(Instruction inst) throws MException {
		for (Service service : model.getServices().values()) {
			String name = service.getName();
			MProperties prop = new MProperties(model.getProperties());
			prop.putAll(service.getProperties());
			prop.setString("_name", name);
			prop.put("_model", model);
			prop.put("_inst", inst.getDef());
			prop.put("_def", service.getDefinition());
			prop.put("_fields", service.getParameters());
			prop.put("_result", service.getResult());
			prop.put("_service", service);

			String path = StringCompiler.compile(inst.getPath()).execute(prop);
			File d = new File(targetDir, path);
			log().i("Create service",d);
			processor.process(d,inst.getTemplate(), prop);
		}
	}

	private void generateGlobal(Instruction inst) throws MException {
		MProperties prop = new MProperties(model.getProperties());
		prop.put("_model", model);
		prop.put("_inst", inst.getDef());
		
		String path = StringCompiler.compile(inst.getPath()).execute(prop);
		File d = new File(targetDir, path);
		log().i("Create global",d);
		processor.process(d,inst.getTemplate(), prop);
	}

	public void setName(String name) {
		this.name = name;
	}

	public Model getModel() {
		return model;
	}

	public void addInstruction(Instruction inst) {
		instructions.add(inst);
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public void setFlavor(Flavor flavor) {
		this.flavor = flavor;
	}

	public File getTargetDir() {
		return targetDir;
	}

	public void setTargetDir(File targetDir) {
		this.targetDir = targetDir;
	}
	
	public String getName() {
		return name;
	}

	public Flavor getFlavor() {
		return flavor;
	}
	
	
}
