/*
 * ID-based signature class
 * Base class
 * Original Author: Tan Syh Yuan
 * Refactored by: ToraNova
 * toranova@github.com
 * chia_jason96@live.com
 *
 * HybridPKI Project
 * Aug 04 2020
 */
package edu.mmu.idcrypt.idsign;

import org.apache.log4j.Logger;
import java.math.BigInteger;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;

import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public class RSAWriter extends IBSWriter {

	private static final String mType = "RSA";
	private static final Logger log = Logger.getLogger(RSAWriter.class);
	private BigInteger d,e,n,h,x,s,T;

	public RSAWriter() throws NoSuchAlgorithmException {
		super(); //call this only at end of constructor
	}

	@Override
	public boolean verifyMsg(
		BufferedReader mpkReader,
		BufferedReader sigReader,
		String userid,
		String msg
	) throws IOException {
		BigInteger c;

		parseMPK(mpkReader);
		parseSIG(sigReader);

		// h = H(ID) mod n
		mMD.reset();
		mMD.update(userid.getBytes());
		h = new BigInteger(mMD.digest()).mod(n);
		String MT = msg.concat(T.toString());

		// c = H( msg || T ) mod n
		mMD.reset();
		mMD.update(MT.getBytes());
		c = new BigInteger(mMD.digest());
		return (h.multiply(T.modPow(c,n)).mod(n).compareTo(s.modPow(e,n)) == 0);
	}

	@Override
	public boolean signMsg(
			BufferedReader uskReader,
			BufferedWriter sigWriter,
			String msg
	) throws IOException {
		BigInteger t,c;

		SecureRandom rand = new SecureRandom();

		parseUSK(uskReader);
                t = new BigInteger(rand.nextInt(n.bitLength()),rand).mod(n);
		T = t.modPow(e,n);
		String MT = msg.concat(T.toString());

		mMD.reset();
		mMD.update(MT.getBytes());
		c = new BigInteger(mMD.digest());
		s = x.multiply(t.modPow(c, n)).mod(n);

		//clear USK
		x = BigInteger.ZERO;

		//write signature s, T
		sigWriter.write(mType+newline);
		sigWriter.write(s.toString()+newline);
		sigWriter.write(T.toString()+newline);
		return false;
	}

	@Override
	protected void parseSIG(BufferedReader br)throws IOException{
		s = new BigInteger(br.readLine());
		T = new BigInteger(br.readLine());
	}


	/*
	 * signs on userid with msk, place result in usk
	 * performs H(uid)^d mod n
	 */
	@Override
	public boolean uskGen(
			BufferedReader mskReader,
			BufferedWriter uskWriter,
			String userid,
			int exprday
	) throws IOException, ParseException{
		//read secret
		parseMSK(mskReader);

		mMD.reset(); //reset the message digest
		mMD.update( appendExpiry(userid, exprday).getBytes() );
		h = new BigInteger(mMD.digest()).mod(n);
		x = h.modPow(d, n);

		//clear secret from memory
		d = BigInteger.ZERO;
		e = BigInteger.ZERO;
		n = BigInteger.ZERO;

		//output
		uskWriter.write(mType+newline);
		uskWriter.write(x.toString());
		//clear usk
		x = BigInteger.ZERO;
		return false;
	}

	@Override
	protected void parseUSK(BufferedReader br) throws IOException{
		x = new BigInteger(br.readLine());
	}

	/*
	 * compute a rsa keypair
	 */
	@Override
	public boolean keyGen(
			BufferedWriter mskWriter,
			BufferedWriter mpkWriter,
			String skparam
	)throws NoSuchAlgorithmException, IOException{
		int bits;

		try{
			bits = Integer.parseInt(skparam);
		}catch(Exception e){
			log.error("Error parsing skparam for RSA: "+skparam);
			bits = 2048; //default key size
		}

		BigInteger p, q, phi;
		switch(bits){
			case 1024:
			case 2048:
			case 3072:
			case 4096:
// KEYGEN START
SecureRandom rand = new SecureRandom();
do{
	do{
		q = new BigInteger(bits/2, 16, rand);
		p = new BigInteger(bits/2, 16, rand);
		n = p.multiply(q);
	}while(!q.isProbablePrime(16)||!p.isProbablePrime(16)||n.bitLength()<bits);
	phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
	e = new BigInteger("65537");
}while(!e.gcd(phi).equals(BigInteger.ONE));
//clear p, q
p = BigInteger.ZERO;
q = BigInteger.ZERO;
d = e.modInverse(phi);
//clear phi
phi = BigInteger.ZERO;
break;
// KEYGEN END
		 	default:
	    			throw new NoSuchAlgorithmException("RSA keyGen only supports 1024,2048, 3072 and 4096 bits.");
		}

		//write to msk
		mskWriter.write(mType + newline);
		mskWriter.write(d.toString() + newline);
		d = BigInteger.ZERO;
		mskWriter.write(e.toString() + newline);
		mskWriter.write(n.toString() + newline);

		//write to mpk
		mpkWriter.write(mType + newline);
		mpkWriter.write(e.toString() + newline);
		mpkWriter.write(n.toString() + newline);
		return false;
	}


	@Override
	protected void parseMSK(BufferedReader br) throws IOException{
		//read in d,e,N
		d=new BigInteger(br.readLine());
		e=new BigInteger(br.readLine());
		n=new BigInteger(br.readLine());
	}

	@Override
	protected void parseMPK(BufferedReader br) throws IOException{
		e = new BigInteger(br.readLine());
		n = new BigInteger(br.readLine());
	}

	@Override
	public Logger getLogger(){
		return log;
	}
}
