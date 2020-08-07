/*
 * Main TCP Service class
 *
 * HybridPKI project
 * Aug 06 2020
 *
 */

package edu.mmu.idcrypt.idsign;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.BufferedReader;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.io.IOException;

public class PrimaryTCP {
	private static final Logger log = Logger.getLogger(PrimaryTCP.class);

	public static void main(String args[]){
		BasicConfigurator.configure();
		TCPStateless stless = new TCPStateless();
		stless.start(6666);
	}
}
