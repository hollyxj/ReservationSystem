package run;
import javax.swing.SwingUtilities;

import gui.*;
import server.*;

public class run {
	
    public static void main(String[] args) {
    	// Start server
    	SwingUtilities.invokeLater(() -> {
			RezServer server = new RezServer(); // Create an instance of Server on the Event Dispatch Thread
			// Initialize the user GUI
        	MainFrame mf = new MainFrame();
    	});
    	
    }
}
