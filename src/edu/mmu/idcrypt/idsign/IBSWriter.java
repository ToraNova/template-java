/*
 * ID-based signature class
 * Buffered Writer base class
 *
 * This class and its subclasses uses buffered writers
 * for i/p and o/p of keys and signatures
 *
 * toranova@github.com
 * chia_jason96@live.com
 *
 * HybridPKI Project
 * Aug 04 2020
 */
package edu.mmu.idcrypt.idsign;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public abstract class IBSWriter {

	private static final Logger log = Logger.getLogger(IBSWriter.class);

	protected final String mDFormat = "yyyyMMdd";
	protected SimpleDateFormat mSDF;
	protected MessageDigest mMD;
   	protected String newline = System.getProperty("line.separator");

	protected abstract void parseMSK(BufferedReader br) throws IOException;
	protected abstract void parseUSK(BufferedReader br) throws IOException;
	protected abstract void parseMPK(BufferedReader br) throws IOException;
	protected abstract void parseSIG(BufferedReader br) throws IOException;
	protected boolean mInit = false;

	/*
	 * Initializes the appending date format
	 */
	public IBSWriter() throws NoSuchAlgorithmException{
		mSDF = new SimpleDateFormat(mDFormat);
		mMD = MessageDigest.getInstance("SHA-512");
		mInit = true;
	}

	public boolean initialized(){
		return mInit;
	}

	/*
	 * verify a signature and userid
	 */
	protected abstract boolean verifyMsg(
			BufferedReader mpkReader,
			BufferedReader sigReader,
			String userid,
			String msg
	) throws IOException;

	/*
	 * perform ID-based signature using usk
	 */
	protected abstract boolean signMsg(
			BufferedReader uskReader,
			BufferedWriter sigWriter,
			String message
	) throws IOException;

	/*
	 * perform keygeneration
	 */
	protected abstract boolean keyGen(
			BufferedWriter mskWriter,
			BufferedWriter mpkWriter,
			String skparam
	) throws NoSuchAlgorithmException, IOException;

	/*
	 * signs on userid with msk, place result in usk
	 */
	protected abstract boolean uskGen(
			BufferedReader mskReader,
			BufferedWriter uskWriter,
			String userid,
			int exprday
	) throws IOException, ParseException;

	/*
	 * Overloaded uskgen function
	 */
	protected boolean uskGen(
			BufferedReader mskReader,
			BufferedWriter uskWriter,
			String userid
	) throws IOException, ParseException{
		return uskGen(mskReader, uskWriter, userid, 360);
	}

	/*
	 * Builds a signature candidate by appending expiry of usk to public string
	 * userid + expiry date
	 */
	public String appendExpiry(String userid, int exprday) throws ParseException{
		String dt = new SimpleDateFormat(mDFormat).format(new Date());
		Calendar c = Calendar.getInstance();
		c.setTime(mSDF.parse(dt));
		c.add(Calendar.DATE,exprday);
		dt = mSDF.format(c.getTime());
		return userid+dt;
	}


	public Logger getLogger(){
		return log;
	}
}
