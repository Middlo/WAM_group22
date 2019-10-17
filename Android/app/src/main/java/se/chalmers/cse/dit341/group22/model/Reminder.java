package se.chalmers.cse.dit341.group22.model;

import java.util.Date;

// My Reminder Class. Used by GSON to transfer JSON directly to Java Object
public class Reminder {
    public String _id;
    public String userId;
    public String topic;
    public String remindBefore;
    public String importanceLevel;
    public Date targetMoment;
    public String reminderFor;

    Reminder() {
    }
}
