package org.summerclouds.facade.mvn;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class FacadePluginTest extends AbstractMojoTestCase {

	protected void setUp() throws Exception
    {
        // required for mojo lookups to work
        super.setUp();
    }
	
	public void testMojoGoal() throws Exception
    {
        File testPom = new File( getBasedir(),
          "src/test/resources/tests/basic-test/basic-test-plugin-config.xml" );
 
        GenerateMojo mojo = (GenerateMojo) lookupMojo( "generate", testPom );
 
        assertNotNull( mojo );
    }
}
