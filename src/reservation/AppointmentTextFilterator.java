package reservation;

import java.util.List;

import ca.odell.glazedlists.TextFilterator;
import ca.odell.issuezilla.Issue;

// SOURCE: https://glazedlists.github.io/glazedlists-tutorial/#hello-world
/**
 * Get the Strings to filter against for a given Issue.
 * 
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class AppointmentTextFilterator implements TextFilterator<Appointment> {
	public void getFilterStrings(List<String> baseList, Appointment apt) {
		baseList.add(apt.getAppointmentType());
		baseList.add(apt.getWho());
		baseList.add(apt.getNotes());
		baseList.add(apt.getShortDescription());	  
	}
}