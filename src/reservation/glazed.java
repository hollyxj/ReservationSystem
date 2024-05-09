package reservation;
import ca.odell.glazedlists.*;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventListModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import static ca.odell.glazedlists.swing.GlazedListsSwing.eventTableModelWithThreadProxyList;
import static ca.odell.glazedlists.swing.GlazedListsSwing.eventListModelWithThreadProxyList;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.issuezilla.Issue;

// SOURCE: https://glazedlists.github.io/glazedlists-tutorial/#hello-world

public class glazed implements TableFormat<Appointment>  {
	/**
	 * Display a frame for browsing issues.
	 */
	
	/** event list that hosts the issues */

	private EventList<Appointment> appointmentEventList = new BasicEventList<>(); 

	public void display() {
		SortedList<Appointment> sortedAppointments = new SortedList<>(appointmentEventList, new AppointmentComparator());
		JTextField filterEdit = new JTextField(10);
		AppointmentTextFilterator filterator = new AppointmentTextFilterator();
		MatcherEditor<Appointment> textMatcherEditor = new TextComponentMatcherEditor<>(filterEdit, filterator);
		FilterList<Appointment> textFilteredIssues = new FilterList<>(sortedAppointments, textMatcherEditor);
		
		// derive the users list from the issues list
		EventList<String> usersNonUnique = new AppointmentToUserList(appointmentEventList);
		UniqueList<String> usersEventList = new UniqueList<>(usersNonUnique);

		// create the issues table
		AdvancedTableModel<Appointment> tableModel = eventTableModelWithThreadProxyList(
												textFilteredIssues, new AppointmentTableFormat());
		JTable issuesJTable = new JTable(tableModel);
		TableComparatorChooser.install(issuesJTable, sortedAppointments, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);
		JScrollPane issuesTableScrollPane = new JScrollPane(issuesJTable);
	
		// create the users list
		DefaultEventListModel<String> usersListModel = eventListModelWithThreadProxyList(usersEventList);
		JList<String> usersJList = new JList<>(usersListModel);
		JScrollPane usersListScrollPane = new JScrollPane(usersJList);
	
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
		panel.add(issuesTableScrollPane,       new GridBagConstraints(1, 0, 1, 4, 0.85, 1.0,
		      GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	  	// create a frame with that panel
		JFrame frame = new JFrame("Schedule Appointment");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(540, 380);
		frame.getContentPane().add(panel);
		frame.setVisible(true);
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
	
	public static void main(String[] args) {
		glazed G = new glazed();
		G.display();
	}
} // end class
