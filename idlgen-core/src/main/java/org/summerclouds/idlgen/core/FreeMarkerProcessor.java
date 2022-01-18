package org.summerclouds.idlgen.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.yaml.YMap;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

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
		
	}

}
