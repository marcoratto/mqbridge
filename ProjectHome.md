Simple Bridges:

**From file to a IBM MQ queue**

| **Parameter** | **Note** |
|:--------------|:---------|
| **-host**     | The hostname or IP address |
| **-port**     | The port of the Queue Manager (default 1414) |
| **-channel**  | The channel of the Queue Manager|
| **-qmgrName** | The Queue Manager name |
| **-queueName** | The name of the queue |
| **-ccsid**    | Coded Character Set Identifier (default 1208). See the complete list of CCSID on [IBM](http://www-01.ibm.com/software/globalization/ccsid/ccsid_registered.jsp) site|
| **-format**   | MQSTR    |
| **-file**     | File containing the message to send to the queue |
| **-encoding** | Java Character Encoding (default "UTF-8") |
| **-base64**   | Base64 encoding of the message |

**From IBM MQ queue for a file**

| **Parameter** | **Note** |
|:--------------|:---------|
| **-host**     | The hostname or IP address |
| **-port**     | The port of the Queue Manager (default 1414) |
| **-channel**  | The channel of the Queue Manager|
| **-qmgrName** | The Queue Manager name |
| **-queueName** | The name of the queue |
| **-ccsid**    | Coded Character Set Identifier (default 1208). See the complete list of CCSID on [IBM](http://www-01.ibm.com/software/globalization/ccsid/ccsid_registered.jsp) site|
| **-format**   | MQSTR    |
| **-file**     | The file written with the message read from the queue |
| **-encoding** | Java Character Encoding (default "UTF-8") |
| **-base64**   | Base64 decoding of the message |

[![](http://www2.clustrmaps.com/stats/maps-no_clusters/code.google.com-p-mqbridge--thumb.jpg)](http://www2.clustrmaps.com/user/12410a852)