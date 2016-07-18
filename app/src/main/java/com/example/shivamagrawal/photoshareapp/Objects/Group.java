package com.example.shivamagrawal.photoshareapp.Objects;

public class Group {

    String id;
    String name;

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean equals(String otherID) {
        return id.equals(otherID);
    }

    // TODO: need toString() method?

}
