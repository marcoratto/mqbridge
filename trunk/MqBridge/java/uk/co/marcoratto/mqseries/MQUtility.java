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
package uk.co.marcoratto.mqseries;

import java.io.IOException;

import org.apache.log4j.Logger;

import uk.co.marcoratto.util.Utility;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class MQUtility {
	
	private String host;
	private int port = -1;
	private String channel;
	private int ccsid;
	private String queueName;
	private String qmgrName;
	private String outputEncoding;		
	private String format = null;
    	
	private static Logger logger = Logger.getLogger("uk.co.marcoratto.mqbridge");
	
    public MQUtility() {
    }
	
	private void setup() throws MQUtilityException {
		if (this.host == null) {
			throw new MQUtilityException("Why host is null ?");
		}
		if (this.port == -1) {
			throw new MQUtilityException("Why port is -1 ?");
		}
		if (this.channel == null) {
			throw new MQUtilityException("Why channel is null ?");
		}
		// host to connect to
		MQEnvironment.hostname = this.host; 
		
		// port to connect to.
		MQEnvironment.port = this.port; 
		
		// If I don't set this,
		// it defaults to 1414
		// (the default WebSphere MQ port)
		MQEnvironment.channel = this.channel; // the CASE-SENSITIVE
		// name of the
		// SVR CONN channel on
		// the queue manager
	}
	
	public String read() throws MQUtilityException {		
		if (this.outputEncoding == null) {
			throw new MQUtilityException("Why outputEncoding is null ?");
		}
		if (this.qmgrName == null) {
			throw new MQUtilityException("Why qmgrName is null ?");
		}
		if (this.queueName == null) {
			throw new MQUtilityException("Why queueName is null ?");
		}
		
		MQQueueManager qMgr = null;
		int openOptions = MQC.MQOO_INQUIRE |
						  MQC.MQOO_FAIL_IF_QUIESCING |
						  MQC.MQOO_INPUT_SHARED;

		MQQueue queue = null;
		String strMsg = null;
		
		try {
			this.setup();
			
			logger.debug("Try to connect to the MQ Queue Manager " + this.qmgrName);
			qMgr = new MQQueueManager(this.qmgrName);
			
			logger.debug("Try to access to the queue " + this.queueName);
			queue = qMgr.accessQueue(this.queueName, openOptions,
					null, // default q manager
					null, // no dynamic q name
					null); // no alternate user id

			MQGetMessageOptions getOptions = new MQGetMessageOptions();
			getOptions.options = MQC.MQGMO_NO_WAIT | MQC.MQGMO_CONVERT;

			MQMessage message;										
			message = new MQMessage();

			// set ccsid to 1208 UTF-8
			message.characterSet = this.ccsid;

			logger.debug("Waiting for message... ");
			queue.get(message, getOptions);
			
			byte[] b = new byte[message.getMessageLength()];
			message.readFully(b);	
			strMsg = new String(b, this.outputEncoding);
			logger.debug("Read message:" + Utility.NEWLINE + strMsg);
			message.clearMessage();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new MQUtilityException(e);
		} finally {
			if (queue != null) {
				try {
					queue.close();
				} catch (MQException e) {
					// Ignore
				}				
			}
			if (qMgr != null) {
				try {
					qMgr.disconnect();
				} catch (MQException e) {
					// Ignore
				}						
			}
		}
		return strMsg;
	}
	
	public void write(String text) throws MQUtilityException {
		if (this.qmgrName == null) {
			throw new MQUtilityException("Why qmgrName is null ?");
		}
		if (this.queueName == null) {
			throw new MQUtilityException("Why queueName is null ?");
		}
		
		MQQueueManager qMgr = null;
		MQQueue queue = null;		
		int openOptions = MQC.MQOO_OUTPUT;
		try {
			this.setup();

			logger.debug("Try to connect to the MQ Queue Manager " + this.qmgrName);
			qMgr = new MQQueueManager(this.qmgrName);
			
			logger.debug("Try to access to the queue " + this.queueName);			
			queue = qMgr.accessQueue(this.queueName, openOptions, null, null, null);

			// Define a MQ message buffer
			MQMessage mBuf = new MQMessage();

			// create message options
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			// accept defaults
			pmo.options = MQC.MQPMO_NONE; // use defaults

			mBuf.clearMessage(); // reset the buffer
			mBuf.correlationId = MQC.MQCI_NONE; // set correlationId
			mBuf.messageId = MQC.MQMI_NONE; // set messageId
			
			if (mBuf.format == null) {
				mBuf.format = MQC.MQFMT_STRING;				
			} else {
				mBuf.format = this.format;
			}
			
			// set ccsid to 1208 UTF-8
			mBuf.characterSet = this.ccsid; 			
			
			mBuf.writeString(text); // set actual message			
			logger.debug("Send message:" + Utility.NEWLINE + text);
			queue.put(mBuf, pmo); // put the message out on the queue
			logger.debug("Message sent.");
		} catch (MQException e) {
			logger.error(e.getMessage(), e);
			throw new MQUtilityException(e);
		} catch (IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
			throw new MQUtilityException(ioe);
		} finally {
			if (queue != null) {
				try {
					logger.debug("Close connection.");
					queue.close();
				} catch (MQException e) {
					// Ignore;
				}
			}
			if (qMgr != null) {
				try {
					logger.debug("Disconnect.");
					qMgr.disconnect();
				} catch (MQException e) {
					// Ignore
				}
			}
		}
	}

	public void clear() throws MQUtilityException {
		MQQueueManager qMgr = null;
		
		// open options for browse & share
		int openOptions = MQC.MQOO_BROWSE | MQC.MQOO_INPUT_SHARED; // open options for browse & share 
		
		MQQueue queue = null;
		
		try {
			this.setup();
			
			qMgr = new MQQueueManager(this.qmgrName);
			
			queue = qMgr.accessQueue(this.queueName, openOptions,
					null, // default q manager
					null, // no dynamic q name
					null); // no alternate user id
		
			MQGetMessageOptions getOptions = new MQGetMessageOptions();
			getOptions.options = MQC.MQOO_INQUIRE | MQC.MQGMO_NO_WAIT ; //for browsing
			getOptions.waitInterval = MQC.MQWI_UNLIMITED; // for waiting unlimted times
				
			MQMessage message;
			String strMsg;
			while(true) {
				message = new MQMessage();

				// set ccsid to 1208 UTF-8
				message.characterSet = this.ccsid;

				try {
					logger.debug("waiting for message... ");
					queue.get(message, getOptions);					
					byte[] b = new byte[message.getMessageLength()];
					message.readFully(b);	
					strMsg = new String(b, this.outputEncoding);
					logger.debug("Clear message:" + Utility.NEWLINE + strMsg);
					getOptions.options = MQC.MQGMO_WAIT | MQC.MQGMO_MSG_UNDER_CURSOR;;
					queue.get(message, getOptions);						
					message.clearMessage();
					getOptions.options = MQC.MQGMO_BROWSE_NEXT | MQC.MQGMO_NO_WAIT ;
				} catch (Exception e) {
				  logger.error("Exception. Exit", e);
				  break;
				}							
			} // while ends here
		} catch (MQException e) {
			if (e.completionCode != 2) {
				logger.error(e.getMessage(), e);
				throw new MQUtilityException(e);
			}
		} finally {
			if (queue != null) {
				try {
					queue.close();
				} catch (MQException e) {
					// Ignore
				}				
			}
			if (qMgr != null) {
				try {
					qMgr.disconnect();
				} catch (MQException e) {
					// Ignore
				}						
			}
		}			
	}
	
	public int getCurrentDepth() throws MQUtilityException {
		int openOptions = MQC.MQOO_INPUT_SHARED + 
						  MQC.MQOO_INQUIRE;

		MQQueueManager qMgr = null;
		MQQueue queue = null;
		int size = -1;
		
		try {
			this.setup();
			
			qMgr = new MQQueueManager(this.qmgrName);
			
			queue = qMgr.accessQueue(this.queueName, 
									 openOptions,
									 null, // default q manager
									 null, // no dynamic q name
									 null); // no alternate user id
			
			size = queue.getCurrentDepth();	
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new MQUtilityException(e);
		} finally {
			if (queue != null) {
				try {
					queue.close();
				} catch (MQException e) {
					// Ignore
				}				
			}
			if (qMgr != null) {
				try {
					qMgr.disconnect();
				} catch (MQException e) {
					// Ignore
				}						
			}
		}	
		return size;
	}	

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int aPort) {
		this.port = aPort;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public int getCcsid() {
		return ccsid;
	}

	public void setCcsid(int i) {
		this.ccsid = i;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getQmgrName() {
		return qmgrName;
	}

	public void setQmgrName(String qmgrName) {
		this.qmgrName = qmgrName;
	}

	public String getOutputEncoding() {
		return outputEncoding;
	}

	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	
}
