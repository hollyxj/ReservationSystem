// Holly Jordan
package server;
import database.*;
import encryption.*;
import gui.Communicator;
import gui.MainFrame;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;

import java.security.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class RezServer extends JFrame {

	private static final String RSA = "RSA";
	private Key privateKey;
	private JTextArea textArea = new JTextArea();
	private static final int PORT = 9898;
	private int clientNum = 0;        // Create a HashMap with Integer keys and DataOutputStream values
    HashMap<Integer, DataOutputStream> clientMap = new HashMap<>();
    HashMap<Integer, Key> clientKeys = new HashMap<>();
    
    private ReservationDB db = null;

	public RezServer() {
		super("ReZerver (aka The Server)");
		try {
			privateKey = Encryption.readPrivateKey("keypairs/pkcs8_key");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("problem loading private key: " + e.getMessage());
			System.exit(1);
		}
		createGUI(); // Create the server GUI
        startServer(); // Start the server when the server is created
	}
	
    public ReservationDB getDB() {
		// Like the Public Constructor
    	if (this.db == null) {
    		this.db = new ReservationDB();
    	}
    	return this.db;
    }
	
	private void createGUI() {
		// Create the text area
        this.textArea = new JTextArea();
        this.textArea.setEditable(false); // Make it non-editable

        // Create a scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(this.textArea);

        // Add the scroll pane to the center of the frame using BorderLayout
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Set frame properties
        setSize(400, 300); // Set your preferred size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Make the frame visible
//        setAlwaysOnTop(true);
	}
	
	public String getDate() {
		// Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Get the system's default time zone
        ZoneId zoneId = ZoneId.systemDefault();

        // Create a DateTimeFormatter for the desired format, including the time zone
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");

        // Format the current date and time along with the time zone using the formatter
        String formattedDateTime = now.format(formatter.withZone(zoneId));
        
        return formattedDateTime;
	}
	
	public String getStartString() {
        // Print the formatted date, time, and time zone
        String startString = "Reservation server started at " + getDate();
        System.out.println(startString);
       
        // Return the formatted date and time
        return startString;
        
	}
	
	public void printMessageOnServerConsole(String message) {
		// Broadcast a message in the server GUI
		SwingUtilities.invokeLater(() -> {
			textArea.append(message+"\n");
		});
		System.out.println("[printMessageOnServerConsole]=\'"+message+"\'");		
	}
	
	public void broadcastMessage(String message, int localClientNum) {
		// Broadcast a message to all the clients
		System.out.println("RezServer:[broadcast message][message]:\'"+message+"\'");
		try {			
	        for (HashMap.Entry<Integer, DataOutputStream> entry : this.clientMap.entrySet()) {
	            int clientNum = entry.getKey();
	            DataOutputStream outputStream = entry.getValue();
	            // Decrypt using the sender's key
	            Key senderKey = clientKeys.get(clientNum);

	            String msgToEncrypt;
	            if (localClientNum == clientNum) {
	            	msgToEncrypt = message;
	            }
	            else {
	            	msgToEncrypt = localClientNum + ": "+ message;
	            }
	            
	            // Encrypt message with AES key before broadcasting
				try {
					// @TODO Maybe we don't have to broadcast anything back to the communicator
					// Might be useful later... time will tell...
					String encryptedMessage = Encryption.encrypt(senderKey, msgToEncrypt);
					outputStream.writeInt(localClientNum);
//		            outputStream.writeUTF("RezServer:[broadcastMessage]:TESTING");
//		            outputStream.writeUTF("RezServer:[broadcastMessage]:[encryptedMessage]:"+encryptedMessage);

		            outputStream.writeUTF(encryptedMessage);
		            
		            
		            
				} catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
						| InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	            
	        }
	       
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    private void startServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT); // Listen on port 9898
                
                printMessageOnServerConsole(getStartString() + "\nListening on port 9898...");

                while (true) {
                    Socket socket = serverSocket.accept(); // Accept incoming connections
                	this.clientNum++;

                    printMessageOnServerConsole("Starting thread for client "+this.clientNum+" at "+getDate());
                    printMessageOnServerConsole("Client "+this.clientNum+"'s host name is "+socket.getInetAddress().getHostName());
                    printMessageOnServerConsole("Client "+this.clientNum+"'s IP Address is "+socket.getInetAddress());
                    
                    handleClient(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void handleClient(Socket socket) {
        try {
        	DataInputStream inHandshakeStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outHandshakeStream = new DataOutputStream(socket.getOutputStream());
            this.clientMap.put(clientNum, outHandshakeStream);
            int localClientNum = this.clientNum;
            
            // Perform handshake
            String helloMessage = inHandshakeStream.readUTF();
            if ("HELLO".equals(helloMessage)) {
                outHandshakeStream.writeUTF("CONNECTED");
                outHandshakeStream.writeInt(this.clientNum);
            }
            
            // Read the encrypted AES seed from the client
            int encryptedSeedLength = inHandshakeStream.readInt();
            byte[] encryptedSeed = new byte[encryptedSeedLength];
            inHandshakeStream.readFully(encryptedSeed);
            
            // Decrypt the AES seed using the server's private key
            byte[] aesSeed = Encryption.pkDecrypt(privateKey, encryptedSeed);
            
            // Generate AES key from the decrypted seed
            Key aesKey = Encryption.generateAESKey(aesSeed);
            System.out.println("server key is: " + aesKey);
            this.clientKeys.put(clientNum, aesKey);

            
            new Thread(() -> {
                // Continue reading and broadcasting messages
                while (true) {
                	DataInputStream inStream = inHandshakeStream;
					try {
						String message = inStream.readUTF();
						System.out.println("Message from client: \'" + message + "\'");

	                    String decryptedMessage = Encryption.decrypt(aesKey, message);
						System.out.println("decrypted string is: " + decryptedMessage);
	                    
	                    // Broadcast encrypted message to all connected clients	
	                    broadcastMessage(decryptedMessage, localClientNum);
	                    
	                    // ADD IN COMMANDS HERE
	                    
	                    
	                    
	                    
	                    
	                    
	                    
	                    // Broadcast encrypted message to all connected clients	
	                    printMessageOnServerConsole(decryptedMessage);
	                    
	                    // Process/Decode the message
	                    String command = decryptedMessage;
	                    processMessageFromCommunicator(command);
	                    
					} catch (EOFException eof) {
	                    System.out.println("Client " + localClientNum + " disconnected.");
	                    printMessageOnServerConsole("Client " + localClientNum + " disconnected.");
	                    
						return;
					} catch (IOException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }).start();

        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }
    
    private void processMessageFromCommunicator(String cmd) {
		System.out.println("**********");

		System.out.println("In processMessageFromCommunicator");
		System.out.println("[cmd]="+cmd);


    	ReservationDB db = getDB();
    	
    	String[] parts = cmd.split(",");
    	String function = parts[0];
    	System.out.println("RezServer:[processMessageFromCommunicator]:[function]:" + function);
    	String status = null;
    	
    	String name = null;
    	String email = null;
    	String pwd = null;
    	String encryptedPwd = null;
    	String decryptedPwd = null;
    	String msg = null;
    	
    	try { 
    		switch (function) {
	    		case "addUser": 
	    			// for Sign Up
	    			System.out.println("In case addUser");

	    			name = parts[1];
	    			email = parts[2];
	    			pwd = parts[3];    		
	    			
	    			
	    			if (!name.equals("") && !email.equals("") && !pwd.equals("")) {
		    			encryptedPwd = PKCS5.encrypt(pwd);
		    			// Send to DB
		    			db.addUserToDB(name, email, encryptedPwd); // send encrypted password
		    			System.out.println("[encryptedPwd]="+encryptedPwd);
	    			}
	    			else {
	    				// name/ email/ password are null
	    				status = generateErrorStatus("Name, email, and password required.\nPlease enter a valid name, email, and password and try again.");			
						System.err.println(status);
						broadcastMessage(status,getClientNum());
	    			}
	    			break; // end case addUser
	   
	    			
	    		case "authenticate":
	    			// for Sign In
	    			System.out.println("In case authenticate:");
	    			email = parts[1];
	    			System.out.println("[email]="+email);
	    			pwd = parts[2];
	    			System.out.println("[pwd]="+pwd);

	    			

	    			if ((!email.equals("") && !pwd.equals(""))) {
		    			encryptedPwd = db.getEncryptedPasswordFromDB(email);
		    			System.out.println("[encryptedPwd]="+encryptedPwd);
		
		    			decryptedPwd = PKCS5.decrypt(encryptedPwd, pwd);
		    			System.out.println("\t[decryptedPwd]="+decryptedPwd);
		    			System.out.println("\t[pwd]="+pwd);
		
		    			
						if (decryptedPwd.equals(pwd)) {
							// Good - Valid credentials
							status = generateAlertStatus("Login Successful");
							System.out.println(status);
							broadcastMessage(status,getClientNum());
						} else {
							// Bad - Not valid credentials
							status = generateErrorStatus("Invalid User Credentials");
							System.err.println(status);
							broadcastMessage(status,getClientNum());
						}
    				}
	    			else {
	    				// email/ password are null
	    				status = generateErrorStatus("Email and password required.\nPlease enter a valid email and password and try again.");			
						System.err.println(status);
						broadcastMessage(status,getClientNum());

	    			}	
    				break; // end case authenticate
    				
	    		case "":
	    			status = generateErrorStatus("RezServer:[processMessageFromCommunicator]: case Null");
					System.err.println(status);
					
					broadcastMessage(status,getClientNum());
	    			break;
	    		// case "scheduleAppointment"
    		
    				
	    		default:
					status = generateIgnoreStatus("Unrecognized command \'" + cmd + "\'");
					System.err.println(status);
	    			System.out.println("[processMessageFromCommunicator]: Unrecognized command \'" + cmd + "\'");
	    			break;
    			
    		} // end Switch
    		
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// @TODO: Figure out client num
			broadcastMessage("Something went wrong. Please try again", getClientNum());
			status = generateErrorStatus("Something went wrong. Please try again");
			System.err.println(status);
		}
    }
    
	private String generateErrorStatus(String msg) {
		return "error,"+msg;
	}
	
	private String generateIgnoreStatus(String msg) {
		return "ignore,"+msg;
	}
	
	private String generateAlertStatus(String msg) {
		return "alert,"+msg;
	}

    
    public int getClientNum() {
    	return this.clientNum;
    }
    
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			RezServer server = new RezServer(); // Create an instance of Server on the Event Dispatch Thread
		});
	}	
}