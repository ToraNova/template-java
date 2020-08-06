/*
 * Easy file output function
 * Written for hybridpki project
 *
 * author: Toranova
 * mailto: chia_jason96@live.com
 * Aug 04 2020
 */

package edu.mmu.ioutil;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

public class FileInOut {

	private static final Logger log = Logger.getLogger(FileInOut.class);
	private File mFile;

	public FileInOut(String filename, boolean renew) throws IOException{
		mFile = new File(filename);

		//ensure parent directory exists
		if(! mFile.getParentFile().exists()){
			mFile.getParentFile().mkdirs();
		}

		if(!mFile.exists()){
			//create file if not exist
			mFile.createNewFile();
		}else if(renew){
			//renew file
			mFile.delete();
			mFile.createNewFile();
		}
	}

	public FileInOut(String filename)throws IOException{
		this(filename, false);
	}

	//obtain buffer reader/writer
	public BufferedReader getBufRead() throws IOException{
		FileReader fr = new FileReader(mFile.getAbsoluteFile());
		return new BufferedReader(fr);
	}

	public BufferedWriter getBufWrite() throws IOException{
		FileWriter fw = new FileWriter(mFile.getAbsoluteFile());
		return new BufferedWriter(fw);
	}

	public String getName(){
		return mFile.getName();
	}

	public String getPath(){
		return mFile.getPath();
	}

	public String getAbsName(){
		return mFile.getAbsolutePath();
	}

	public String getAbsPath(){
		return mFile.getParent();
	}

}
