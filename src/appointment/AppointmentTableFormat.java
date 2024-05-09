package appointment;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.issuezilla.Issue;

// SOURCE: https://glazedlists.github.io/glazedlists-tutorial/#hello-world
/**
 * Display issues in a tabular form.
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class AppointmentTableFormat implements TableFormat<Appointment> {

  @Override
  public int getColumnCount() {
    return 6;
  }

  @Override
  public String getColumnName(int column) {
    switch (column) {
	    case 0:
	    	return "ID";
	    case 1:
	    	return "Time";
	    case 2:
	    	return "Date";
	    case 3:
	    	return "Type";
	    case 4:
	    	return "Notes"; // person who the appt is with
	    case 5:
	    	return "With";
	    case 6: 
	    	return "Description";
    }
    throw new IllegalStateException("Unexpected column: " + column);
  }

  @Override
  public Object getColumnValue(Appointment apt, int column) {
    switch (column) {
	    case 0:
	    	return apt.getId();
	    case 1:
	    	return apt.getTime();
	    case 2:
	    	return apt.getDate();
	    case 3:
	    	return apt.getAppointmentType();
	    case 4:
	    	return apt.getNotes();
	    case 5: 
	    	return apt.getWho();
	    case 6:
	    	return apt.getShortDescription();
    }
    throw new IllegalStateException("Unexpected column: " + column);
  }
}