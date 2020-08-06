/*
 * Sanity Test Class
 *
 * sometimes, without these sanity checks,
 * we might just become insane.
 *
 * template-java project
 *
 */
package example.sanity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Test;

public final class SampleTest {

	private static final Logger log = Logger.getLogger(SampleTest.class);

	@Test
	public void test(){
		BasicConfigurator.configure();

		Sample s = new Sample();
		int res = s.add(1,2);
		assertEquals(res, 3);
	}

}
