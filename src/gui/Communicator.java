// Communicates between MainFrame GUI and RezServer Server
package gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.SwingUtilities;

import encryption.Encryption;

public class Communicator {
    private static final String RSA = "RSA";
	private static final String SERVER_PUBLIC_KEY = "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgGk9wUQ4G9PChyL5SUkCyuHjTNOglEy5h4KEi0xpgjxi/UbIH27NXLXOr94JP1N5pa1BbaVSxlvpuCDF0jF9jlZw5IbBg1OW2R1zUACK+NrUIAYHWtagG7KB/YcyNXHOZ6Icv2lXXd7MbIao3ShrUVXo3u+5BJFCEibd8a/JD/KpAgMBAAE=";
	private PublicKey serverPublicKey;
	private Key communicationKey;
	private static final String SERVER_ADDRESS = "localhost";
	private static final int SERVER_PORT = 9898;
	DataInputStream inHandshakeStream;
    DataOutputStream outHandshakeStream;
    
    private int tryNum = 0;
	private static final int RETRY_LIMIT = 10;
	private static final int SLEEP_TIME = 1000;
	private static String DOT = "\u2022";
    private static Communicator mycommunicator = null;
    
    private Communicator() {
        try {
			serverPublicKey = Encryption.readPublicKey(SERVER_PUBLIC_KEY);
			connectToServer();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("error getting server public key: " + e.getMessage());
		}
    }
    
    public static Communicator getCommunicator() {
    	// Like the Public Constructor
    	if (mycommunicator == null) {
    		mycommunicator = new Communicator();
    	}
    	return mycommunicator;
    }
    
	public void addUser(String name, String email, String pwd, Boolean isSelected) {
		System.out.println("Formatting string.\n");
        String submittedStr = "addUser," // action called by Server
        					  +  name + ","
        					  +  email + ","
        					  +  pwd + ","
        					  +  isSelected + "\n";
        System.out.println("Communicator:[addUser]="+submittedStr);
        
        sendMessageToServer(submittedStr);
        // add the user to the Database
	}
	
	public void addAvailability(String time, String date, String appointmentType, String who, String notes, String shortDesc) {
		System.out.println("Formatting string.\n");
        String submittedStr = "addAvailability," // action called by Server
        					  +  time + ","
        					  +  date + ","
        					  +  appointmentType + ","
        					  +  who + ","
        					  +  notes + ","
        					  +  shortDesc + "\n";
        System.out.println("Communicator:[addAvailability]="+submittedStr);
 
        sendMessageToServer(submittedStr);
        // add the user to the Database
	}
	
	public void authenticate(String email, String pwd) {
        String submittedStr = "authenticate," // action called by Server
        					  +  email + ","
        					  +  pwd + "\n";
        System.out.println("Communicator:[authenticate]="+submittedStr);
        sendMessageToServer(submittedStr);
	}
	
	public void generateJSON() {
		String submittedStr = "generateJSON," + "\n";
        System.out.println("Communicator:[generateJSON]="+submittedStr);
        sendMessageToServer(submittedStr);
	}
	
	public void loadDBFromJSON(String filePath) {
	    System.out.println("Formatting string.\n");
	    String submittedStr = "loadDBFromJSON," + filePath + "\n";
	    System.out.println("Communicator:[loadDBFromJSON]=" + submittedStr);
	    sendMessageToServer(submittedStr);
	}

