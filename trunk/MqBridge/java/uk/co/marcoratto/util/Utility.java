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
package uk.co.marcoratto.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.apache.log4j.Logger;

public final class Utility {

	private static Logger logger = Logger.getLogger("uk.co.marcoratto.mqbridge");
	
	public static void stringToFile(File file, String encoding, String buffer) throws UtilityException {
		BufferedWriter bw = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new StringReader(buffer));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			String line = null;
			String space = "";
			while ((line = br.readLine()) != null) {
				bw.write(space);
				bw.write(line);
				space = NEWLINE;
			}
			logger.debug("Write " + buffer.length() + " bytes to file.");
			logger.debug("The buffer is:" + NEWLINE + buffer);
		} catch (Throwable t) {
			throw new UtilityException(t);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}
	
	public static String fileToString(File file, String encoding) throws UtilityException {
		BufferedReader br = null;
		StringBuffer out = new StringBuffer("");
		try {
			logger.debug("Try to read file " + file.getAbsolutePath().toString() + ".");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			String line = null;
			String space = "";
			while ((line = br.readLine()) != null) {
				out.append(space);
				out.append(line);
				space = NEWLINE;
			}
			logger.debug("Read " + out.length() + " bytes from file.");
			logger.debug("The buffer is:" + NEWLINE + out.toString());
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw new UtilityException(t);
		} finally {
			if (br != null) {
				try {
						br.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
		return out.toString();
	}	
	
	public int stringToInt(String s, int defaultValue) {
		int out = defaultValue;
		try {
			out = Integer.parseInt(s, 10);
		} catch (NumberFormatException e) {
			out = defaultValue;
		}
		return out;
	}
		
	public final static String NEWLINE = System.getProperty("line.separator");

}
