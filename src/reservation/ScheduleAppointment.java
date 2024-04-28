package reservation;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class ScheduleAppointment extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel savedState;

    public ScheduleAppointment() {
		// Default constructor
        super(new BorderLayout()); // Set the layout for the panel
        initGUI(); // Initialize the GUI components
    }

    public JPanel initGUI() {
        // Add components to the panel
        JLabel scheduleLabel = new JLabel("Schedule Appointment GUI");
        add(scheduleLabel, BorderLayout.CENTER);
        // Add other components as needed
        setSavedState(this);
        return this;
        
    }
    
    
    public void setSavedState(JPanel toSave) {
    	this.savedState = toSave;
    }
    
    public JPanel getSavedState() {
    	return this.savedState;
    }
}
