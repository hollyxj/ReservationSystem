// Holly Jordan
package server;

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

import encryption.Encryption;

import java.security.*;
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


	public RezServer() {
	
		super("Server");

		try {
			privateKey = Encryption.readPrivateKey("keypairs/pkcs8_key");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("problem loading private key: " + e.getMessage());
			System.exit(1);
		}
		
		createGUI(); // Create the server GUI
        startServer(); // Start the server when the ChatServer is created
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
        setAlwaysOnTop(true);
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
        String startString = "Chat server started at " + getDate();
        System.out.println(startString);
       
        // Return the formatted date and time
        return startString;
        
	}
	
	public void writeMessageToServer(String message) {
		// Broadcast a message in the chat server
		SwingUtilities.invokeLater(() -> {
			textArea.append(message+"\n");
		});
		System.out.println("[writeMessageToServer]=\'"+message+"\'");		
	}
	
	public void broadcastMessage(String message, int localClientNum) {
		// Broadcast a message to all the clients
		System.out.println("broadcast message got: \'"+message+"\'");
		try {			
	        for (HashMap.Entry<Integer, DataOutputStream> entry : this.clientMap.entrySet()) {
	            int clientNum = entry.getKey();
	            DataOutputStream outputStream = entry.getValue();
	            // Decrypt using the sender's key
	            Key senderKey = clientKeys.get(clientNum);

	            String msgToEncrypt;
	            if (localClientNum == clientNum) {
	            	msgToEncrypt = "You: " + message;
	            }
	            else {
	            	msgToEncrypt = localClientNum + ": "+ message;
	            }
	            
	            // Encrypt message with AES key before broadcasting
				try {
					String encryptedMessage = Encryption.encrypt(senderKey, msgToEncrypt);
					outputStream.writeInt(localClientNum);
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
                
                writeMessageToServer(getStartString() + "\nListening on port 9898...");

                while (true) {
                    Socket socket = serverSocket.accept(); // Accept incoming connections
                	this.clientNum++;

                    writeMessageToServer("Starting thread for client "+this.clientNum+" at "+getDate());
                    writeMessageToServer("Client "+this.clientNum+"'s host name is "+socket.getInetAddress().getHostName());
                    writeMessageToServer("Client "+this.clientNum+"'s IP Address is "+socket.getInetAddress());
                    
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
	                    
					} catch (EOFException eof) {
	                    System.out.println("Client " + localClientNum + " disconnected.");
	                    writeMessageToServer("Client " + localClientNum + " disconnected.");
	                    
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
    
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			RezServer server = new RezServer(); // Create an instance of ChatServer on the Event Dispatch Thread
		});
	}	
}
