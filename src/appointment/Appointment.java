// Modified by Holly Jordan 2024 from:
/*   Issuezilla in Java                                                       */
/*   COPYRIGHT 2005 JESSE WILSON                                              */
package appointment;

import java.util.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.Month;



public class Appointment {

    // mandatory appointment fields
	// ID
    private Integer id = null;
    // Time (a LocalTime object, such as 5:00 PM)
    private LocalTime time = null;
    // Date (a LocalDate object, such as May 3)
    private LocalDate date = null;
    // Appointment type
    private String appointmentType = null;
    // Who you are scheduling with
    private String who = null;

    // optional fields
    // Notes
    private String notes = null;
    private String shortDescription = null;
    
    private List keywords = new ArrayList();
    private List blocks = new ArrayList();
    private List cc = new ArrayList();
    // issue rich fields
    private List descriptions = new ArrayList();
    private List attachments = new ArrayList();
    private List activities = new ArrayList();
    private List duplicates = new ArrayList();
    private List dependsOn = new ArrayList();
    
    /**
     * Creates a new empty issue.
     */
    public Appointment() {
        // do nothing
    }
    
    /**
     * Creates a new issue that uses the specified issue as a template.
     */
    public Appointment(Appointment template) {
        id = template.id;
	    time = template.time;
	    date = template.date;
	    appointmentType = template.appointmentType;
	    notes = template.notes;
	    who = template.who;       
      
        // optional fields
        keywords.addAll(template.keywords);
        blocks.addAll(template.blocks);
        cc.addAll(template.cc);
        // issue rich fields
        descriptions.addAll(template.descriptions);
        attachments.addAll(template.attachments);
        activities.addAll(template.activities);
        duplicates.addAll(template.duplicates);
        dependsOn.addAll(template.dependsOn);
    }

    /**
     * ID of this appointment (unique key).
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Time of the appointment (load status).
     */
    public LocalTime getTime() {
        return this.time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * Date of this appointment
     */
    public LocalDate getDate() {
        return this.date;
    }

    public void setStatus(LocalDate date) {
        this.date = date;
    }
    
    /**
     *  Type of appointment being scheduled
     */
    public String getAppointmentType() {
        return this.appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    /**
     *  Name of "who" the appointment is being scheduled with
     */
    public String getWho() {
        return this.who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    
    /**
     *  Notes for the appointment, if any
     */
    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    /**
     * Short description of issue.
     */
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String toString() {
        return "Issue " + id + ": " + getShortDescription();
    }
}
