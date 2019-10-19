package se.chalmers.cse.dit341.group22.model;

import java.util.Date;

// My Task Class. Used by GSON to transfer JSON directly to Java Object
public class Task {

    private String _id;
    private String userId;
    private String taskTitle;
    private String taskDescription;
    private String importanceLevel;
    private Date deadline;

    public Task() {
    }

    public String getUserId() {
        return userId;
    }

    public String getImportanceLevel() {
        return importanceLevel;
    }

    public String getId() {
        return _id;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setImportanceLevel(String importanceLevel) {
        this.importanceLevel = importanceLevel;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
}
