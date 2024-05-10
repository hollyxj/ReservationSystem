package gui;

import javax.swing.*;
import database.*;
import gui.Communicator.UpdateCallback;

import javax.swing.table.DefaultTableModel;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import appointment.*;

import java.awt.*;
import server.RezServer;


public class MyAppointments extends JPanel implements UpdateCallback {

    private static final long serialVersionUID = 1L;
    private JPanel savedState;
    private JTable myAppointments;
    private DefaultTableModel tableModel;
    
    private EventList<Appointment> appointmentEventList = new BasicEventList<>();
    private RezServer server;


    Font h1 = new Font("Arial", Font.BOLD, 24);
    Font h2 = new Font("Arial", Font.BOLD, 18);
    Font h4 = new Font("Arial", Font.ITALIC, 16);
    Font field = new Font("Arial", Font.PLAIN, 16);

    public MyAppointments() {
        // Default constructor
        super(new BorderLayout()); // Set the layout for the panel
        initGUI(); // Initialize the GUI components
    }
    
    public MyAppointments(RezServer server) {
        this.server = server;
    }

    public JPanel initGUI() {
        JLabel myAptsLabel = new JLabel("My Appointments:");
        myAptsLabel.setFont(h1);

        // Initialize the table model
        String[] columnNames = {"Time", "Date", "Appointment Type", "Who", "Notes", "Short Description"};
        tableModel = new DefaultTableModel(columnNames, 0);
        myAppointments = new JTable(tableModel);

        // Create a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(myAppointments);

        JPanel flowPanel = new JPanel();
        flowPanel.setLayout(new FlowLayout());
        flowPanel.add(myAptsLabel);

        add(flowPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        setSavedState(this);
        return this;
    }
    
    
        // Retrieve appointments for the current user
//        String userEmail = getCurrentUserEmail(); // Method to get the current logged-in user's email
//        DatabaseManager dbManager = new DatabaseManager();
//        String appointmentIDs = dbManager.getAppointmentsForUser(userEmail);
//        if (appointmentIDs != null) {
//            String[] appointmentIDArray = appointmentIDs.split(",");
//            for (String appointmentID : appointmentIDArray) {
//                // Query the "appointments" table for each appointment ID and add them to the table
//                Appointment appointment = dbManager.getAppointmentByID(appointmentID);
//                if (appointment != null) {
//                    String[] rowData = {appointment.getTime(), appointment.getDate(), appointment.getAppointmentType(),
//                            appointment.getWho(), appointment.getNotes(), appointment.getShortDescription()};
//                    addAppointment(rowData);
//                }
//            }
//        }
//
//        dbManager.closeConnection();
//        return this; // Return the panel
    
        
        @Override
    public void onUpdateSuccess() {
        // Update UI after successful database update
        initGUI();
    }
    
    public EventList<Appointment> getAppointmentEventList() {
        return appointmentEventList;
    }

    public void setSavedState(JPanel toSave) {
        this.savedState = toSave;
    }

    public JPanel getSavedState() {
        return this.savedState;
    }

    // Method to add appointment data to the table
    public void addAppointment(String[] rowData) {
        tableModel.addRow(rowData);
    }

    // Method to clear all appointments from the table
    public void clearAppointments() {
        tableModel.setRowCount(0);
    }
    
    public String getAppointmentsForUser(String userEmail) {
        return server.getDB().getAppointmentsForUser(userEmail);
    }

    public void updateAppointmentsColumn(String userEmail, String appointmentID) {
        server.getDB().updateAppointmentsColumn(userEmail, appointmentID);
    }
}
