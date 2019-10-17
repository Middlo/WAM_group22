package se.chalmers.cse.dit341.group22.model;

import java.util.Date;

// My Task Class. Used by GSON to transfer JSON directly to Java Object
public class Task {

    public String _id;
    public String userId;
    public String taskTitle;
    public String taskDescription;
    public String importanceLevel;
    public Date deadline;

    Task() {
    }
}
