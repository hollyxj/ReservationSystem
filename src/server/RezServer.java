// Holly Jordan
package server;
import database.*;
import encryption.*;
import gui.Communicator;
import gui.MainFrame;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.security.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.google.gson.*;
import java.io.File;


public class RezServer extends JFrame {

	private static final String RSA = "RSA";
	private Key privateKey;
	private JTextArea textArea = new JTextArea();
	private static final int PORT = 9898;
	private int clientNum = 0;        // Create a HashMap with Integer keys and DataOutputStream values
    HashMap<Integer, DataOutputStream> clientMap = new HashMap<>();
    HashMap<Integer, Key> clientKeys = new HashMap<>();
    private static RezServer instance;

    
	private static String signedInName;
	private static String signedInEmail;
    private static Boolean userIsLoggedIn;
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
    	String admin = null;
    	String appointmentID = null;
    	String appointments = null;
    	String encryptedPwd = null;
    	String decryptedPwd = null;
    	String msg = null;
    	JsonArray availabilityArray = null;
    	String time = null;
		String date = null;
		String appointmentType = null;
		String who = null;
		String notes = null;
		String shortDescription = null;
    	
    	
    	try { 
    		switch (function) {
	    		case "addUser": 
	    			// for Sign Up
	    			System.out.println("In case addUser");

	    			name = parts[1];
	    			email = parts[2];
	    			pwd = parts[3];    		
	    			admin = parts[4];
	    			Boolean isAdmin;
	    			appointments = parts[5];
	    			
	    			if (admin.equals("true")) {
	    				isAdmin = true;
	    			} else {
	    				isAdmin = false;
	    			}
	    			
	    			if (name.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
	    				// name/ email/ password are empty
	    				status = generateErrorStatus("Name, email, and password required.\nPlease enter a valid name, email, and password and try again.");			
						System.err.println(status);
						broadcastMessage(status,getClientNum());
	    			}
	    			else {
	    				encryptedPwd = PKCS5.encrypt(pwd);
		    			System.out.println("[encryptedPwd]="+encryptedPwd);

		    			// Send to DB
		    			db.addUserToDB(name, email, encryptedPwd, isAdmin, appointments); // send encrypted password
		    		
		    			status = generateAlertStatus("Account created!");			
						System.out.println(status);
						broadcastMessage(status,getClientNum());	
	    			}
	    			
	    			break; // end case addUser
	    		// ********************************************************
	    		
	    		case "authenticate":
	    			// for Log In
	    			System.out.println("In case authenticate:");
	    			email = parts[1];
	    			System.out.println("[email]="+email);
	    			pwd = parts[2];
	    			System.out.println("[pwd]="+pwd);
	    			String password = pwd.trim();

	    			
	    			if ((email.isEmpty() || pwd.isEmpty())) {
	    				// email/ password are null
	    				status = generateErrorStatus("Email and password required.\nPlease enter a valid email and password and try again.");			
						System.err.println(status);
						broadcastMessage(status,getClientNum());

    				}
	    			else {
	    				encryptedPwd = db.getEncryptedPasswordFromDB(email);
		    			System.out.println("[encryptedPwd]="+encryptedPwd);
		
		    			if (Objects.isNull(encryptedPwd)) {
		    				status = generateErrorStatus("User not found.\nNeed to create an account? Go to \"Menu\" > \"Sign Up\".");			
							System.err.println(status);
							broadcastMessage(status,getClientNum());
							break;
		    			}
		    			
		    			try {
			    			decryptedPwd = PKCS5.decrypt(encryptedPwd, password);
			    			System.out.println("\t[decryptedPwd]="+decryptedPwd);
			    			System.out.println("\t[pwd]="+pwd);
			    			System.out.println("\tdecryptedPwd.trim().equals(pwd.trim()]="+decryptedPwd.trim().equals(pwd.trim()));

			    			
			    			
							if (decryptedPwd.trim().equals(pwd.trim())) {
								// Good - Valid credentials
								status = generateAlertStatus("Login Successful!");
								System.out.println(status);
								broadcastMessage(status,getClientNum());
							} else {
								// Bad - Not valid credentials
								status = generateErrorStatus("Invalid User Credentials. Please try again.");
								System.err.println(status);
								broadcastMessage(status,getClientNum());
							}
		    			} catch (Exception e4) {
		    				e4.printStackTrace();
		    				status = generateErrorStatus("Invalid User Credentials. Please try again.");
							System.err.println(status);
							broadcastMessage(status,getClientNum());
		    			}
	    			}	
    				break; // end case authenticate
    			// ********************************************************
	    			
    				
	    		case "updateAppointmentsColumn":
	    			email = parts[1];
	    			appointmentID = parts[2];
	    			
	    			db.updateAppointmentsColumn(email, appointmentID);
	    			
	    			break; // end case updateAppointmentsColumn
	    		// ********************************************************	
	    			
	    		case "addAvailability":
	    			System.out.println("In case addAvailability");
//	    			id = parts[];
	    			time = parts[1];
	    			date = parts[2];
	    			appointmentType = parts[3];
	    			who = parts[4];
	    			notes = parts[5];
	    			shortDescription = parts[6];
	    			
	    			
	    			if (time.isEmpty() || date.isEmpty() || appointmentType.isEmpty() ||
	    					who.isEmpty()) {
	    				// one of the required fields is null
	    				// NOTE: notes & short description fields not required
	    				status = generateErrorStatus("Time, Date, Appointment Type & who the appointment is with are required.\nPlease enter a valid time, date, appointment type, and name and try again.");			
						System.err.println(status);
						broadcastMessage(status,getClientNum());
	    			} else {
	    				// good
	    				// required fields are filled out! yay
		    			db.addAvailabilityToDB(time, date, appointmentType, who, notes, shortDescription); 
	    						    			
		    			status = generateAlertStatus("Availability added!");			
						System.out.println(status);
		    			System.out.println("RezServer:[authenticate status]=" + status + "\n\n\n");
						broadcastMessage(status,getClientNum());
	    			}

	    			break; // end addAvailability
	    		// ********************************************************
	    			
	    			
	    		case "generateJSON": 
	    			System.out.println("In case generate JSON:");

	    			// Call your Database method to get availability data as a JSON array
	    		    availabilityArray = db.getAvailabilityFromDB();

	    		    // Convert the JSON array to a JSON string using Gson
	    		    String jsonString = new Gson().toJson(availabilityArray);

	    		    // Write the JSON string to a file
	    		    try (FileWriter fileWriter = new FileWriter("availability.json")) {
	    		        fileWriter.write(jsonString);
	    		        System.out.println("JSON file updated successfully.");
		    		    status = generateAlertStatus("Load Successful");

	    		    } catch (IOException e) {
	    		        e.printStackTrace();
	    		        System.err.println("Error writing JSON file: " + e.getMessage());
		    		    status = generateErrorStatus("Error generating JSON file from Database");

	    		    } finally {
		    		    // Broadcast or use the status message as needed
		    		    status = generateAlertStatus("Load Successful");
		    		    broadcastMessage(status, getClientNum());
	    		    }
	    			break; // end generateJSON
	    		// ********************************************************
	    			
	    		case "loadDBFromJSON":
	    		    System.out.println("In case load DB from JSON:");

	    		    String filePath = parts[1];
	    		    String filePathStripped = filePath.trim();
	    		    System.out.println("[filePath]=\'" + filePathStripped + "\'");
	    		    
	    		    try (FileReader fileReader = new FileReader(filePathStripped)) {
	    		        // Read the JSON file contents into a String
	    		        StringBuilder jsonStringBuilder = new StringBuilder();
	    		        int character;
	    		        while ((character = fileReader.read()) != -1) {
	    		            jsonStringBuilder.append((char) character);
	    		        }
	    		        jsonString = jsonStringBuilder.toString();

	    		        // Parse the JSON string using JsonParser
	    		        availabilityArray = new JsonParser().parse(jsonString).getAsJsonArray();
	    		        int count=0;
	    		        
	    		        System.out.println("RezServer:[case loadDBFromJSON]:");
	    		        // Insert each appointment from JSON into the database
	    		        try {
	    		            for (JsonElement jsonElement : availabilityArray) {
	    		            	count++;
	    		                JsonObject jsonObject = jsonElement.getAsJsonObject();
	    		                time = jsonObject.get("time").getAsString();
	    		                date = jsonObject.get("date").getAsString();
	    		                appointmentType = jsonObject.get("appointmentType").getAsString();
	    		                who = jsonObject.get("who").getAsString();
	    		                notes = jsonObject.get("notes").getAsString();
	    		                shortDescription = jsonObject.get("shortDescription").getAsString();

	    		                String stringyy = "time=" // action called by Server
	    	        					  +  time + ", date="
	    	        					  +  date + ", appointmentType="
	    	        					  +  appointmentType + ", who="
	    	        					  +  who + ", notes="
	    	        					  +  notes + ", shortDescription="
	    	        					  +  shortDescription + "[done]";
	    		                System.out.println("Appointment (" + count + "/59) =["+stringyy+"]");
	    		                // Add appointment to database
	    		                db.addAvailabilityToDB(time, date, appointmentType, who, notes, shortDescription);
	    		            }
	    		            System.out.println("Appointments loaded from JSON to database successfully.");
	    		            status = generateAlertStatus("Load Successful");
	    		        } catch (SQLException e) {
	    		            e.printStackTrace();
	    		            System.err.println("Error adding appointments to database: " + e.getMessage());
	    		            status = generateAlertStatus("Error loading appointments from JSON to database");
	    		        }
	    		    } catch (IOException e) {
	    		        e.printStackTrace();
	    		        System.err.println("Error reading JSON file: " + e.getMessage());
	    		        status = generateErrorStatus("Error reading JSON file");
	    		    } finally {
	    		        broadcastMessage(status, getClientNum());
	    		    }

	    		    break; // end loadDBFromJSON
	    		// ********************************************************		

    				
	    		case "":
	    			status = generateErrorStatus("RezServer:[processMessageFromCommunicator]: case Null");
					System.err.println(status);
					
					broadcastMessage(status,getClientNum());
	    			break; // end case ""
	    		// ********************************************************

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
			status = generateErrorStatus("Something went wrong. Please try again");
			broadcastMessage(status, getClientNum());
			System.err.println(status);
		}
    }
    
    public void sendLoggedInUserInfo() {
    	String status = "getLoggedInUserInfo," +
						getSignedInName() + "," +
    					getSignedInEmail() + "," +
    					getUserIsLoggedIn();

    	broadcastMessage(status, getClientNum());
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
    
	
	public void setUserIsLoggedIn(Boolean isLoggedIn) {
		if (isLoggedIn) {
	        userIsLoggedIn = true;
		} else {
			 userIsLoggedIn = false;
		}
	}


	public Boolean getUserIsLoggedIn() {
	    return userIsLoggedIn;
	}
	
	
	public String getSignedInName() {
		return signedInName;
	}
	
	
	public void setSignedInName(String name) {
		 signedInName = name;
	}
	
	
	public String getSignedInEmail() {
		return signedInEmail;
	}
	
	
	public void setSignedInEmail(String email) {
		signedInEmail = email;
	}

	public int getClientNum() {
    	return this.clientNum;
    }
    
    public static RezServer getInstance() {
        if (instance == null) {
            instance = new RezServer();
        }
        return instance;
    }
    
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			RezServer server = getInstance(); // Create an instance of Server on the Event Dispatch Thread
		});
	}


}