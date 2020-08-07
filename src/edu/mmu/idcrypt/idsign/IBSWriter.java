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
import java.io.StringReader;
import java.io.Reader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public abstract class IBSWriter {

	/*
	 * Static method to obtain the right IBSWriter class
	 */
	public static IBSWriter getIBSWriter(String algostr)
	throws NoSuchAlgorithmException {
		IBSWriter out;
		switch(algostr){
			case "RSA": case "rsa":
				out = new RSAWriter();
				break;
			default:
				out = null;
				log.error("unsupported algorithm: "+algostr);
				log.error("supported algorithms:");
				for(String s : getSuppAlgo()){
					log.error(s);
				}
				throw new NoSuchAlgorithmException("unsupported algorithm: "+algostr);
		}
		return out;
	}

	public static IBSWriter getIBSWriter(BufferedReader kr)
	throws NoSuchAlgorithmException, IOException{
		return getIBSWriter(kr.readLine());
	}

	public static String[] getSuppAlgo(){
		String[] out = {"RSA (rsa)"};
		return out;
	}

	private static final Logger log = Logger.getLogger(IBSWriter.class);

	protected final String mDFormat = "yyyyMMdd";
	protected SimpleDateFormat mSDF;
	protected MessageDigest mMD;
   	protected String newline = System.getProperty("line.separator");

	protected abstract void parseMSK(BufferedReader br) throws IOException;
	protected abstract void parseMPK(BufferedReader br) throws IOException;
	protected abstract void parseSIG(BufferedReader br) throws IOException;
	protected abstract String parseUSK(BufferedReader br) throws IOException;
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
	 * provides the default key specs
	 */
	protected abstract String getDKeySpec();

	/*
	 * verify a signature and userid
	 * return TRUE if valid
	 * FALSE if msg is invalid
	 */
	protected abstract boolean verifyMsg(
			BufferedReader mpkReader,
			BufferedReader msgReader,
			BufferedReader sigReader,
			String userid
	) throws IOException;

	//overloaded base function
	public boolean verifyMsg(
			BufferedReader mpkReader,
			String message,
			BufferedReader sigReader,
			String userid
	) throws IOException{
		Reader str = new StringReader(message);
		BufferedReader br = new BufferedReader(str);
		return verifyMsg(mpkReader,br,sigReader,userid);
	}

	/*
	 * perform ID-based signature using usk
	 */
	protected abstract boolean signMsg(
			BufferedReader uskReader,
			BufferedReader msgReader,
			BufferedWriter sigWriter
	) throws IOException;


	//overloaded base function
	public boolean signMsg(
			BufferedReader uskReader,
			String message,
			BufferedWriter sigWriter
	) throws IOException{
		Reader str = new StringReader(message);
		BufferedReader br = new BufferedReader(str);
		return signMsg(uskReader, br, sigWriter);
	}

	/*
	 * signs on userid with msk, place result in usk
	 */
	protected abstract boolean uskGen(
			BufferedReader mskReader,
			BufferedWriter uskWriter,
			String userid
	) throws IOException, ParseException;

	/*
	 * perform keygeneration
	 */
	protected abstract boolean mskSetup(
			BufferedWriter mskWriter,
			BufferedWriter mpkWriter,
			String skparam
	) throws NoSuchAlgorithmException, IOException;

	/*
	 * Builds a signature candidate by appending expiry of usk to public string
	 * userid + expiry date
	 */
	public String appendExpiry(String userid, int exprday) throws ParseException{
		String dt = mSDF.format(new Date());
		Calendar c = Calendar.getInstance();
		c.setTime(mSDF.parse(dt));
		c.add(Calendar.DATE,exprday);
		dt = mSDF.format(c.getTime());
		return userid+dt;
	}

	/*
	 * checks if a public + expiry date has expired.
	 * if today > yyyyMMdd, then return true,
	 * else false
	 */
	public boolean invalidID(String appendedID) throws ParseException{
		if(appendedID.length() - 8 <= 0) return true;
		String datestr = appendedID.substring( appendedID.length() - 8 );
		Date expiry = mSDF.parse(datestr);
		if(expiry.before(new Date())){
			//expired
			return true;
		}
		return false;
	}

	public Logger getLogger(){
		return log;
	}
}
