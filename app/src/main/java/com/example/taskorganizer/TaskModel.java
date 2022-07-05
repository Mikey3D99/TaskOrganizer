package com.example.taskorganizer;

import java.util.Date;

public class TaskModel {
    String taskName;
    String description;
    Date timeOfCreation;
    Date timeOfExecution;
    Boolean finished; // yes or no
    Boolean notification; // on or off
    String category;
    String attachmentFileName;


    public TaskModel(String taskName, String description, Date timeOfCreation, Date timeOfExecution,
                     Boolean finished, Boolean notification, String category, String attachmentFileName) {
        this.taskName = taskName;
        this.description = description;
        this.timeOfCreation = timeOfCreation;
        this.timeOfExecution = timeOfExecution;
        this.finished = finished;
        this.notification = notification;
        this.category = category;
        this.attachmentFileName = attachmentFileName;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public Date getTimeOfCreation() {
        return timeOfCreation;
    }

    public Date getTimeOfExecution() {
        return timeOfExecution;
    }

    public Boolean getFinished() {
        return finished;
    }

    public Boolean getNotification() {
        return notification;
    }

    public String getCategory() {
        return category;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimeOfCreation(Date timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public void setTimeOfExecution(Date timeOfExecution) {
        this.timeOfExecution = timeOfExecution;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }
}
