package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class EditAvailability extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel savedState;

	// required fields
	private JTextField timeField = new JTextField(25);
	private JTextField dateField = new JTextField(25);
	private JTextField appointmentType = new JTextField(25);
	private JTextField who = new JTextField(25);
	
	// optional fields
	private JTextField notes = new JTextField(25);
	private JTextField shortDescription = new JTextField(25);
	
    Font h1 = new Font("Arial", Font.BOLD, 24); 
    Font h2 = new Font("Arial", Font.BOLD, 18);
    Font h3 = new Font("Arial", Font.BOLD, 16);
    Font h4 = new Font("Arial", Font.ITALIC, 16);
    Font field = new Font("Arial", Font.PLAIN, 16);


	public EditAvailability() {
		// Default constructor
        super(new BorderLayout()); // Set the layout for the panel
        initGUI(); // Initialize the GUI components
	}
	
    public JPanel initGUI() {
    	JLabel title = new JLabel("Edit Availability");
    	JLabel subtitle = new JLabel("Hi Rezerve™ Admin!\nEdit your visible appointments below");
    	
    	title.setFont(h1);
    	subtitle.setFont(h4);

        JLabel timeLabel = new JLabel("Time:");
        JLabel dateLabel = new JLabel("Date:");
        JLabel aptTypeLabel = new JLabel("Appointment Type:");
        JLabel whoLabel = new JLabel("Who:");
        JLabel optionalLabel = new JLabel("Optional:");
        JLabel notesLabel = new JLabel("Notes (optional):");
        JLabel descLabel = new JLabel("Short Description (optional):");

        timeLabel.setFont(h2);
        dateLabel.setFont(h2);
        aptTypeLabel.setFont(h2);
        whoLabel.setFont(h2);
        optionalLabel.setFont(h4);
        notesLabel.setFont(h4);
        descLabel.setFont(h4);
        
        this.timeField.setEditable(true);
        this.timeField.setPreferredSize(new Dimension(200,25));
        this.dateField.setEditable(true);
        this.dateField.setPreferredSize(new Dimension(200,25));
        this.appointmentType.setEditable(true);
        this.appointmentType.setPreferredSize(new Dimension(200,25));
        this.who.setEditable(true);
        this.who.setPreferredSize(new Dimension(200,25));
        this.notes.setEditable(true);
        this.notes.setPreferredSize(new Dimension(200,25));
        this.shortDescription.setEditable(true);
        this.shortDescription.setPreferredSize(new Dimension(200,25));

        timeField.setFont(field);
        dateField.setFont(field);
        appointmentType.setFont(field);
        who.setFont(field);
        notes.setFont(field);
        shortDescription.setFont(field);
        
        // Panel configurations
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.Y_AXIS));
        
        JPanel timeFlow = new JPanel();
        timeFlow.setLayout(new FlowLayout());
        timeFlow.add(timeLabel);
        timeFlow.add(timeField);

        JPanel dateFlow = new JPanel();
        dateFlow.setLayout(new FlowLayout());
        dateFlow.add(dateLabel);
        dateFlow.add(dateField);
        
        JPanel aptFlow = new JPanel();
        aptFlow.setLayout(new FlowLayout());
        aptFlow.add(aptTypeLabel);
        aptFlow.add(appointmentType);
        
        JPanel whoFlow = new JPanel();
        whoFlow.setLayout(new FlowLayout());
        whoFlow.add(whoLabel);
        whoFlow.add(who);
        
        JPanel notesFlow = new JPanel();
        notesFlow.setLayout(new FlowLayout());
        notesFlow.add(notesLabel);
        notesFlow.add(notes);
        
        JPanel descFlow = new JPanel();
        descFlow.setLayout(new FlowLayout());
        descFlow.add(descLabel);
        descFlow.add(shortDescription);
        
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(h3);
        submitButton.addActionListener(e -> {
        	 try {
				handleSubmitButton();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
        
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(h3);
        clearButton.addActionListener(e -> {
        	 try {
				clearFields();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });

        
        JPanel buttonFlow = new JPanel();
        buttonFlow.setLayout(new FlowLayout());
        buttonFlow.add(submitButton);
        buttonFlow.add(clearButton);
        
        
        gridPanel.add(title); // 
        gridPanel.add(subtitle); // 
        addSeparator(gridPanel);
        gridPanel.add(timeFlow); // 
        gridPanel.add(dateFlow); // 
        gridPanel.add(aptFlow); // 
        gridPanel.add(whoFlow); // 
        addSeparator(gridPanel);
//        gridPanel.add(optionalLabel); // 
        gridPanel.add(notesFlow); // 
        gridPanel.add(descFlow); // 
        addSeparator(gridPanel);
        gridPanel.add(buttonFlow);
 

        JPanel megaPanel = new JPanel();
        megaPanel.setLayout(new FlowLayout());
        megaPanel.add(gridPanel);

        setLayout(new BorderLayout());
        add(megaPanel);

        setSavedState(this);
        return this;
    }
    
    private JPanel addSeparator(JPanel sep) {
        sep.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing before the separator
        sep.add(new JSeparator(SwingConstants.HORIZONTAL)); // Horizontal separator
        sep.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing after the separator
        return sep;
    }
    
    public JTextField getTimeField() {
		return timeField;
	}

	public void setTimeField(JTextField timeField) {
		this.timeField = timeField;
	}

	public JTextField getDateField() {
		return dateField;
	}

	public void setDateField(JTextField dateField) {
		this.dateField = dateField;
	}

	public JTextField getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(JTextField appointmentType) {
		this.appointmentType = appointmentType;
	}

	public JTextField getWho() {
		return who;
	}

	public void setWho(JTextField who) {
		this.who = who;
	}

	public JTextField getNotes() {
		return notes;
	}

	public void setNotes(JTextField notes) {
		this.notes = notes;
	}

	public JTextField getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(JTextField shortDescription) {
		this.shortDescription = shortDescription;
	}

	private void handleSubmitButton() {
    	System.out.println("EditAvailability: Submit Button Pressed");
    	
    	String time = this.timeField.getText();
    	String date = this.dateField.getText();
    	String aptType = this.appointmentType.getText();
    	String who = this.who.getText();
    	String notes = this.notes.getText();
    	String shortDesc = this.shortDescription.getText();
    	    
        // Send info the server

        Communicator c = Communicator.getCommunicator();
        c.addAvailability(time, date, aptType, who, notes, shortDesc);
//        clearFields(); make into button
    }
    
    public void clearFields() {
    	this.timeField.setText("");
    	this.dateField.setText("");
    	this.appointmentType.setText("");
    	this.who.setText("");
    	this.notes.setText("");
    	this.shortDescription.setText("");
    }
    
    public void setSavedState(JPanel toSave) {
    	this.savedState = toSave;
    }
    
    public JPanel getSavedState() {
    	return this.savedState;
    }
}
