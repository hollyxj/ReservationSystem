package reservation;
import database.*;
import server.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
//import org.jdatepicker.impl.*;
//import org.jdatepicker.util.*;
//import org.jdatepicker.*;
//import org.jdatepicker.JDatePicker;
//import org.jdatepicker.UtilDateModel;
//import javafx.scene.control.DatePicker;

public class SignUp extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTextField nameField = new JTextField(25);
	private JTextField emailField = new JTextField(25);
	private JPasswordField passwordField = new JPasswordField(15);
	private JCheckBox showPasswordCheckbox = new JCheckBox();
	private JPanel savedState;
	
	public SignUp() {
		// Default constructor
        super(new BorderLayout()); // Set the layout for the panel
        initGUI(); // Initialize the GUI components
	}
	
    public JPanel initGUI() {
        JLabel nameLabel = new JLabel("Name:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        this.nameField.setEditable(true);
        this.nameField.setPreferredSize(new Dimension(200,20));
        this.emailField.setEditable(true);
        this.passwordField.setEditable(true);
        this.showPasswordCheckbox = new JCheckBox();

        // Panel configurations
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));

        panel.add(nameLabel); // 1
        panel.add(this.nameField); // 2

        // Press the "Tab" key to go to the next field functionality
        this.nameField.addKeyListener(new KeyListener() {
            JTextField emlField = getEmailField();

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    emlField.requestFocusInWindow(); // Move focus to textField2
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        panel.add(emailLabel);
        panel.add(this.emailField);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(this.passwordField);
        passwordPanel.add(new JLabel("Show:")); // Eyeball here
        passwordPanel.add(this.showPasswordCheckbox);
        panel.add(passwordPanel);

        // Show/hide password checkbox functionality
        showPassword();

        JButton submitButton = new JButton("Sign Up");

        submitButton.addActionListener(e -> {
        	 try {
				handleSubmitButton();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
    	String name = this.nameField.getText();
    	String email = this.emailField.getText();
    	char[] passwordChars = this.passwordField.getPassword();
        String pwd = new String(passwordChars);
        
        System.out.println("Submit button pressed.\n");
        // Send info the server

        Communicator c = Communicator.getCommunicator();
        c.addUser(name,email,pwd);
        clearFields();
    }
    
    
    public void clearFields() {
    	this.nameField.setText("");
    	this.emailField.setText("");
    	this.passwordField.setText("");
    }
    
    public void setSavedState(JPanel toSave) {
    	this.savedState = toSave;
    }
    
    public JPanel getSavedState() {
    	return this.savedState;
    }

	public JTextField getNameField() {
		return this.nameField;
	}
	
	public JTextField getEmailField() {
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