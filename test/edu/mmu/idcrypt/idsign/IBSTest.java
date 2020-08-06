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

import java.util.Formatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Test;

import edu.mmu.ioutil.FileInOut;
import java.io.BufferedWriter;
import java.io.BufferedReader;

public final class IBSTest {

	private static final Logger log = Logger.getLogger(IBSTest.class);

	@Test
	public void rsawriter_test(){
		BasicConfigurator.configure();

		try{
			final String pkgname = "test";

			IBSWriter idsrsa = new RSAWriter();
			assertTrue(idsrsa.initialized());

			//get the directory in which ant is running from
			String ard = System.getenv("BASEDIR");
			//log.info(ard);

			String skey_filename = String.format("%s/tmp/%s.msk", ard, pkgname);
			String pkey_filename = String.format("%s/tmp/%s.par", ard, pkgname);
			String usk_filename = String.format("%s/tmp/alice.usk", ard);
			String sig_filename = String.format("%s/tmp/alice.sig", ard);

			//create file i/o with renew
			FileInOut skfile = new FileInOut(skey_filename,true);
			FileInOut pkfile = new FileInOut(pkey_filename,true);
			FileInOut uskfile = new FileInOut(usk_filename,true);
			FileInOut sigfile = new FileInOut(sig_filename,true);

			BufferedWriter skbw = skfile.getBufWrite();
			BufferedWriter pkbw = pkfile.getBufWrite();

			assertFalse( idsrsa.mskSetup(skbw, pkbw, "2048"));

			skbw.close();
			pkbw.close();


			BufferedReader skbr = skfile.getBufRead();
			BufferedWriter uskbw = uskfile.getBufWrite();

			assertEquals( skbr.readLine(), "RSA" );
			String offid = idsrsa.appendExpiry("alice",30);
			log.info("Alice public: "+offid);
			assertFalse( idsrsa.uskGen( skbr, uskbw, offid) );

			skbr.close();
			uskbw.close();

			BufferedReader uskbr = uskfile.getBufRead();
			BufferedWriter sigbw = sigfile.getBufWrite();

			assertEquals( uskbr.readLine(), "RSA" );
			assertFalse( idsrsa.signMsg( uskbr, sigbw, "hello world") );

			uskbr.close();
			sigbw.close();

			BufferedReader mpkbr = pkfile.getBufRead();
			BufferedReader sigbr = sigfile.getBufRead();

			assertEquals( sigbr.readLine(), mpkbr.readLine() );
			assertTrue( idsrsa.verifyMsg( mpkbr, sigbr, offid, "hello world") );

			mpkbr.close();
			sigbr.close();

		}catch(Exception e){
			e.printStackTrace(System.err);
			log.error(e.getMessage());
		}
	}

}
