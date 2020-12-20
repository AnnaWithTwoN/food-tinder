package edu.um.feri.pora.lib;

import java.util.ArrayList;
import java.util.HashMap;

public class Conversation {
    private String id;
    private User opponentA;
    private User opponentB;
    private HashMap<String, Message> messages;

    public Conversation() {
    }

    public Conversation(String id, User opponentA, User opponentB) {
        this.id = id;
        this.opponentA = opponentA;
        this.opponentB = opponentB;

        messages = new HashMap<String, Message>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOpponentA() {
        return opponentA;
    }

    public void setOpponentA(User opponentA) {
        this.opponentA = opponentA;
    }

    public User getOpponentB() {
        return opponentB;
    }

    public void setOpponentB(User opponentB) {
        this.opponentB = opponentB;
    }

    public HashMap<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, Message> messages) {
        this.messages = messages;
    }
}
