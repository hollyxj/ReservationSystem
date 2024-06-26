package gui;
import ca.odell.glazedlists.*;
import ca.odell.glazedlists.CollectionList.Model;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventListModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import static ca.odell.glazedlists.swing.GlazedListsSwing.eventTableModelWithThreadProxyList;
import static ca.odell.glazedlists.swing.GlazedListsSwing.eventListModelWithThreadProxyList;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import appointment.Appointment;
import appointment.AppointmentComparator;
import appointment.AppointmentTableFormat;
import appointment.AppointmentTextFilterator;
import appointment.AppointmentToUserList;
import ca.odell.glazedlists.gui.TableFormat;

// Modified by Holly Jordan 2024 from
// SOURCE: https://glazedlists.github.io/glazedlists-tutorial/#hello-world

public class ScheduleAppointment extends JPanel implements TableFormat<Appointment>  {
	private static final long serialVersionUID = 1L;
	private JPanel savedState;
	private EventList<Appointment> appointmentEventList = new BasicEventList<>(); 
	private JTable appointmentsJTable;
    private String userEmail; // assuming userEmail is a field in the class


	// Constructor
    public ScheduleAppointment(String userEmail) {
        this.userEmail = userEmail;
        initGUI();
    }
    
    // Getter method for userEmail
    public String getUserEmail() {
        return userEmail;
    }
	public JPanel initGUI()  {
		SortedList<Appointment> sortedAppointments = new SortedList<>(appointmentEventList, new AppointmentComparator());
		
		JTextField filterEdit = new JTextField(10);
		AppointmentTextFilterator filterator = new AppointmentTextFilterator();
		MatcherEditor<Appointment> textMatcherEditor = new TextComponentMatcherEditor<>(filterEdit, filterator);
		FilterList<Appointment> textFilteredWho = new FilterList<>(sortedAppointments, textMatcherEditor);
		
		// derive the users list from the appointments list
		EventList<String> usersNonUnique = new AppointmentToUserList(appointmentEventList);
		UniqueList<String> usersEventList = new UniqueList<>(usersNonUnique);

		// create the appointment table
		AdvancedTableModel<Appointment> tableModel = eventTableModelWithThreadProxyList(
												textFilteredWho, new AppointmentTableFormat());
		this.appointmentsJTable = new JTable(tableModel);
		TableComparatorChooser.install(appointmentsJTable, sortedAppointments, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);
		JScrollPane appointmentsTableScrollPane = new JScrollPane(appointmentsJTable);
	
		// create the users list
		DefaultEventListModel<String> usersListModel = eventListModelWithThreadProxyList(usersEventList);
		JList<String> usersJList = new JList<>(usersListModel);
		JScrollPane usersListScrollPane = new JScrollPane(usersJList);
		
		 // Add a ListSelectionListener to the JList
        usersJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensure the selection has stabilized
                    JList<String> list = (JList<String>) e.getSource();
                    String selectedValue = list.getSelectedValue();
                    System.out.println("Selected user: " + selectedValue);
                    filterEdit.setText(selectedValue);
                    // Perform actions based on the selected user, e.g., filter appointments
                    // handleUserSelection(selectedValue);
                }
            }
        });
	
	    // create the panel
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(new JLabel("Filter: "),      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
		      GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(filterEdit,                  new GridBagConstraints(0, 1, 1, 1, 0.15, 0.0,
		      GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(new JLabel("Schedule Appointment With: "), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
		     GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(usersListScrollPane,         new GridBagConstraints(0, 3, 1, 1, 0.15, 1.0,
		      GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		panel.add(appointmentsTableScrollPane,       new GridBagConstraints(1, 0, 1, 4, 0.85, 1.0,
		      GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
		JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	handleRefreshButton();
            }
        });
        
		JButton scheduleButton = new JButton("Schedule Appointment");
		scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	handleScheduleButton();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshButton);
        buttonPanel.add(scheduleButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setSavedState(this);
        return this;
	}
	
	public void handleRefreshButton() {
	    System.out.println("ScheduleAppointment: Load button pressed.");

        Communicator c = Communicator.getCommunicator();
        c.generateJSON();
	    
	    // Read JSON file and populate appointments
	    Gson gson = new Gson();
	    // Get the current working directory
	    String currentDirectory = System.getProperty("user.dir");
	    System.out.println("ScheduleAppointment:[userdir]=\'"+currentDirectory+"\'");

	    // Concatenate the file name to the directory path
	    String filePath = currentDirectory + File.separator + "availability.json";

	    System.out.println("File Path: " + filePath);
	    
	    
	    try (FileReader reader = new FileReader(filePath)) {
	        // Define the type of the appointment list using TypeToken
	        Type appointmentListType = new TypeToken<List<Appointment>>(){}.getType();

	        // Deserialize the JSON file into a list of appointments
	        List<Appointment> appointments = gson.fromJson(reader, appointmentListType);

	        // Clear existing appointments
	        appointmentEventList.clear();

	        // Add loaded appointments to the event list
	        appointmentEventList.addAll(appointments);

	        // Refresh the view to display the loaded appointments
	        repaint();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	
	public void handleScheduleButton() {
	    System.out.println("ScheduleAppointment: Select button pressed.");

	    // Get the selected row index
	    int selectedRowNumber = this.appointmentsJTable.getSelectedRow();
        System.out.println("selectedRowNumber "+ selectedRowNumber);


	    if (selectedRowNumber != -1) { // Check if a row is selected
	    	// Use the selected row number to get the ID number of the appointment in the selected row
	    	
	        int modelRow = this.appointmentsJTable.convertRowIndexToModel(selectedRowNumber);

	        // Get the date value from the model
	        Object idObject = this.appointmentsJTable.getModel().getValueAt(modelRow, 0);
	        System.out.println("!!!!!!!!!!!!!!!!!!!idObject="+idObject);
	        Integer idNum =  (Integer) idObject;
	        idNum--;
	        System.out.println("ID Number1 [idNum]= "+ idNum);

//	        Object dateObject = this.appointmentsJTable.getModel().getValueAt(modelRow, 2);

	    	
	        // Get the appointment object from the selected row
	        Appointment selectedAppointment = appointmentEventList.get(idNum);

	        // Extract the desired fields from the selected appointment
	        String time = selectedAppointment.getTime();
	        String date = selectedAppointment.getDate();
	        String appointmentType = selectedAppointment.getAppointmentType();
	        String who = selectedAppointment.getWho();
	        String notes = selectedAppointment.getNotes();
	        String shortDescription = selectedAppointment.getShortDescription();

	        // Print the extracted appointment fields
	        System.out.println("Selected Appointment:");
	        System.out.println("Time: " + time);
	        System.out.println("Date: " + date);
	        System.out.println("Appointment Type: " + appointmentType);
	        System.out.println("Who: " + who);
	        System.out.println("Notes: " + notes);
	        System.out.println("Short Description: " + shortDescription);
	        
	        String message = "Please confirm appointment details: \n" + 
	        		"You are scheduling a " + appointmentType + " appointment with " + who +
	        		" on  " + date + " at " + time + ".\n\n" +
	        		"Notes: " + notes + "\n" +
	                "Short Description: " + shortDescription + "\n\n" +
	        		"Schedule appointment?" +"\n";
	        
	        int option = JOptionPane.showOptionDialog(null, message, "Schedule Appointment",
	                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
	                new String[]{"Schedule", "Go Back"}, "Schedule");

	        // Check user's choice
	        if (option == JOptionPane.YES_OPTION) {
	            // Get the selected appointment ID
	            // Send to Main Frame
		        System.out.println("ID Number2 [idNum]= "+ idNum);

	            MainFrame.setCurrentAptID(idNum);

	            String appointmentID = Integer.toString(idNum);
	            
	            // Update the "users" table with the appointment ID
	            String userEmail = getUserEmail(); // Method to get the current logged-in user's email

	            Communicator c = Communicator.getCommunicator();
	            c.updateAppointmentsColumn(userEmail, appointmentID, () -> {
	                // Callback for UI update
	                SwingUtilities.invokeLater(() -> {
	                    String[] rowData = {time, date, appointmentType, who, notes, shortDescription};
	                    ((MainFrame) SwingUtilities.getWindowAncestor(this)).getMyAppointmentsPanel().addAppointment(rowData);
	                });
	            });
	        
	        } else {
	            System.out.println("User clicked Go Back.");
	        }
	    } else {
	        System.out.println("No appointment selected.");
	    }
	}
	
	public JTable getAppointmentsJTable() {
		return appointmentsJTable;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getColumnName(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object getColumnValue(Appointment arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public void setSavedState(JPanel toSave) {
    	this.savedState = toSave;
    }
    
    public JPanel getSavedState() {
    	return this.savedState;
    }

} // end class ScheduleAppointment