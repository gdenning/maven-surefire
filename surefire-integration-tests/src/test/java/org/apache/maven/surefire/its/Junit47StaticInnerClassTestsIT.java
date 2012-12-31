package org.apache.maven.surefire.its;

import org.apache.maven.surefire.its.fixture.SurefireIntegrationTestCase;

public class Junit47StaticInnerClassTestsIT extends SurefireIntegrationTestCase {

	public void testStaticInnerClassTests() {
		executeErrorFreeTest( "junit47-static-inner-class-tests", 3 );
	}
}
