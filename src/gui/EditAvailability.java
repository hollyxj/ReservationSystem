package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class EditAvailability extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel savedState;

	private JTextField timeField = new JTextField(25);
	private JTextField dateField = new JTextField(25);
	private JTextField appointmentType = new JTextField(25);
	private JTextField who = new JTextField(25);
	
	// optional
	private JTextField notes = new JTextField(25);
	private JTextField shortDescription = new JTextField(25);

	
    Font h1 = new Font("Arial", Font.BOLD, 24); 
    Font h2 = new Font("Arial", Font.BOLD, 18);
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
        JLabel notesLabel = new JLabel("Notes:");
        JLabel descLabel = new JLabel("Short Description:");

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
                
        timeLabel.setFont(h2);
        dateLabel.setFont(h2);
        aptTypeLabel.setFont(h2);
        whoLabel.setFont(h2);
        notesLabel.setFont(h2);
        descLabel.setFont(h2);

        timeField.setFont(field);
        dateField.setFont(field);
        appointmentType.setFont(field);
        who.setFont(field);
        notes.setFont(field);
        shortDescription.setFont(field);

        // Panel configurations
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.Y_AXIS));
//        
//        
//        JPanel gridPanel = new JPanel(new BoxLayout(throwaway, BoxLayout.Y_AXIS));
        
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
        
        
        gridPanel.add(title); // 1
        gridPanel.add(subtitle); // 2
        
        gridPanel.add(timeFlow); // 3
        gridPanel.add(dateFlow); // 4
        gridPanel.add(aptFlow); // 4
        gridPanel.add(whoFlow); // 5
        gridPanel.add(notesFlow); // 6
        gridPanel.add(descFlow); // 7

//        // Press the "Tab" key to go to the next field functionality
//        this.timeField.addKeyListener(new KeyListener() {
//            JTextField emlField = getEmailField();
//
//            @Override
//            public void keyTyped(KeyEvent e) {
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_TAB) {
//                    emlField.requestFocusInWindow(); // Move focus to textField2
//                }
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//            }
//        });

  
        JButton submitButton = new JButton("Submit");

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
        megaPanel.add(gridPanel);

        setLayout(new BorderLayout());
        add(megaPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        setSavedState(this);
        return this;
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
