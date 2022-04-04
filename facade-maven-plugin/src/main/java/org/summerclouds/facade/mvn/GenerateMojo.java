package org.summerclouds.facade.mvn;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.summerclouds.facade.core.Controller;
import org.summerclouds.facade.core.ControllerBuilder;
import org.summerclouds.facade.core.Generator;
import org.summerclouds.facade.core.GeneratorBuilder;
import org.summerclouds.facade.core.Model;
import org.summerclouds.facade.core.ModelBuilder;
import org.summerclouds.facade.core.Source;

@Mojo(
	name = "generate",
	defaultPhase = LifecyclePhase.GENERATE_SOURCES,
	requiresDependencyResolution = ResolutionScope.COMPILE,
	requiresProject = true, 
	threadSafe = true,
	inheritByDefault = false
)

public class GenerateMojo extends AbstractMojo {

	@Parameter(required = false)
	protected File controller;
	
	@Parameter(required = false)
	protected File template;
	
	@Parameter(required = false)
	protected File idl;

	@Parameter(defaultValue = "${project.build.directory}/generated-sources/facade")
	protected File target;
	
	@Parameter
	protected Map<String, String> parameters;
	
	@Parameter
	protected Map<String, String> global;
	
	
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		if (!target.exists()) {
            if (!target.mkdirs())
            	throw new MojoExecutionException("Can't create target directory: " + target);
        }
		
		if (controller != null) {
			try {
				List<Controller> controllers = new ControllerBuilder().load(controller).build();
				for (Controller controller : controllers)
					controller.doExecute();
			} catch (Throwable t) {
				throw new MojoExecutionException("can't execute controller: " + controller, t);
			}
		} else
		if (template != null && idl != null) {
			try {
				Model model = new Model();
				ModelBuilder builder = new ModelBuilder(new Source(), model);
				builder.load(idl);
				builder.build();
				
				Generator gen = new GeneratorBuilder(model).load(template).build();
				gen.setTargetDir(target);
				gen.generate();
			} catch (Throwable t) {
				throw new MojoExecutionException("can't execute idl: " + idl + " with template: " + template, t);
			}
		} else {
			getLog().error("controller or template and idl must be set to generated sources");
			throw new MojoExecutionException("controller or template and idl must be set to generated sources");
		}
		
		
	}

}
