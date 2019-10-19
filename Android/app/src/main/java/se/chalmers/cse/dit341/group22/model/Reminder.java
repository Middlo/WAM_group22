package se.chalmers.cse.dit341.group22.model;

import java.util.Date;

// My Reminder Class. Used by GSON to transfer JSON directly to Java Object
public class Reminder {
    private String _id;
    private String userId;
    private String topic;
    private String remindBefore;
    private String importanceLevel;
    private Date targetMoment;
    private String reminderFor;

    public Reminder() {
    }

    public String getId() {
        return _id;
    }

    public String getImportanceLevel() {
        return importanceLevel;
    }

    public String getUserId() {
        return userId;
    }

    public Date getTargetMoment() {
        return targetMoment;
    }

    public String getRemindBefore() {
        return remindBefore;
    }

    public String getReminderFor() {
        return reminderFor;
    }

    public String getTopic() {
        return topic;
    }

    public void setImportanceLevel(String importanceLevel) {
        this.importanceLevel = importanceLevel;
    }

    public void setRemindBefore(String remindBefore) {
        this.remindBefore = remindBefore;
    }

    public void setReminderFor(String reminderFor) {
        this.reminderFor = reminderFor;
    }

    public void setTargetMoment(Date targetMoment) {
        this.targetMoment = targetMoment;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
