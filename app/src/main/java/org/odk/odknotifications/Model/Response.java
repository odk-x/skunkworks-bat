package org.odk.odknotifications.Model;

public class Response {
    private String responseID;
    private String response;
    private String senderID;
    private long time;

    public Response(String response, String senderID, long time) {
        this.response = response;
        this.senderID = senderID;
        this.time = time;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
