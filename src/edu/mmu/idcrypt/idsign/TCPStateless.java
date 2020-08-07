/*
 * Stateless TCP implementation
 * (Does not store ANY file onto the filesystem, only does IBS computations)
 *
 * HybridPKI project
 * Aug 06 2020
 *
 */

package edu.mmu.idcrypt.idsign;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.commons.cli.*;

import java.io.*;
import java.net.*;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public class TCPStateless {
   	protected String newline = System.getProperty("line.separator");
	private ServerSocket mServerSock;
	private Socket mClientSock;
	private boolean mRunFlag;
	private OutputStreamWriter osw;
	private InputStreamReader isr;
	private BufferedWriter out;
	private BufferedReader in;

	private static final Logger log = Logger.getLogger(TCPStateless.class);

	public TCPStateless(){
		mRunFlag = false;
	}

	public void handleClient(Socket c) throws IOException, UnsupportedEncodingException{
		IBSWriter iw;
		String mode, algo;
		String s1, s2, s3;
		osw = new OutputStreamWriter(c.getOutputStream(),"UTF-8");
		isr = new InputStreamReader(c.getInputStream(), "UTF-8");
		out = new BufferedWriter(osw);
		in = new BufferedReader(isr);
		try{
			mode = in.readLine(); //this is the mode
			algo = in.readLine(); //this is the algo
			iw = IBSWriter.getIBSWriter(algo);
			switch(mode){
				case "init":
					s1 = in.readLine(); //keyspec
					iw.mskSetup(out, out, s1);
					break;
				case "keyder":
					s1 = in.readLine(); //masterkey
					s2 = in.readLine(); //userid
					s3 = in.readLine(); //validity(days)
					break;
				case "sign":
					break;
				case "verify":
					break;
				default:
					log.error("unsupported opmode: "+mode);
			}
			log.info(String.format("%s %s operation succeeded.",algo,mode));
		}catch(Exception e){
			out.write(e.getClass().getCanonicalName()+newline);
			out.write(e.getMessage()+newline);
			log.error("inner exception: "+e.getMessage());
		}
		out.close();
		in.close();
		c.close();
	}

	public void start(int port){
		mRunFlag = true;
		try{
			//bound ONLY to localhost
			mServerSock = new ServerSocket(port, 0, InetAddress.getByName(null));
			do{
				mClientSock = mServerSock.accept(); //blocking
				handleClient(mClientSock);
			}while(mRunFlag);
		}catch(Exception e){
			log.error("critical exception: "+e.getMessage());
			try{
				mServerSock.close();
			}catch(Exception e2){
				//funny java
			}
		}
		mRunFlag = false;
	}

	public boolean isRunning(){
		return mRunFlag;
	}

	public void stop(){
		mRunFlag = false;

	}
}
