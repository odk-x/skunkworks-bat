package org.odk.odknotifications.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Notification {
    private String title;
    private String message;
    private Long date;
    private String group;

    public Notification(String title, String message, Long date, String group) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStringDate() {
        return new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(date);
    }
}
