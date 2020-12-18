package edu.um.feri.pora.lib;

import java.util.UUID;

public class Photo {
    private String id;
    private String uri;
    private String name;

    public Photo(){
    }

    public Photo(String uri, String name) {
        this.uri = uri;
        this.name = name;
        //id = UUID.randomUUID().toString();
    }

    public String getUri() {
        return uri;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
