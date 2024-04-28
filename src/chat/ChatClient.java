// Holly Jordan
package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

import encryption.Encryption;


public class ChatClient extends JFrame {

	private static final String RSA = "RSA";
	private static final String SERVER_PUBLIC_KEY = "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgGk9wUQ4G9PChyL5SUkCyuHjTNOglEy5h4KEi0xpgjxi/UbIH27NXLXOr94JP1N5pa1BbaVSxlvpuCDF0jF9jlZw5IbBg1OW2R1zUACK+NrUIAYHWtagG7KB/YcyNXHOZ6Icv2lXXd7MbIao3ShrUVXo3u+5BJFCEibd8a/JD/KpAgMBAAE=";
	private PublicKey serverPublicKey;
	private Key communicationKey;
	private JTextArea textArea = new JTextArea();
	private JTextField typingArea = new JTextField();
	private static final String SERVER_ADDRESS = "localhost";
	private static final int SERVER_PORT = 9898;
	private static String DOT = "\u2022";
	private int tryNum = 0;
	private static final int RETRY_LIMIT = 10;
	private static final int SLEEP_TIME = 1000;
    DataInputStream inHandshakeStream;
    DataOutputStream outHandshakeStream;
    JLabel currentStatus = new JLabel();
    JLabel statusLight = new JLabel();
    JMenuItem connectItem = new JMenuItem();
    private static final Color DARK_GREEN = Color.decode("#16ba27");
    private static final Color BURNT_ORANGE = Color.decode("#d16b11");
    private static final Color ORANGE = Color.decode("#ffa305");



	public ChatClient() {
		super("Chat Client");
		try {
			serverPublicKey = Encryption.readPublicKey(SERVER_PUBLIC_KEY);			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("error getting server public key: " + e.getMessage());
		}
		createGUI();
	}
	
	private void createGUI() {
		// Create the text area
        this.textArea = new JTextArea();
        this.textArea.setEditable(false); // Make it non-editable
        // Create a scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(this.textArea);

        /* TYPING PANEL */
		// Create the text area
        this.typingArea = new JTextField("\n");
        typingArea.setPreferredSize(new Dimension(400, 25));
        this.typingArea.setEditable(true);
        
        typingArea.addKeyListener(new KeyListener() {

			@Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    try {
                    	// Send the message to the server
                    	DataOutputStream outStream = getHandshakeOutputStream();
                    	Key aesKey = getCommunicationKey();
	                    String encryptedMessage;

						encryptedMessage = Encryption.encrypt(aesKey, getMessageFromTypingField());
                    	outStream.writeUTF(encryptedMessage);
                    	clearTypingArea();
					
					} catch (IOException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException
							| InvalidAlgorithmParameterException | BadPaddingException
							| IllegalBlockSizeException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
            }

			@Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        JPanel typingPanel = new JPanel();
        typingPanel.setLayout(new FlowLayout());
        typingPanel.add(typingArea);
		this.typingArea.setEnabled(false);

        
        
        /* STATUS PANEL */
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout());
        
        // Status Light
        statusLight = new JLabel(DOT);
        statusLight.setPreferredSize(new Dimension(20, 25));
        statusLight.setFont(new Font("Arial", Font.PLAIN, 50));
        statusLight.setForeground(Color.RED);
        statusPanel.add(statusLight);

        // Status: Label
        JLabel statusLabel = new JLabel("Status: ");
        statusLabel.setPreferredSize(new Dimension(50, 25));
        statusPanel.add(statusLabel);
        
        // Current Status
        currentStatus.setPreferredSize(new Dimension(200, 25));
        // Default status is Disconnected
        setStatus("Disconnected", Color.RED);
        statusPanel.add(currentStatus);

        // Clear Button
        JButton clearButton = new JButton("Clear");
	    clearButton.addActionListener(e -> clearTypingArea());
	    clearButton.setPreferredSize(new Dimension(75, 25));
        statusPanel.add(clearButton);
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add(typingPanel, BorderLayout.NORTH);
        southPanel.add(statusPanel, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        
        /* MENU BAR */
        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        connectItem = new JMenuItem("Connect");


        // Add action listeners to menu items
        connectItem.addActionListener(e -> {
            connectToServer();
        });
        exitItem.addActionListener(e -> {
            System.exit(0); // Exit the application
        });

        // Add menu items to the file menu
        fileMenu.add(connectItem);
        fileMenu.add(exitItem);

        // Add file menu to the menu bar
        menuBar.add(fileMenu);

        // Set the menu bar for the frame
        setJMenuBar(menuBar);

        // Set frame properties
        setSize(400, 300); // Set your preferred size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true); // Make the frame visible
        setAlwaysOnTop(true);

	}
	
	public void setStatus(String status, Color color) {
        this.currentStatus.setText(status);
        this.currentStatus.setForeground(color);
        this.statusLight.setForeground(color);
	}
	
	public void setStatus(String status, Color color, Color color2) {
        this.currentStatus.setText(status);
        this.currentStatus.setForeground(color);
        this.statusLight.setForeground(color2);
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
				this.typingArea.setEnabled(true);

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
                
                SwingUtilities.invokeLater(() -> { 
                	setStatus("Connected as User: "+myClientNum, DARK_GREEN, Color.GREEN); 
                	// Only allow one connection per chat client
                	this.connectItem.setEnabled(false); 
                }); // end invoke later
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
						String decryptedMessage = Encryption.decrypt(key, msgFromServer);
						System.out.println("decrypted string is: " + decryptedMessage);
						
		                SwingUtilities.invokeLater(() -> { textArea.append(decryptedMessage + "\n"); });
					} catch (IllegalArgumentException e) {
						System.err.println("Invalid encrypted message format: " + e.getMessage());
					}
					catch (EOFException eof) {
						System.out.println("End of file/ socket closed.");
						setStatus("Server closed, disconnected", Color.RED);
						this.typingArea.setEnabled(false);
						this.connectItem.setEnabled(true);
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
                    setStatus("Unable to connect to server", BURNT_ORANGE, ORANGE);
                    // wait
                    try {
    					Thread.sleep(SLEEP_TIME);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                    setStatus("Retrying connection "+tryNum+" of "+RETRY_LIMIT+"...", BURNT_ORANGE, ORANGE);

                    // Allow the client to retry connections until successful
                    this.connectItem.setEnabled(true); 
                    
                    connectToServer();
                });
        	}
        	else {
        		tryNum = 0;
        		SwingUtilities.invokeLater(() -> { 
                    // Update the status in the client
                    setStatus("Connection error", Color.RED);
                    // Allow the client to retry connections until successful
                    this.connectItem.setEnabled(true); 
                });
        	}
        	return;
        }
        
        
        catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> { 
                // Update the status in the client
                setStatus("Connection error", Color.RED);
                // Allow the client to retry connections until successful
                this.connectItem.setEnabled(true); 
            });
        }
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
	
	private String getMessageFromTypingField() {
		return this.typingArea.getText().trim();
	}
	
    private void clearTypingArea() {
        typingArea.setText("");
    }
	
	public static void main(String[] args) {
		ChatClient chatClient = new ChatClient();
	}
}
