/*
 * CLI-based main interface class
 *
 * HybridPKI project
 * Aug 06 2020
 *
 */

package edu.mmu.idcrypt.idsign;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.commons.cli.*;

import edu.mmu.ioutil.FileInOut;
import java.io.BufferedWriter;
import java.io.BufferedReader;

public class PrimaryCLI {
	private static final Logger log = Logger.getLogger(PrimaryCLI.class);

	//print help string and exit
	public static void exithelp(Options opts){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("utility-name", opts);
		System.exit(1);
	}

	public static void main(String args[]){
		BasicConfigurator.configure();

		Options opts = new Options();
		Option opmode = new Option("m", "mode", true, "mode of operation (init(i), keyder(k), sign(s), verify(v))");
		opmode.setRequired(true);
		opts.addOption(opmode);

		Option opa = new Option("a", "algorithm", true, "algorithm for the pkg");
		opa.setRequired(true);
		opts.addOption(opa);

		Option opmsk = new Option("msk", "masterkey", true, "master secret-key path");
		opts.addOption(opmsk);

		Option oppar = new Option("par", "sysparams", true, "master public-key path");
		opts.addOption(oppar);

		Option opusk = new Option("usk", "userkey", true, "user secret-key path");
		opts.addOption(opusk);

		Option opu = new Option("u", "userid", true, "user public id");
		opts.addOption(opu);

		Option opm = new Option("mstr", "message", true, "message for signing");
		opts.addOption(opm);

		Option opf = new Option("mfile", "msgfile", true, "file for singing");
		opts.addOption(opf);

		Option opo = new Option("o", "output", true, "output file path for respective modes of operation");
		opo.setRequired(true);
		opts.addOption(opo);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(opts, args);
			String mode = cmd.getOptionValue("mode");
			String algo = cmd.getOptionValue("algorithm");
			switch(mode){
				case "init": case "i":
					break;
				case "keyder": case "k":
					break;
				case "sign": case "s":
					break;
				case "verify": case "v":
					break;
				default:
					log.error("Invalid mode: "+mode);
					exithelp(opts);
			}
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			exithelp(opts);
		}
	}
}
