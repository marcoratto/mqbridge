/*
 * Copyright (C) 2010 Marco Ratto
 *
 * This file is part of the project MqBridge.
 *
 * MqBridge is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 *
 * MqBridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MqBridge; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package uk.co.marcoratto.security;

import java.security.*;
import java.io.*;

public class MD5 {

	private final static int BUFFER_SIZE = 1 * 1024 * 1024;
	
   public MD5() {
   }

   public static byte[] encode (String s) {
      if (s == null) {
         return null;
      }
      return encode(s.getBytes());
   }

   public static byte[] encode(byte[] buffer) {
      try {
         MessageDigest md5 = MessageDigest.getInstance("MD5");
         return md5.digest(buffer);
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      }
      return null;
   }

   public static byte[] encodeFile(File aFile) {
	    BufferedInputStream bis = null;
	    DigestInputStream in = null;
	    MessageDigest md = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(aFile));
		    md = MessageDigest.getInstance("MD5");
		    in = new DigestInputStream(bis, md);
		    
		    int i;
		    byte[] buffer = new byte[BUFFER_SIZE];
		    do {
		      i = in.read(buffer, 0, BUFFER_SIZE);
		    } while (i == BUFFER_SIZE);
		    md = in.getMessageDigest();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					// Ignore
				}				
			}
			if (in != null) {
			    try {
					in.close();
				} catch (IOException e) {
					// Ignore
				}							
			}
		}

	    return md.digest();
  }
   
   public static String getHexString(byte[] b) throws Exception {
	   String result = "";
	   for (int i=0; i < b.length; i++) {
	     result +=
	           Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
	   }
	   return result;
	 }

}
