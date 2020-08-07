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

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.io.IOException;

public class PrimaryCLI {
	private static final Logger log = Logger.getLogger(PrimaryCLI.class);

	//print help string and exit
	public static void exithelp(Options opts){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("jidcrypt-cli", opts);
		System.exit(1);
	}

	public static void main(String args[]){
		BasicConfigurator.configure();

		Options opts = new Options();

		Option opmode = new Option("op", "opmode", true, "mode of operation :init(i), keyder(k), sign(s), signfile(sf), verify(v), verifyfile(vf)");
		opmode.setRequired(true);
		opts.addOption(opmode);

		Option opa = new Option("a", "algorithm", true, "algorithm for the pkg");
		opts.addOption(opa);

		Option opmsk = new Option("msk", "masterkey", true, "master secret-key path");
		opts.addOption(opmsk);

		Option okn = new Option("kn", "keyspecs", true, "keyspec for algo");
		opts.addOption(okn);

		Option oppar = new Option("par", "sysparams", true, "master public-key path");
		opts.addOption(oppar);


		Option opusk = new Option("usk", "userkey", true, "user secret-key path");
		opts.addOption(opusk);

		Option opu = new Option("u", "userid", true, "user public id");
		opts.addOption(opu);

		Option opvd = new Option("vd", "valid_days", true, "user secret-key validity (days)");
		opts.addOption(opvd);

		//TODO: msgfile signing/verification
		//Option opf = new Option("mfile", "msgfile", true, "file for singing");
		//opts.addOption(opf);

		Option opm = new Option("m", "message", true, "message for signing");
		opts.addOption(opm);


		Option opsig = new Option("sig", "signature", true, "signature path");
		opts.addOption(opsig);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(opts, args);
			String mode = cmd.getOptionValue("opmode");

			IBSWriter iw;

			//declare variables
			String skfn, pkfn, ukfn, mgfn, sgfn;
			String ksstr, msg, userid;
			FileInOut skf, pkf, ukf, mgf, sgf;
			BufferedReader bra, brb;
			BufferedWriter bwa, bwb;

			switch(mode){
				case "init": case "i":

					String algo = cmd.getOptionValue("algorithm");
					if(algo == null){
						log.error("mode init must have algorithm(a) specified");
						log.error("supported algorithms:");
						for(String s : IBSWriter.getSuppAlgo()){
							log.error(s);
						}
						System.exit(1);
					}

					iw = IBSWriter.getIBSWriter(algo);

					skfn = cmd.getOptionValue("masterkey");
					pkfn = cmd.getOptionValue("sysparams");
					if(skfn == null || pkfn == null){
						log.error("mode init must have masterkey(msk) and sysparams(par) specified.");
						exithelp(opts);
					}

					ksstr = cmd.getOptionValue("keyspecs");
					if(ksstr == null){
						ksstr = iw.getDKeySpec();
						log.warn("keyspecs not specified. using default: "+ksstr);
					}

					skf = new FileInOut(skfn,true);
					pkf = new FileInOut(pkfn,true);
					bwa = skf.getBufWrite();
					bwb = pkf.getBufWrite();
					iw.mskSetup(bwa,bwb,ksstr);
					bwa.close();
					bwb.close();
					break;

				case "keyder": case "k":
					skfn = cmd.getOptionValue("masterkey");
					ukfn = cmd.getOptionValue("userkey");
					userid = cmd.getOptionValue("userid");
					String validd = cmd.getOptionValue("valid_days");
					if(skfn == null || ukfn == null || userid == null || validd == null ){
						log.error("mode keyder must have masterkey(msk), userkey(usk), userid(u) and valid_days(vd) specified.");
						exithelp(opts);
					}
					int vd;
					try{
						vd = Integer.parseInt(validd);
					}catch(Exception e){
						vd = 0;
						log.error("unable to parse validity (days): "+validd);
						System.exit(1);
					}

					skf = new FileInOut(skfn,false);
					ukf = new FileInOut(ukfn,true);

					bra = skf.getBufRead();
					bwa = ukf.getBufWrite();

					iw = IBSWriter.getIBSWriter(bra);

					userid = iw.appendExpiry(userid,vd);
					log.info("user public id: "+userid);

					iw.uskGen( bra, bwa, userid );
					bra.close();
					bwa.close();
					break;

				case "sign": case "s":
					ukfn = cmd.getOptionValue("userkey");
					sgfn = cmd.getOptionValue("signature");
					msg = cmd.getOptionValue("message");
					if(msg == null || ukfn == null || sgfn == null){
						log.error("mode sign must have userkey(usk), message(m) and signature(sig) specified.");
						exithelp(opts);
					}

					ukf = new FileInOut(ukfn,false);
					sgf = new FileInOut(sgfn,true);

					bra = ukf.getBufRead();
					bwa = sgf.getBufWrite();

					iw = IBSWriter.getIBSWriter(bra);

					iw.signMsg( bra, msg, bwa );
					bra.close();
					bwa.close();
					break;

				case "verify": case "v":
					userid = cmd.getOptionValue("userid");
					pkfn = cmd.getOptionValue("sysparams");
					sgfn = cmd.getOptionValue("signature");
					msg = cmd.getOptionValue("message");
					if(pkfn == null || sgfn == null || userid == null || msg == null){
						log.error("mode verify must have sysparams(par), userid(u), message(m) and signature(sig) specified.");
						exithelp(opts);
					}

					pkf = new FileInOut(pkfn,false);
					sgf = new FileInOut(sgfn,false);

					bra = pkf.getBufRead();
					brb = sgf.getBufRead();

					iw = IBSWriter.getIBSWriter(bra);
					brb.readLine(); //skip one line for the 'type' header

					if(iw.verifyMsg( bra, msg, brb, userid )){
						log.info("valid signature");
					}else{
						log.info("invalid signature");

					}
					bra.close();
					brb.close();
					break;
				default:
					log.error("invalid mode: "+mode);
					exithelp(opts);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			exithelp(opts);
		}
	}
}
