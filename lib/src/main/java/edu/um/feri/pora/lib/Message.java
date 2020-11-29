package edu.um.feri.pora.lib;

import java.util.Date;

public class Message {
    private String body;
    private User sender;
    private int length;
    private Date dateStamp;

    public Message(String body, User sender) {
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
}
