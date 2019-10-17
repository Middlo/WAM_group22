package se.chalmers.cse.dit341.group22.model;

import java.util.Date;

// My Note Class. Used by GSON to transfer JSON directly to Java Object
public class Note {
    public String _id;
    public String userId;
    public String topic;
    public String textContent;
    public Date createdOn;
    public Date lastUpdated;

    Note() {
    }
}
