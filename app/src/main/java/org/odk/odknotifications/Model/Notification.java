package org.odk.odknotifications.Model;

import java.text.SimpleDateFormat;

public class Notification {
    public static final String SIMPLE = "Simple";
    public static final String INTERACTIVE = "Interactive";

    private String id;
    private String title;
    private String message;
    private Long date;
    private String group;
    private String type;

    public Notification(String id, String title, String message, Long date, String group, String type) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.group = group;
        this.type = type;
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

    public String getStringDate(){
        return  new SimpleDateFormat("HH:mm dd/MM/yyyy").format(date);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
