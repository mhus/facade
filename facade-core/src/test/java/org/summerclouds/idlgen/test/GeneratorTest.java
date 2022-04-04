package org.summerclouds.idlgen.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.summerclouds.common.core.error.MException;
import org.summerclouds.common.junit.TestCase;
import org.summerclouds.common.junit.TestUtil;
import org.summerclouds.facade.core.Controller;
import org.summerclouds.facade.core.ControllerBuilder;
import org.summerclouds.facade.core.Generator;
import org.summerclouds.facade.core.GeneratorBuilder;
import org.summerclouds.facade.core.Model;
import org.summerclouds.facade.core.ModelBuilder;
import org.summerclouds.facade.core.Source;

public class GeneratorTest extends TestCase {

	@Test
	public void test1() throws MException, IOException {
		Model model = new Model();
		ModelBuilder builder = new ModelBuilder(new Source(), model);
		builder.load(new File("examples/config1/idl.yaml"));
		builder.build();
		
		
		Generator gen = new GeneratorBuilder(model).load(new File("examples/templates1")).build();
		model.dump();
		gen.setTargetDir(new File("target/generated-test-sources"));
		gen.generate();
		
	}

	//@Test
	public void test2() throws Exception {
		Model model = new Model();
		ModelBuilder builder = new ModelBuilder(new Source(), model);
		builder.load(new File("examples/config1/idl.yaml"));
		builder.build();
		
//		model.dump();
		
		Generator gen = new GeneratorBuilder(model).load(new File("examples/templates2")).build();
		File target = new File("target/generated-test-sources2");
		gen.setTargetDir(target);
		gen.generate();
		
		TestUtil.recordOrValidateDirectory(target, new File("records/test2.properties") );
		
	}

	@Test
	public void test3() throws Exception {
		Model model = new Model();
		ModelBuilder builder = new ModelBuilder(new Source(), model);
		builder.load(new File("examples/config3/idl.yaml"));
		builder.build();
		
//		model.dump();
		
		Generator gen = new GeneratorBuilder(model).load(new File("examples/templates3")).build();
		File target = new File("target/generated-test-sources3");
		gen.setTargetDir(target);
		gen.generate();
		
		TestUtil.recordOrValidateDirectory(target, new File("records/test3.properties") );

	}

	@Test
	public void testController() throws Exception {
		List<Controller> controllers = new ControllerBuilder().load(new File("examples/idl-controller.yaml")).build();
		for (Controller controller : controllers)
			controller.doExecute();
		
		File target = new File("target/generated-test-sources4");
		TestUtil.recordOrValidateDirectory(target, new File("records/test4.properties") );

	}

}
