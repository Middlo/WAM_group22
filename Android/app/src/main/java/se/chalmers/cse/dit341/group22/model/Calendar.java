package se.chalmers.cse.dit341.group22.model;

import java.util.ArrayList;
import java.util.Date;

// My Calendar Class. Used by GSON to transfer JSON directly to Java Object
public class Calendar {
    public String _id;
    public ArrayList<Event> events;
    public ArrayList<Task> tasks;
    public ArrayList<Note> notes;
    public ArrayList<Reminder> reminders;

    Calendar() {
    }
}
