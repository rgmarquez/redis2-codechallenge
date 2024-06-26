package redis2;

// Greg Marquez
// Engineering Redis Challenge
// Multithreaded server code from :
//          http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**

 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;

    private enum commandVerbs {
        SET, GET, DEL, DBSIZE,
        INCR, ZADD, ZCARD, ZRANK, ZRANGE, STOP 
    }
    
    private static final int VERB_INDEX = 0;
    private static final int KEY_INDEX = 1;
    private static final int ARG1_INDEX = 2;
    private static final int ARG2_INDEX = 3;
    private static final int ARG3_INDEX = 4;
    private static final String TOKEN_SEPERATOR = " ";
    private static final String OPERATION_SUCCESSFUL_STRING = "OK";
    private static final String SYNTAX_ERROR_STRING = "syntax error";
    private static final String ILLEGAL_OPERATION_STRING = "illegal operation";
    
    // The DB emulation structure, static to make it singular, and accessible
    // to multiple object of class WorkerRunnable
    private static final ConcurrentMap<String, String> keyValueStore = new ConcurrentHashMap<String, String>();
    
    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(input));

            String lineIn, lineOut;
            do {
                lineIn  = inputReader.readLine();
                lineOut = SYNTAX_ERROR_STRING;	// default value for for what we return, assume "unknown input"
                
                // Split the input line into constituent string "token" (individual words
                // separated by spaces
                String[] lineInTokens = lineIn.split(TOKEN_SEPERATOR);
                // TODO : check for null pointers in 'lineInTokens' here
                
                // If the line isn't blank...
                if (lineInTokens.length >= 1) {
                	
                	// Check for one of the supported "verbs" and execute functionality
                	// based on the verbs
                	if (lineInTokens[VERB_INDEX].equals(commandVerbs.SET.toString())) {
                		if (lineInTokens.length >= 3) {
                			keyValueStore.put(lineInTokens[KEY_INDEX], lineInTokens[ARG1_INDEX]);
                			lineOut = OPERATION_SUCCESSFUL_STRING;
                		}
                	} else if (lineInTokens[VERB_INDEX].equals(commandVerbs.GET.toString())) {
                		if (lineInTokens.length == 2) {
                			lineOut = keyValueStore.get(lineInTokens[KEY_INDEX]);
                		}
                	} else if (lineInTokens[VERB_INDEX].equals(commandVerbs.DEL.toString())) {
                		if (lineInTokens.length == 2) {
                			keyValueStore.remove(lineInTokens[KEY_INDEX]);
                			lineOut = OPERATION_SUCCESSFUL_STRING;
                		}
                	} else if (lineInTokens[VERB_INDEX].equals(commandVerbs.DBSIZE.toString())) {
                		lineOut = Integer.toString(keyValueStore.size());
                	} else if (lineInTokens[VERB_INDEX].equals(commandVerbs.INCR.toString())) {
                		if (lineInTokens.length == 2) {
                			// if the key doesn't exist, set it to zero before performing the operation.
                			// if the key does exist, but can't be converted to an integer, return an error
                			// Since read-modify-write isn't atomic, we need to "lock" the database while
                			// we perform the operation so as to not violate consistency
                			synchronized(keyValueStore) {
                				String stringValue = keyValueStore.get(lineInTokens[KEY_INDEX]);
                				if (stringValue == null) {
                					keyValueStore.put(lineInTokens[KEY_INDEX], "1");	// 0 + 1
                					lineOut = OPERATION_SUCCESSFUL_STRING;
                				} else {
	            					int numericValue = 0;
	                				try {
	                					numericValue = Integer.parseInt(stringValue);
	                					keyValueStore.put(lineInTokens[KEY_INDEX], Integer.toString(numericValue + 1));
	                					lineOut = OPERATION_SUCCESSFUL_STRING;
	                				} catch (NumberFormatException e) {
	                					// it wasn't a numeric value, return an error
	                					lineOut = ILLEGAL_OPERATION_STRING;
	                				}
                				}
                			}
                		}
                	} else if (lineInTokens[VERB_INDEX].equals(commandVerbs.STOP.toString())) {
                		lineOut = OPERATION_SUCCESSFUL_STRING;
                	}
                }
                
                writeln(lineOut, output);
            } while (! lineIn.equals(commandVerbs.STOP.toString()));

            output.close();
            input.close();
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

    private static void writeln(String string, OutputStream output) throws IOException {
        String outLine = string + "\n";
        output.write(outLine.getBytes(Charset.forName("UTF-8")));
    }
}
