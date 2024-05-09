package gui;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Welcome extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel savedState;

	Font h1 = new Font("Arial", Font.BOLD, 24); 
    Font h2 = new Font("Arial", Font.BOLD, 18);
    Font h4 = new Font("Arial", Font.ITALIC, 16);
    Font field = new Font("Arial", Font.PLAIN, 16);
	
    public Welcome() {
		// Default constructor
        super(new BorderLayout()); // Set the layout for the panel
        initGUI(); // Initialize the GUI components
    }

    public JPanel initGUI() {
        JLabel welcomeLabel = new JLabel("Welcome to Rezerve™!");
        welcomeLabel.setFont(h1);
        JPanel flowPanel = new JPanel();
    	flowPanel.setLayout(new FlowLayout());
    	flowPanel.add(welcomeLabel);
    	

//    	try {
//            BufferedImage img = ImageIO.read(new File("images/Server.jpg")); // Replace "images/Server.jpg" with the actual path to your image file
//            ImageIcon icon = new ImageIcon(img);
//
//            // Create a JLabel with the image icon
//            JLabel imageLabel = new JLabel(icon);
//
//            // Add the label with the image to the panel
//            add(imageLabel, BorderLayout.CENTER);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    	String currentDirectory = System.getProperty("user.dir");
        System.out.println("[Welcome] Current Working Directory: " + currentDirectory);
    	
    	
        // Print debug information about the resource loading
        URL imageUrl = getClass().getResource("/images/Server.jpg");
        System.out.println("Image URL: " + imageUrl); // Debug line

        if (imageUrl != null) {
            try {
                BufferedImage img = ImageIO.read(imageUrl);
                ImageIcon icon = new ImageIcon(img);
                JLabel imageLabel = new JLabel(icon);
                flowPanel.add(imageLabel, BorderLayout.CENTER);
            } catch (IOException e) {
                System.err.println("Error loading image icon: " + e.getMessage());
            }
        } else {
            System.err.println("Error loading image icon. Image not found.");
        }
        
    	
        add(flowPanel, BorderLayout.NORTH);
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