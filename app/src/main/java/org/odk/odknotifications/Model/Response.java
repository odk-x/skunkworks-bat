package org.odk.odknotifications.Model;

public class Response {
    private String responseID;
    private String message;
    private String senderID;
    private long time;

    public Response(String message, String senderID, long time) {
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
