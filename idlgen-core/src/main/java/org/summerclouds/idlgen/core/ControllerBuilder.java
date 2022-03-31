package org.summerclouds.idlgen.core;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.summerclouds.common.core.node.MProperties;
import org.summerclouds.common.core.tool.MFile;
import org.summerclouds.common.core.yaml.MYaml;
import org.summerclouds.common.core.yaml.YElement;
import org.summerclouds.common.core.yaml.YList;
import org.summerclouds.common.core.yaml.YMap;

public class ControllerBuilder {
	
	private List<Controller> controllers = new LinkedList<>();
	
	public ControllerBuilder load(File config) throws IOException {
		if (config.isDirectory())
			config = new File(config, "idl-generate.yaml");
		File dir = config.getParentFile();

		String content = MFile.readFile(config);
		YMap fileE = MYaml.loadMapFromString(content);

		MProperties global = new MProperties();
		YMap globalE = fileE.getMap("global");
		if (globalE != null && globalE.isMap()) {
			for (String key : globalE.getKeys()) {
				Object value = globalE.getObject(key);
				global.put(key, value);
			}
		}
		
		YList listE = fileE.getList("generate");

		for (YElement configE : listE) {
			YMap mapE = configE.asMap();
			Controller controller = new Controller();
			controller.setDir(dir);
			
			String template = mapE.getString("template");
			controller.setTemplate(template);
			
			String model = mapE.getString("model");
			controller.setModel(model);
			
			String target = mapE.getString("target");
			controller.setTarget(target);
			
			YMap propertiesE = mapE.getMap("properties");
			controller.getProperties().putAll(global);
			if (propertiesE != null && propertiesE.isMap()) {
				for (String key : propertiesE.getKeys()) {
					Object value = propertiesE.getObject(key);
					controller.getProperties().put(key, value);
				}
			}
			
			YMap filterE = mapE.getMap("filter");
			if (filterE != null && filterE.isMap()) {
				for (String key : filterE.getKeys()) {
					YMap filterConfigE = filterE.getMap(key);
					MProperties filterConfig = toProperties(filterConfigE);
					if (filterConfig.getBoolean("exclude", false))
						controller.addElementExclude(key);
					else
						controller.addElementInclude(key, filterConfig);
						
				}
			}
			
			controllers.add(controller);
		}
		
		
		return this;
	}
	
	private MProperties toProperties(YMap filterE) {
		MProperties out = new MProperties();
		if (filterE != null && filterE.isMap()) {
			for (String key : filterE.getKeys()) {
				Object value = filterE.getObject(key);
				out.put(key, value);
			}
		}
		return out;
	}

	public List<Controller> build() {
		
		return controllers;
	}

}
