package edu.northeastern.pawpal;

public class MessageModel {
    private String message, senderId;

    public MessageModel(){

    }
    public MessageModel(String message, String senderId) {
        this.message = message;
        this.senderId = senderId;
//        this.receiver = receiver;
//        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

}
