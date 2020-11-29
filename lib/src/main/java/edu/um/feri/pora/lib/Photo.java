package edu.um.feri.pora.lib;

import java.util.ArrayList;

public class Photo {
    private String path;
    private ArrayList<String> tags;

    public Photo(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
