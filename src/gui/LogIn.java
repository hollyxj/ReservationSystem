package gui;
import database.*;
import server.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class LogIn extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTextField emailField = new JTextField(25);
	private JPasswordField passwordField = new JPasswordField();
	private JCheckBox showPasswordCheckbox = new JCheckBox();
	private JPanel savedState;
	
    Font h1 = new Font("Arial", Font.BOLD, 24); 
    Font h2 = new Font("Arial", Font.BOLD, 18);
    Font h4 = new Font("Arial", Font.ITALIC, 16);
    Font field = new Font("Arial", Font.PLAIN, 16);

	
	public LogIn() {
		// Default constructor
        super(new BorderLayout()); // Set the layout for the panel
        initGUI(); // Initialize the GUI components
	}
	
    public JPanel initGUI() {
    	JLabel title = new JLabel("Log In");
    	JLabel subtitle = new JLabel("Already have a Rezerve™ account? Sign in below!");
    	
    	title.setFont(h1);
    	subtitle.setFont(h4);
    	
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        this.emailField.setEditable(true);
        this.emailField.setPreferredSize(new Dimension(200,25));
        this.passwordField.setEditable(true);
        this.passwordField.setPreferredSize(new Dimension(200,25));

        this.showPasswordCheckbox = new JCheckBox();

        emailLabel.setFont(h2);
        passwordLabel.setFont(h2);
        
        emailField.setFont(field);
        passwordField.setFont(field);
        
        JPanel flow = new JPanel();
        flow.setLayout(new FlowLayout());
        
        flow.add(emailLabel);
        flow.add(emailField);
        // Panel configurations
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(4, 1));

        gridPanel.add(title); // 1
        gridPanel.add(subtitle); // 2
        gridPanel.add(flow); // 3


        // Press the "Tab" key to go to the next field functionality
        this.emailField.addKeyListener(new KeyListener() {
            JTextField nmOrEmlField = getNameOrEmailField();

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                	nmOrEmlField.requestFocusInWindow(); // Move focus to textField2
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(this.passwordField);
        passwordPanel.add(new JLabel("Show:")); // Eyeball here
        passwordPanel.add(this.showPasswordCheckbox);
        gridPanel.add(passwordPanel); // 4

        // Show/hide password checkbox functionality
        showPassword();

        JButton submitButton = new JButton("Sign In");

        submitButton.addActionListener(e -> {
        	 handleSubmitButton();
        });

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        southPanel.add(submitButton);

        JPanel megaPanel = new JPanel();
        megaPanel.setLayout(new FlowLayout());
        megaPanel.add(gridPanel);

        setLayout(new BorderLayout());
        add(megaPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        setSavedState(this);
        return this;
    }
    
    private void handleSubmitButton() {
        System.out.println("Sign in Submit button pressed.");
        String email = this.emailField.getText();
        char[] passwordChars = this.passwordField.getPassword();
        String pwd = new String(passwordChars);
        
        System.out.println("[Email]=" + email);
        System.out.println("[Password]=" + pwd);
        System.out.println();

        Communicator c = Communicator.getCommunicator();
        c.authenticate(email,pwd);
        clearFields();
//        MainFrame.switchTo("welcome");
        
//        MainFrame.switchToWelcome();
    }
    
    public void clearFields() {
    	this.emailField.setText("");
    	this.passwordField.setText("");
    }
    
    public void setSavedState(JPanel toSave) {
    	this.savedState = toSave;
    }
    
    public JPanel getSavedState() {
    	return this.savedState;
    }

	public JTextField getNameOrEmailField() {
		return this.emailField;
	}
	
	public JPasswordField getPasswordField() {
		return this.passwordField;
	}
	
	public void showPassword() {
		// Functionality for Show password checkbox
		JPasswordField pwdField = getPasswordField();
        int defaultt = this.passwordField.getEchoChar(); 
		showPasswordCheckbox.addItemListener(new ItemListener() { 
			public void itemStateChanged(ItemEvent e) { 
		        if (e.getStateChange() == ItemEvent.SELECTED) {
		        	pwdField.setEchoChar((char) 0);  
		        } else {  
		        	pwdField.setEchoChar((char) defaultt);
		        } 
		    }
		});
	}
	
    private JPanel addSeparator(JPanel sep) {
        sep.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing before the separator
        sep.add(new JSeparator(SwingConstants.HORIZONTAL)); // Horizontal separator
        sep.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing after the separator
        return sep;
    }

}