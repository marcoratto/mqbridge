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
package uk.co.marcoratto.mqbridge;

import java.io.File;

import org.apache.log4j.Logger;

import com.ibm.mq.MQC;

import uk.co.marcoratto.mqseries.MQUtility;
import uk.co.marcoratto.mqseries.MQUtilityException;
import uk.co.marcoratto.security.Base64;
import uk.co.marcoratto.util.Utility;
import uk.co.marcoratto.util.UtilityException;

public class FileToQueue {
	
	private static Logger logger = Logger.getLogger("uk.co.marcoratto.mqbridge");
	
	private final static String DEFAULT_ENCODING = "UTF-8";
	private final static String DEFAULT_QMGR_NAME = "";
	private final static int DEFAULT_PORT = 1414;
	private final static int DEFAULT_CCSID = 1208;
	private final static String DEFAULT_FORMAT = MQC.MQFMT_STRING;
		
	public FileToQueue() {
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println(help);
			System.exit(1);
		}
		FileToQueue instance = new FileToQueue();
		int retCode = -1;
		try {
			instance.runme(args);
			retCode = 0;
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			retCode = 1;
		}
		System.exit(retCode);
	}
	
	public void runme(String[] args) throws FileToQueueException {
		this.ccsid = DEFAULT_CCSID;
		this.encoding = DEFAULT_ENCODING;
		this.port = DEFAULT_PORT;
		this.qmgrName = DEFAULT_QMGR_NAME;
		this.format = DEFAULT_FORMAT;
		
		for (int j = 0; j < args.length; j++) {
			if (args[j].equalsIgnoreCase("-ccsid") == true) {
				if (++j < args.length) {
					this.ccsid = Integer.parseInt(args[j], 10);
				}
			} else if (args[j].equalsIgnoreCase("-channel") == true) {
				if (++j < args.length) {
					this.channel = args[j];
				}
			} else if (args[j].equalsIgnoreCase("-port") == true) {
				if (++j < args.length) {
					this.port = Integer.parseInt(args[j], 10);
				}
			} else if (args[j].equalsIgnoreCase("-host") == true) {
				if (++j < args.length) {
					this.host = args[j];
				}
			} else if (args[j].equalsIgnoreCase("-qmgrName") == true) {
				if (++j < args.length) {
					this.qmgrName = args[j];
				}
			} else if (args[j].equalsIgnoreCase("-queueName") == true) {
				if (++j < args.length) {
					this.queueName = args[j];
				}
			} else if (args[j].equalsIgnoreCase("-format") == true) {
				if (++j < args.length) {
					this.format = args[j];
				}
			} else if (args[j].equalsIgnoreCase("-encoding") == true) {
				if (++j < args.length) {
					this.encoding = args[j];
				}
			} else if (args[j].equalsIgnoreCase("-file") == true) {
				if (++j < args.length) {
					this.file = new File(args[j]);
				}
			} else if (args[j].equalsIgnoreCase("-base64") == true) {
				if (++j < args.length) {
					this.base64Encoding = args[j].trim().equalsIgnoreCase("true");
				}
			} else {
				System.err.println("ERROR: Parameter " + args[j] + " unknown!");
				System.exit(1);
			}
		}
		
		if (this.host == null) {
			System.err.println("ERROR: Parameter '-host' is mandatory!");
			System.exit(1);
		}
		
		MQUtility mqUtility = new MQUtility();
		mqUtility.setCcsid(this.ccsid);
		mqUtility.setChannel(this.channel);
		mqUtility.setHost(this.host);
		mqUtility.setPort(this.port);
		mqUtility.setQmgrName(this.qmgrName);
		mqUtility.setQueueName(this.queueName);
		mqUtility.setFormat(this.format);
		
		String buffer = null;
		try {
			buffer = Utility.fileToString(this.file, this.encoding);
			if (this.base64Encoding) {
				buffer = Base64.encode(buffer);
			}
			mqUtility.write(buffer);
		} catch (UtilityException e) {
			logger.fatal(e.getMessage());
			throw new FileToQueueException(e);
		} catch (MQUtilityException e) {
			logger.fatal(e.getMessage());
			throw new FileToQueueException(e);
		}				

	}

	private String qmgrName = null;
	private String host = null;
	private int port = -1;
	private String channel = null;
	private String queueName = null;
	private String format = null;
	private String encoding = null;
	private File file = null;
	private int ccsid = -1;
	private boolean base64Encoding = false;
	
	private static String help = " * Parameters:" +
								 "\n" +
								 "-host: Hostname or IP address" +
								 "\n" +
								 "-port: Port (default 1414)" +
								 "\n" +
								 "-channel: Channel" +								 
								 "\n" +
								 "-qmgrName: Queue Manager" +								 
								 "\n" +
								 "-queueName: Queue Name" +								 
								 "\n" +
								 "-ccsid: CCSID (default 1208)" +								 
								 "\n" +
								 "-encoding: File Encoding (default UTF-8)" +								 
								 "\n" +
								 "-base64: Enable the encoding with BASE64" +								 
								 "\n" +
								 "";

}
