package org.summerclouds.facade.mvn;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.jupiter.api.Test;
import org.summerclouds.common.junit.TestUtil;

public class FacadePluginTest extends AbstractMojoTestCase {

	protected void setUp() throws Exception
    {
        // required for mojo lookups to work
        super.setUp();
    }
	
//	@Test
	public void testMojoGoal() throws Exception
    {
        File testPom = new File( getBasedir(),
          "src/test/resources/tests/basic-test/basic-test-plugin-config.xml" );
 
        GenerateMojo mojo = (GenerateMojo) lookupMojo( "generate", testPom );
 
        assertNotNull( mojo );

        File target = new File("target/FacadePluginTest");
        setVariableValueToObject(mojo, "target", target);
        setVariableValueToObject(mojo, "controller", new File("../facade-core/examples/idl-controller.yaml"));
        
        mojo.execute();
        
		TestUtil.recordOrValidateDirectory(target, new File("records/testMojoGoal.properties") );

    }
}
