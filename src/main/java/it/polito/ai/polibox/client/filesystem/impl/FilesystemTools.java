/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Igor Deplano
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package it.polito.ai.polibox.client.filesystem.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FilesystemTools {

	public static final int CHUNK_SIZE=76800;
	
	private FilesystemTools() {
	}

	public static String sha3Digest(File f){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA3-512");
			@SuppressWarnings("resource")
			FileInputStream fi = new  FileInputStream(f);
			byte[] block=new byte[4096];
			int length;
	        while ((length = fi.read(block)) > 0) {
	        	md.update(block, 0, length);
	        }
	        byte[] digest=md.digest();
	        return org.bouncycastle.util.encoders.Hex.toHexString(digest);
		} catch (FileNotFoundException e) {
			new RuntimeException(e);
		} catch (IOException e) {
			new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			new RuntimeException(e);
		}
		return "";
	}
	
	public static String getMime(File f){
		Path p;
		try {
			p = Paths.get(f.getCanonicalPath());
			return Files.probeContentType(p);
		} catch (IOException e) {
			new RuntimeException(e);
		}
		return "";
	}
	
	public static int getChunkNumber(File f){
		long size=f.length();
		System.out.println("dimensione:"+size+"|chunksize:"+CHUNK_SIZE+"|ceil:"+Math.ceil(size/CHUNK_SIZE));
		return (int) Math.ceil(((double)(size))/CHUNK_SIZE);
	}
	
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
}
