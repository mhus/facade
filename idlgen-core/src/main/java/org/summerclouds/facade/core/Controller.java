package org.summerclouds.facade.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.summerclouds.common.core.error.MException;
import org.summerclouds.common.core.log.MLog;
import org.summerclouds.common.core.node.IProperties;
import org.summerclouds.common.core.node.MProperties;
import org.summerclouds.common.core.util.MUri;

public class Controller extends MLog {

	private MProperties properties = new MProperties();
	private MUri templateUri;
	private Set<String> exclude = new HashSet<>();
	private Map<String, IProperties> include = new HashMap<>();
	private MUri modelUri;
	private String target;
	private Source source;

	public Controller(Source source) {
		this.source = source;
	}
	
	public void setDir(File dir) {
		source.getSchemes().setParent(dir);
	}

	public void setTemplateUri(MUri template) {
		this.templateUri = template;
	}

	public MProperties getProperties() {
		return properties;
	}

	public void addElementExclude(String name) {
		exclude.add(name);
		
	}

	public void addElementInclude(String key, IProperties config) {
		include.put(key, config);
	}

	public void setModelUri(MUri modelUri) {
		this.modelUri = modelUri;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void doExecute() throws MException, IOException {
		log().i("execute",modelUri,templateUri,target);
		// load model
		Model model = new Model();
		ModelBuilder builder = new ModelBuilder(source, model);
		
		// add include filter
		include.forEach((k,v) -> builder.addFilter(k) );
		
		builder.load( source.getSchemes()
				.get(modelUri)
				.load(modelUri) );
		builder.build();

		// exclude filter
		exclude.forEach((k) -> {
			if (k.startsWith("struct:"))
				model.getStructs().remove(k.substring(7));
			else
			if (k.startsWith("service:"))
				model.getServices().remove(k.substring(8));
		});
		
		// create template
		Generator gen = new GeneratorBuilder(model)
				.load( source
						.getSchemes()
						.get(templateUri)
						.load(templateUri) )
				.build();
		gen.setTargetDir(new File(source.getSchemes().getParent(), target));
		gen.generate();
		
	}

}
