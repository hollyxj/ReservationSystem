package reservation;
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Driver extends JFrame {
    private static final long serialVersionUID = 1L;
	private SignUp signUpPage;
	private SignIn signInPage;
    private Welcome welcomePage;
    private ScheduleAppointment schedulePage;
    
    public Driver() {
        super("Driver");
        this.signUpPage = new SignUp();
        this.signInPage = new SignIn();
        this.welcomePage = new Welcome();
        this.schedulePage = new ScheduleAppointment();

        // Initialize the Sign In page as the initial view
        setContentPane(this.welcomePage);

        // Create menu items
        JMenuItem welcomeItem = new JMenuItem("Welcome");
        JMenuItem signUpItem = new JMenuItem("Sign Up");
        JMenuItem signInItem = new JMenuItem("Sign In");
        JMenuItem scheduleItem = new JMenuItem("Schedule Appointment");
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
            this.signInPage.setSavedState((JPanel) getContentPane());
        });
        welcomeItem.addActionListener(e -> {
        	// Welcome page
        	switchToWelcome();
        	this.welcomePage.setSavedState((JPanel) getContentPane());
        });
        scheduleItem.addActionListener(e -> {
        	// Schedul appointment page
        	switchToScheduleAppointment();
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
        menu.addSeparator();
        menu.add(exitItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void switchToSignIn() {
        setContentPane(this.signInPage.getSavedState());
        validate();
        repaint();
    }
    
    private void switchToSignUp() {
        setContentPane(this.signUpPage.getSavedState());
        validate();
        repaint();
    }
    
    private void switchToWelcome() {
        setContentPane(this.welcomePage.getSavedState());
        validate();
        repaint();
    }

    private void switchToScheduleAppointment() {
        setContentPane(this.schedulePage.getSavedState());
        validate();
        repaint();
    }
    
    public static void main(String[] args) {
    	new Driver();
//        try { 
//        	new Driver();
//        }
//        catch (Exception e) {
//        	e.printStackTrace();
//        }
    }
}
