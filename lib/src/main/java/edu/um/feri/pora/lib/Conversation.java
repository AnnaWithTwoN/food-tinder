package edu.um.feri.pora.lib;

import java.util.ArrayList;

public class Conversation {
    private User opponentA;
    private User opponentB;
    private ArrayList<Message> messages;
    private int status;

    public Conversation(User opponentA, User opponentB) {
        this.opponentA = opponentA;
        this.opponentB = opponentB;

        messages = new ArrayList<>();
        status = 1; // running
    }

    public void addMessage(Message msg){
        messages.add(msg);
    }

    public User getOpponentA() {
        return opponentA;
    }

    public User getOpponentB() {
        return opponentB;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public int getStatus() {
        return status;
    }
}
