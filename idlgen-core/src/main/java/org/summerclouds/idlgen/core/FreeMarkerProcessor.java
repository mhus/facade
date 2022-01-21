package org.summerclouds.idlgen.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.yaml.YMap;
import de.mhus.lib.errors.MRuntimeException;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.Version;

public class FreeMarkerProcessor implements Processor {

	private Configuration cfg;

	@Override
	public void process(File file, String template, MProperties prop) {
		
		file.getParentFile().mkdirs();

        try {
			Template ftl = cfg.getTemplate(template);
			Writer fileWriter = new FileWriter(file);
	        try {
	            ftl.process(prop, fileWriter);
	        } finally {
	            fileWriter.close();
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
			throw new MRuntimeException(e);
		}

	}

	@Override
	public void init(File dir, YMap config) throws IOException {
		cfg = new Configuration(Configuration.VERSION_2_3_28);
		cfg.setDefaultEncoding("UTF-8");
		TemplateLoader loader = new FileTemplateLoader(dir);
		cfg.setTemplateLoader(loader);
		cfg.setLocale(Locale.US);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		
		BeansWrapper wrapper = new BeansWrapper(new Version(2,3,27));
		TemplateModel statics = wrapper.getStaticModels();
		
		Map<String, Object> sharedVariables = new HashMap<>();
		sharedVariables.put("statics", statics);
		try {
			cfg.setSharedVaribles(sharedVariables);
		} catch (TemplateModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
