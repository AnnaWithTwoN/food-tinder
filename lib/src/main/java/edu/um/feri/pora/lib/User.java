package edu.um.feri.pora.lib;

import java.util.ArrayList;
import java.util.Date;

public class User {
    private String name;
    private Date birthDate;
    private ArrayList<Photo> photos;
    private ArrayList<User> likedBy;
    private ArrayList<Conversation> conversations;

    public User(String name, Date birthDate) {
        this.name = name;
        this.birthDate = birthDate;

        photos = new ArrayList<>();
        likedBy = new ArrayList<>();
        conversations = new ArrayList<>();
    }

    public void addPhoto(Photo photo){
        photos.add(photo);
    }

    public String getName() {
        return name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public ArrayList<User> getLikedBy() {
        return likedBy;
    }

    public ArrayList<Conversation> getConversations() {
        return conversations;
    }
}