    public void sendMessageToServer(String msg) {
    	// Send the message to the server
    	DataOutputStream outStream = getHandshakeOutputStream();
    	Key aesKey = getCommunicationKey();
        String encryptedMessage;

		try {
			encryptedMessage = Encryption.encrypt(aesKey, msg);
			outStream.writeUTF(encryptedMessage);
		} catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
				| InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            this.inHandshakeStream = new DataInputStream(socket.getInputStream());
            this.outHandshakeStream = new DataOutputStream(socket.getOutputStream());

            this.outHandshakeStream.writeUTF("HELLO"); // Send handshake message

            String reply = inHandshakeStream.readUTF(); // Wait for server's reply
            int myClientNum = inHandshakeStream.readInt();
            if ("CONNECTED".equals(reply)) {
            	System.out.println("Connected as User: "+myClientNum);

                // Generate AES seed and encrypt it with the server's public key
                byte[] aesSeed = Encryption.generateSeed();
                byte[] encryptedKey = Encryption.pkEncrypt(serverPublicKey, aesSeed);
                                
                // Send the length of the encrypted seed
                this.outHandshakeStream.writeInt(encryptedKey.length);
                // Send the encrypted seed
                this.outHandshakeStream.write(encryptedKey);

                // Set the communication key for AES encryption/decryption
                this.communicationKey = new SecretKeySpec(aesSeed, "AES");
                System.out.println("client key is: " + this.communicationKey);
                
//                SwingUtilities.invokeLater(() -> { 
//                	setStatus("Connected as User: "+myClientNum, DARK_GREEN, Color.GREEN); 
//                	// Only allow one connection per chat client
//                	this.connectItem.setEnabled(false); 
//                }); // end invoke later
            }
            
            new Thread(() -> {
            	// Continuously read messages from the server
	            while (true) {
	            	// Message from the server to broadcast to all clients
	            	DataInputStream inputStream = getHandshakeInputStream();
//	                String msgFromServer;
	            	
					try {
						int localClientNum = inputStream.readInt();

						String msgFromServer = inputStream.readUTF();
						System.out.println("msgFromServer: " + msgFromServer);

						// Decrypt message here
						Key key = getCommunicationKey();
//						System.out.println("key used for decryption: "+key);
						String status = Encryption.decrypt(key, msgFromServer);
						System.out.println("status from server is: " + status);
						
						
						
						
						
						
    					parseStatus(status);	
    					
    					
    					
    					
    					
						
//		                SwingUtilities.invokeLater(() -> { textArea.append(decryptedMessage + "\n"); });
					} catch (IllegalArgumentException e) {
						System.err.println("Invalid encrypted message format: " + e.getMessage());
					}
					catch (EOFException eof) {
						System.out.println("End of file/ socket closed.");
//						setStatus("Server closed, disconnected", Color.RED);
//						this.typingArea.setEnabled(false);
//						this.connectItem.setEnabled(true);
						return;
					} catch (IOException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException| InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
            }).start();
        
        } catch (ConnectException ce) {
        	
        	System.out.println("Failed to connect, retry "+tryNum+" of "+RETRY_LIMIT+"...");
        	this.tryNum++;

        	if(tryNum <= RETRY_LIMIT) {
        		SwingUtilities.invokeLater(() -> { 
                    // Update the status in the client
//                    setStatus("Unable to connect to server", BURNT_ORANGE, ORANGE);
                    // wait
                    try {
    					Thread.sleep(SLEEP_TIME);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
//                    setStatus("Retrying connection "+tryNum+" of "+RETRY_LIMIT+"...", BURNT_ORANGE, ORANGE);

                    // Allow the client to retry connections until successful
//                    this.connectItem.setEnabled(true); 
                    
                    connectToServer();
                });
        	}
        	else {
        		tryNum = 0;
        		SwingUtilities.invokeLater(() -> { 
                    // Update the status in the client
//                    setStatus("Connection error", Color.RED);
                    // Allow the client to retry connections until successful
//                    this.connectItem.setEnabled(true); 
                });
        	}
        	return;
        }
        
        
        catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> { 
                // Update the status in the client
//                setStatus("Connection error", Color.RED);
                // Allow the client to retry connections until successful
//                this.connectItem.setEnabled(true); 
            });
        }
    }
    
    private String parseStatus(String msg) {
    	System.out.println("In Communicator:parseStatus");
    	String[] parts = msg.split(",");
    	String function = parts[0];
    	System.out.println("Communicator:[parseStatus]:[function]:" + function);

    	// Remove the 'function' from the message and send the rest of the string in the pop up
    	String toReplace = parts[0]+",";
    	String status = msg.replace(toReplace, "");
    	try { 
    		switch (function) {
	    		case "ignore": 
	    			// do nothing
	    			System.out.println("Communicator:[parseStatus]:[ignore]:"+status);
	    			break;
	    		
	    		case "alert":
	    			// Show an alert in the mainframe
	    			System.out.println("Communicator:[parseStatus]:[alert]:"+status);
					MainFrame.sendAlert(status);
	    			break;
	    			
	    		case "error":
	    			// Show an error in the mainframe
	    			System.err.println("Communicator:[parseStatus]:[error]:"+status);
					MainFrame.sendError(status);
	    			break;
	    			
	    		default:
	    			System.out.println("Communicator:[parseStatus]: Unrecognized status \'" + status + "\'");
	    			break;
	    			
    		} // end switch
    	} catch (Exception e) {
    		
    	}
    	
    	return "";
    	
    }
    
    private Key getCommunicationKey() {
		return this.communicationKey;
	}

	private DataInputStream getHandshakeInputStream() {
		return this.inHandshakeStream;
	}
	
	private DataOutputStream getHandshakeOutputStream() {
		return this.outHandshakeStream;
	}
	
    
}
