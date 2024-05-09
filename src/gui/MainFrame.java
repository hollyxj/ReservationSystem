package gui;
import server.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import encryption.Encryption;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    	
	private SignUp signUpPage;
	private LogIn logInPage;
    private Welcome welcomePage;
    private ScheduleAppointment schedulePage;
    private EditAvailability editAvailPage;
    
    // TM:   ™
    public MainFrame() {
        super("Rezerve™");
		createGUI();
    }
    
    private void createGUI() {
    	this.signUpPage = new SignUp();
        this.logInPage = new LogIn();
        this.welcomePage = new Welcome();
        this.schedulePage = new ScheduleAppointment();
        this.editAvailPage = new EditAvailability();

        // Initialize the Sign In page as the initial view
        setContentPane(this.welcomePage);

        // Create menu items
        JMenuItem welcomeItem = new JMenuItem("Welcome");
        JMenuItem signUpItem = new JMenuItem("Sign Up");
        JMenuItem signInItem = new JMenuItem("Log In");
        JMenuItem scheduleItem = new JMenuItem("Schedule Appointment");
        JMenuItem editItem = new JMenuItem("Edit Availability");
        JMenuItem exitItem = new JMenuItem("Exit");

        // Implement menu functionality
        signUpItem.addActionListener(e -> {
        	// Sign in page
        	switchToSignUp();
            this.signUpPage.setSavedState((JPanel) getContentPane());
        });
        signInItem.addActionListener(e -> {
        	// Sign in page
        	switchToSignIn();
            this.logInPage.setSavedState((JPanel) getContentPane());
        });
        welcomeItem.addActionListener(e -> {
        	// Welcome page
        	switchToWelcome();
        	this.welcomePage.setSavedState((JPanel) getContentPane());
        });
        scheduleItem.addActionListener(e -> {
        	// Schedule appointment page
        	switchToScheduleAppointment();
        	this.schedulePage.setSavedState((JPanel) getContentPane());

        });
        editItem.addActionListener(e -> {
        	// Schedule appointment page
        	switchToEditAvailability();
        	this.schedulePage.setSavedState((JPanel) getContentPane());

        });
        exitItem.addActionListener(e -> System.exit(0)); // Exit the application
        
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menu.add(welcomeItem);
        menu.add(signUpItem);
        menu.add(signInItem);
        menu.add(scheduleItem);
        menu.add(editItem);
        menu.addSeparator();
        menu.add(exitItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        
        
        // Load the image icon from the "resources" folder
//        URL imageUrl = MainFrame.class.getResource("/resources/Rezerve2.jpg");
//        if (imageUrl != null) {
//            ImageIcon icon = new ImageIcon(imageUrl);
//            setIconImage(icon.getImage());
//        } else {
//            System.err.println("Error loading image icon.");
//        }
      
//        ImageIcon icon = new ImageIcon("/resources/Rezerve2.jpg");
        String cwd = System.getProperty("user.dir");
        System.out.println("Current Working Directory: " + cwd);
        // Prints "Current Working Directory: /Users/hollyjordan/git/ReservationSystem"
        
        // Set the custom icon for the JFrame
//        setIconImage(icon.getImage());
        
        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(750, 500);
        setLocationRelativeTo(null);
        setVisible(true);
   
//        setAlwaysOnTop(true);
    }
    
    public static void sendAlert(String msg) {
//		JOptionPane.showMessageDialog(null, msg); //, "Alert", JOptionPane.INFORMATION_MESSAGE);

		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(null, msg, "Alert", JOptionPane.INFORMATION_MESSAGE);
		});
    }
    
    public static void sendError(String msg) {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
		});
    }
    
    private void switchToSignIn() {
        setContentPane(this.logInPage.getSavedState());
        validate();
        repaint();
    }
    
    private void switchToSignUp() {
        setContentPane(this.signUpPage.getSavedState());
        validate();
        repaint();
    }
    
    public void switchToWelcome() {
        setContentPane(this.welcomePage.getSavedState());
        validate();
        repaint();
    }

    private void switchToScheduleAppointment() {
        setContentPane(this.schedulePage.getSavedState());
        validate();
        repaint();
    }
        
    private void switchToEditAvailability() {
    	setContentPane(this.editAvailPage.getSavedState());
        validate();
        repaint();
    }
    
    public void switchTo(String page) {
    	switch(page) {
    		case "welcome":
    			switchToWelcome();
    			break;
    			
    		default:
    			break;
    	} // end switch
     }
    
    public static void main(String[] args) {
    	MainFrame mf = new MainFrame();
    }
}