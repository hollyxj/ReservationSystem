package appointment;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import ca.odell.issuezilla.Issue;

// SOURCE: https://glazedlists.github.io/glazedlists-tutorial/#hello-world
/**
 * Compare issues by priority.
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class AppointmentComparator implements Comparator<Appointment> {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    @Override
    public int compare(Appointment aptA, Appointment aptB) {
        String timeAString = aptA.getTime();
        String timeBString = aptB.getTime();

        // Parse time strings into LocalTime objects using the specified format
        LocalTime aptATime = LocalTime.parse(timeAString, TIME_FORMATTER);
        LocalTime aptBTime = LocalTime.parse(timeBString, TIME_FORMATTER);

        return aptATime.compareTo(aptBTime);
    }
}