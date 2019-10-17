package se.chalmers.cse.dit341.group22.model;

import java.util.Date;

// My Event Class. Used by GSON to transfer JSON directly to Java Object
public class Event {
    public String _id;
    public String userId;
    public String title;
    public String description;
    public String importanceLevel;
    public Date startDate;
    public Date endDate;

    Event() {
    }
}
