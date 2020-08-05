/*
 * Test class
 *
 * HybridPKI Project
 * Aug 04 2020
 *
 */
package edu.mmu.idcrypt.idsign;

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

import org.apache.log4j.Logger;
import org.junit.Test;

public final class IBSTest {

	private static final Logger log = Logger.getLogger(IBSTest.class);

	@Test
	public void test01(){
		try{
			IBSWriter idsrsa = new RSAWriter();
			assertTrue(idsrsa.initialized());
		}catch(Exception e){
        		log.trace(">RSAWriter()");
			log.error(e.getMessage());
			log.trace("<RSAWriter()");
		}
	}

}
