package com.example.taskorganizer;

import android.icu.text.SimpleDateFormat;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

public class TaskModel implements Serializable, Comparable<TaskModel> {
    String taskID;
    String taskName;
    String description;
    String timeOfCreation;
    String timeOfExecution;
    Boolean finished; // yes or no
    Boolean notification; // on or off
    String category;
    String attachmentFileName;
    String currentCategory;
    Date execution;




    public TaskModel(String taskID, String taskName, String description, String category, String timeOfCreation, String timeOfExecution,
                     Boolean finished, Boolean notification, String currentCategory, String attachmentFileName) throws ParseException {
        this.taskID = taskID;
        this.taskName = taskName;
        this.description = description;
        this.timeOfCreation = timeOfCreation;
        this.timeOfExecution = timeOfExecution;
        this.finished = finished;
        this.notification = notification;
        this.category = category;
        this.currentCategory = currentCategory;

        this.execution = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(timeOfExecution);
        this.attachmentFileName = attachmentFileName;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public String getTimeOfCreation() {
        return timeOfCreation;
    }

    public String getTimeOfExecution() {
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

    public void setTimeOfCreation(String timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public void setTimeOfExecution(String timeOfExecution) {
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

    @Override
    public int compareTo(TaskModel taskModel) {
        return execution.compareTo(taskModel.execution);
    }
}
