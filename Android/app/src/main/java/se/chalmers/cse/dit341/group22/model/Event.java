package se.chalmers.cse.dit341.group22.model;

import java.util.Date;

// My Event Class. Used by GSON to transfer JSON directly to Java Object
public class Event {
    private String _id;
    private String userId;
    private String title;
    private String description;
    private String importanceLevel;
    private Date startDate;
    private Date endDate;

    public Event() {
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getId() {
        return _id;
    }

    public String getDescription() {
        return description;
    }

    public String getImportanceLevel() {
        return importanceLevel;
    }

    public String getTitle() {
        return title;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setImportanceLevel(String importanceLevel) {
        this.importanceLevel = importanceLevel;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
