package gui;
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
	private JCheckBox isAdminCheckbox = new JCheckBox();

	private JPanel savedState;
	
    Font h1 = new Font("Arial", Font.BOLD, 24); 
    Font h2 = new Font("Arial", Font.BOLD, 18);
    Font h3 = new Font("Arial", Font.BOLD, 16);
    Font h4 = new Font("Arial", Font.ITALIC, 16);
    Font field = new Font("Arial", Font.PLAIN, 16);


	public SignUp() {
		// Default constructor
        super(new BorderLayout()); // Set the layout for the panel
        initGUI(); // Initialize the GUI components
	}
	
    public JPanel initGUI() {
    	JLabel title = new JLabel("Sign Up");
    	JLabel subtitle = new JLabel("Create your Rezerve™ account below");
    	
    	title.setFont(h1);
    	subtitle.setFont(h4);

    	
        JLabel nameLabel = new JLabel("Name:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel isAdminLabel = new JLabel("Admin?");

        this.nameField.setEditable(true);
        this.nameField.setPreferredSize(new Dimension(200,25));
        this.emailField.setEditable(true);
        this.emailField.setPreferredSize(new Dimension(200,25));
        this.passwordField.setEditable(true);
        this.passwordField.setPreferredSize(new Dimension(200,25));
        this.showPasswordCheckbox = new JCheckBox();
        
        nameLabel.setFont(h2);
        emailLabel.setFont(h2);
        passwordLabel.setFont(h2);
        
        nameField.setFont(field);
        emailField.setFont(field);
        passwordField.setFont(field);

        // Panel configurations
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(7, 1));
        
        JPanel nameFlow = new JPanel();
        nameFlow.setLayout(new FlowLayout());
        
        nameFlow.add(nameLabel);
        nameFlow.add(nameField);

        JPanel emailFlow = new JPanel();
        emailFlow.setLayout(new FlowLayout());
        
        emailFlow.add(emailLabel);
        emailFlow.add(emailField);
        
        
        gridPanel.add(title); // 1
        gridPanel.add(subtitle); // 2
        
        gridPanel.add(nameFlow); // 3
        gridPanel.add(emailFlow); // 4

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

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(this.passwordField);
        JLabel showLabel = new JLabel("Show:");
        showLabel.setFont(h4);
        passwordPanel.add(showLabel); // Eyeball here
        passwordPanel.add(this.showPasswordCheckbox);
        gridPanel.add(passwordPanel); // 5

        // Show/hide password checkbox functionality
        showPassword();

        
        // Admin flow
        isAdminLabel.setFont(h3);
        this.isAdminCheckbox = new JCheckBox();

        JPanel adminFlow = new JPanel();
        adminFlow.setLayout(new FlowLayout());
        adminFlow.add(isAdminLabel);
        adminFlow.add(isAdminCheckbox);
        gridPanel.add(adminFlow); // 6
        
        
        JButton submitButton = new JButton("Sign Up");
        submitButton.setFont(h3);

        submitButton.addActionListener(e -> {
        	 try {
				handleSubmitButton();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });

        JPanel buttonFlow = new JPanel();
        buttonFlow.setLayout(new FlowLayout());
        buttonFlow.add(submitButton);
        gridPanel.add(buttonFlow); // 7
        
        JPanel megaPanel = new JPanel();
        megaPanel.setLayout(new FlowLayout());
        megaPanel.add(gridPanel);

        setLayout(new BorderLayout());
        add(megaPanel, BorderLayout.CENTER);

        setSavedState(this);
        return this;
    }
    
    private void handleSubmitButton() {
    	String name = this.nameField.getText();
    	String email = this.emailField.getText();
    	char[] passwordChars = this.passwordField.getPassword();
        String pwd = new String(passwordChars);
        Boolean isSelected = this.isAdminCheckbox.isSelected();
    	String appointments = null; // new accounts have no appointments

        System.out.println("Submit button pressed.\n");
        // Send info the server
        try {
        	Communicator c = Communicator.getCommunicator();
        	System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%Pwd="+pwd);
            c.addUser(name,email,pwd,isSelected,appointments);
            clearFields();
        } catch (Exception e) {
        	e.printStackTrace();
//        	SwingUtilities.invokeLater(() -> {
//    			JOptionPane.showMessageDialog(null, "Error: Issue creating account. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
//    		});
        }
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