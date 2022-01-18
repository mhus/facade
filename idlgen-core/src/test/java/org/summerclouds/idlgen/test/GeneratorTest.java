package org.summerclouds.idlgen.test;

import java.io.File;
import java.io.IOException;

import org.summerclouds.idlgen.core.ModelBuilder;
import org.junit.jupiter.api.Test;
import org.summerclouds.idlgen.core.Generator;
import org.summerclouds.idlgen.core.GeneratorBuilder;
import org.summerclouds.idlgen.core.Model;

import de.mhus.lib.errors.MException;
import de.mhus.lib.tests.TestCase;

public class GeneratorTest extends TestCase {

	@Test
	public void test1() throws MException, IOException {
		Model model = new Model();
		ModelBuilder builder = new ModelBuilder(model);
		builder.load(new File("examples/config1/idl.yaml"));
		builder.build();
		
		model.dump();
		
		Generator gen = new GeneratorBuilder(model).load(new File("examples/templates1")).generator();
		gen.setTargetDir(new File("target"));
		gen.generate();
		
	}

	@Test
	public void test2() throws MException, IOException {
		Model model = new Model();
		ModelBuilder builder = new ModelBuilder(model);
		builder.load(new File("examples/config2/idl.yaml"));
		builder.build();
		
//		model.dump();
		
		Generator gen = new GeneratorBuilder(model).load(new File("examples/templates2")).generator();
		gen.setTargetDir(new File("target"));
		gen.generate();
		
	}

}
