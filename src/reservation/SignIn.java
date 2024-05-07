package reservation;
import database.*;
import server.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class SignIn extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTextField emailField = new JTextField(25);
	private JPasswordField passwordField = new JPasswordField(15);
	private JCheckBox showPasswordCheckbox = new JCheckBox();
	private JPanel savedState;
	
	public SignIn() {
		// Default constructor
        super(new BorderLayout()); // Set the layout for the panel
        initGUI(); // Initialize the GUI components
	}
	
    public JPanel initGUI() {
        JLabel nameOrEmailLabel = new JLabel("Enter user email:");
        JLabel passwordLabel = new JLabel("Password:");

        this.emailField.setEditable(true);
        this.emailField.setPreferredSize(new Dimension(200,20));
        this.passwordField.setEditable(true);
        this.showPasswordCheckbox = new JCheckBox();

        // Panel configurations
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));

        panel.add(nameOrEmailLabel); // 1
        panel.add(this.emailField); // 2

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
        panel.add(passwordPanel);

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
        megaPanel.add(panel);

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

}