package appointment;

import java.time.LocalTime;
import java.util.Comparator;
import ca.odell.issuezilla.Issue;

// SOURCE: https://glazedlists.github.io/glazedlists-tutorial/#hello-world
/**
 * Compare issues by priority.
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class AppointmentComparator implements Comparator<Appointment> {
  @Override
  public int compare(Appointment aptA, Appointment aptB) {

    // rating is between 1 and 5, lower is more important
	LocalTime aptATime = aptA.getTime();
    LocalTime aptBTime = aptB.getTime();

    return aptATime.compareTo(aptBTime);
  }
}
