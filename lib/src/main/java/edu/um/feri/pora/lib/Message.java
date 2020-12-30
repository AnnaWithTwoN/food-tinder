package edu.um.feri.pora.lib;

import java.util.Date;

public class Message {
    private String body;
    private User sender;
    private int length;
    private Date dateStamp;
    private boolean seen;

    public Message(){ }

    public Message(User sender, String body) {
        this.body = body;
        this.sender = sender;

        length = body.length();
        dateStamp = new Date();
    }

    public String getBody() {
        return body;
    }

    public User getSender() {
        return sender;
    }

    public int getLength() {
        return length;
    }

    public Date getDateStamp() {
        return dateStamp;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setDateStamp(Date dateStamp) {
        this.dateStamp = dateStamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
