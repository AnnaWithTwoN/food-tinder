package edu.um.feri.pora.lib;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String id;
    private String name;
    private String photoUri;
    private List<String> likedBy = new ArrayList<>();
    private List<String> liked = new ArrayList<>();
    private List<String> conversations = new ArrayList<>();

    public User() { }

    public User(String id, String name, String uri) {
        this.id = id;
        this.name = name;
        this.photoUri = uri;
    }

    public void addConversation(String id){
        conversations.add(id);
    }

    public boolean hasLiked(String userId){
        for(String id: liked)
            if(id.equals(userId))
                return true;
        return false;
    }

    public boolean isLikedBy(String userId){
        for(String id: likedBy)
            if(id.equals(userId))
                return true;
        return false;
    }

    public void addLikedBy(String userId){
        likedBy.add(userId);
    }

    public void addLiked(String userId){
        liked.add(userId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public List<String> getConversations() {
        return conversations;
    }

    public void setConversations(List<String> conversations) {
        this.conversations = conversations;
    }

    public List<String> getLiked() {
        return liked;
    }

    public void setLiked(List<String> liked) {
        this.liked = liked;
    }
}
