package org.odk.odknotifications.Model;

public class Response {
    private String responseID;
    private String notificationID;
    private String message;
    private String senderID;
    private long time;

    public Response(String notificationID, String message, String senderID, long time) {
        this.notificationID = notificationID;
        this.message = message;
        this.senderID = senderID;
        this.time = time;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getResponseID() {
        return responseID;
    }

    public void setResponseID(String responseID) {
        this.responseID = responseID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
