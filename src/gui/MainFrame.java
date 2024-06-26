package gui;
import server.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private static Integer currentAptID;

    	
	private SignUp signUpPage;
	private LogIn logInPage;
    private Welcome welcomePage;
    private EditAvailability editAvailPage;
    private MyAppointments myAppointmentsPage;
    private  ScheduleAppointment schedulePage;

    
	private static String signedInName = null;
	private static String signedInEmail = null;
    private static Boolean userIsLoggedIn = false;
    

    private static JMenuBar menuBar = new JMenuBar();
    private static JMenu menu = new JMenu("Menu");

    private static JMenuItem scheduleItem = new JMenuItem("Schedule Appointment");
    private static JMenuItem myAppointmentsItem = new JMenuItem("My Appointments");
    
    
    // TM:   ™
    public MainFrame() {
        super("Rezerve™");
		createGUI();
    }
    
    private void createGUI() {
    	this.signUpPage = new SignUp();
        this.logInPage = new LogIn();
        this.welcomePage = new Welcome(); 
        schedulePage = new ScheduleAppointment(signedInEmail);
        this.myAppointmentsPage = new MyAppointments();
        this.editAvailPage = new EditAvailability();

        // Initialize the Sign In page as the initial view
        setContentPane(this.welcomePage);

        // Create menu items
        JMenuItem welcomeItem = new JMenuItem("Welcome");
        JMenuItem signUpItem = new JMenuItem("Sign Up");
        JMenuItem signInItem = new JMenuItem("Log In");

        JMenuItem editItem = new JMenuItem("Edit Availability");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        // Create menu bar
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");

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

        editItem.addActionListener(e -> {
        	// Schedule appointment page
        	switchToEditAvailability();
        	editAvailPage.setSavedState((JPanel) getContentPane());

        });
        scheduleItem.addActionListener(e -> {
        	// Schedule appointment page
        	switchToScheduleAppointment();
//        	this.schedulePage.setSavedState((JPanel) getContentPane());

        });
        myAppointmentsItem.addActionListener(e -> {
        	// Schedule appointment page
        	switchToMyAppointments();
        	this.myAppointmentsPage.setSavedState((JPanel) getContentPane());

        });
        
        exitItem.addActionListener(e -> System.exit(0)); // Exit the application
        
        
       
        menu.add(welcomeItem);
        menu.add(signUpItem);
        menu.add(signInItem);
        // if user is logged in
        menu.add(scheduleItem);
        scheduleItem.setVisible(false);
        menu.add(myAppointmentsItem);
        myAppointmentsItem.setVisible(false);

        // if user is admin
        menu.add(editItem);
        
        menu.addSeparator();
        menu.add(exitItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
        
        // Prints "Current Working Directory: /Users/hollyjordan/git/ReservationSystem"
        String cwd = System.getProperty("user.dir");
        System.out.println("Current Working Directory: " + cwd);
        // Prints "Current Working Directory: /Users/hollyjordan/git/ReservationSystem"
        

        JMenuItem scheduleMenuItem = new JMenuItem("Schedule Appointment");
        scheduleMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String aptId = Integer.toString(getCurrentAptID());
            	Communicator c = Communicator.getCommunicator();
                c.updateAppointmentsColumn(getSignedInEmail(), aptId, myAppointmentsPage);
               
            }
        });
        
        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
       }
    
    public static void initializeLoggedInComponents(String name, String email) {
    	System.out.println("MainFrame:[initializeLoggedInComponents]");
    	System.out.println("\n\n\n--------------- Name" + name);
    	System.out.println("\n\n\n--------------- email" + email);

    	setSignedInName(name);
    	setSignedInEmail(email);
    	setUserIsLoggedIn(true);

    	
    	
        // Add menu items for the logged-in user
        scheduleItem.setVisible(true);
        myAppointmentsItem.setVisible(true);
    }
    	
    // -------------------------------
	
    public static Integer getCurrentAptID() {
		return currentAptID;
	}


	public static void setCurrentAptID(Integer currentAptID) {
		MainFrame.currentAptID = currentAptID;
	}

	public static void setUserIsLoggedIn(Boolean isLoggedIn) {
    	if (isLoggedIn) {
            userIsLoggedIn = true;
    	} else {
    		 userIsLoggedIn = false;
    	}

    }
	
	public static Boolean getUserIsLoggedIn() {
        return userIsLoggedIn;
    }
    

    public String getSignedInName() {
		return signedInName;
	}


	public static void setSignedInName(String name) {
		 signedInName = name;
	}


	public static String getSignedInEmail() {
		return signedInEmail;
	}


	public static void setSignedInEmail(String email) {
		signedInEmail = email;
	}

    
    public static void sendAlert(String msg) {
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
//    	schedulePage = new ScheduleAppointment(signedInEmail);
    	setContentPane(new ScheduleAppointment(signedInEmail));
//        setContentPane(this.schedulePage.getSavedState());
        validate();
        repaint();
    }
        
    private void switchToEditAvailability() {
    	setContentPane(this.editAvailPage.getSavedState());
        validate();
        repaint();
    }
    
    public MyAppointments getMyAppointmentsPanel() {
        return myAppointmentsPage;
    }
    
    public void switchToMyAppointments() {
        // Get the content pane
        Container contentPane = getContentPane();

        // Check if the content pane is an instance of MyAppointments
        if (contentPane instanceof MyAppointments) {
            // If already on MyAppointments panel, do nothing
            return;
        }

        // Check if the saved state is an instance of MyAppointments
        if (this.myAppointmentsPage.getSavedState() instanceof MyAppointments) {
            // Set the content pane to the saved state (MyAppointments panel)
            setContentPane(this.myAppointmentsPage.getSavedState());
            validate();
            repaint();
        } else {
            // If saved state is not MyAppointments, create a new instance and switch
            MyAppointments myAppointmentsPanel = new MyAppointments();
            myAppointmentsPanel.setSavedState((JPanel) contentPane);
            setContentPane(myAppointmentsPanel);
            validate();
            repaint();
        }
    }
    
    public static void main(String[] args) {
        System.out.println(System.getProperties());

    	MainFrame mf = new MainFrame();
    }
}