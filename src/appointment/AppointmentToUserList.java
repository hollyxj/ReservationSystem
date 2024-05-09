package appointment;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.issuezilla.Issue;

/**
 * An IssuesToUserList is a list of users that is obtained by getting the users from an issues list.
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class AppointmentToUserList extends TransformedList<Appointment, String> { 

  /**
   * Construct an IssuesToUserList from an EventList that contains only Issue objects.
   */
  public AppointmentToUserList(EventList<Appointment> source) {
    super(source);
    source.addListEventListener(this); 
  }

  /**
   * Gets the user at the specified index.
   */
  public String get(int index) { 
    Appointment apt = source.get(index);
    return apt.getWho();
  }

  /**
   * When the source issues list changes, propogate the exact same changes for the users list.
   */
  public void listChanged(ListEvent<Appointment> listChanges) {
    updates.forwardEvent(listChanges); 
  }

  /** {@inheritDoc} */
  protected boolean isWritable() { 
    return false;
  }
}