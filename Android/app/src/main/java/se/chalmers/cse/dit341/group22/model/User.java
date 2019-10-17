package se.chalmers.cse.dit341.group22.model;

import java.util.Date;

// My User Class. Used by GSON to transfer JSON directly to Java Object
public class User {
    public String _id;
    public String username;
    public String password;
    public String email;
    public String authenticated;
    public String userType;

    User() {
    }
}
