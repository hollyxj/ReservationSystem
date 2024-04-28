package reservation;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class Welcome extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel savedState;

    public Welcome() {
		// Default constructor
        super(new BorderLayout()); // Set the layout for the panel
        initGUI(); // Initialize the GUI components
    }

    public JPanel initGUI() {
        // Create and add components to the panel
        JLabel welcomeLabel = new JLabel("Welcome to My Application!");
        add(welcomeLabel, BorderLayout.CENTER);
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

    // Additional methods specific to the Welcome panel can be added here
}