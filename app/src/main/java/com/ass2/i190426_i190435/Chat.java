package com.ass2.i190426_i190435;

public class Chat {
    int sender, receiver;
    String message, messageType;
    String date;
    int seen;

    public Chat(int sender, int receiver, String message, String date, String messageType, int seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date=date;
        this.messageType=messageType;
        this.seen=seen;
    }

    public Chat(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }
}
