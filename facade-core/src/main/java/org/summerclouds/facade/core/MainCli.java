package org.summerclouds.facade.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.summerclouds.common.core.error.MException;
import org.summerclouds.common.core.log.MLog;

public class MainCli extends MLog {

    public static void main(String[] args) throws Exception {

        if (args == null || (args.length != 1 && args.length != 3)) {
            System.out.println("Usage: <idl-generate.yaml>");
            System.out.println("Usage: <idl.yaml> <template dir> <target dir>");
            return;
        }
        
        if (args.length == 1)
        	new MainCli().executeController(args[0]);
        else
        if (args.length == 1)
        	new MainCli().executeTemplate(args[0], args[1], args[2]);
    }

	private void executeTemplate(String idl, String template, String target) throws IOException, MException {
		Model model = new Model();
		ModelBuilder builder = new ModelBuilder(new Source(), model);
		builder.load(new File(idl));
		builder.build();
		
		Generator gen = new GeneratorBuilder(model).load(new File(template)).build();
		gen.setTargetDir(new File(target));
		gen.generate();
		
	}

	private void executeController(String path) throws IOException, MException {
		List<Controller> controllers = new ControllerBuilder().load(new File(path)).build();
		for (Controller controller : controllers)
			controller.doExecute();
	}
    
}
