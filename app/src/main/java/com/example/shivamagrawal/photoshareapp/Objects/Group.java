package com.example.shivamagrawal.photoshareapp.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {

    String id;
    String name;

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public Group(Parcel in) {
        id = in.readString();
        name = in.readString();
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

    public String toString() {
        return id + "|" + name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    // TODO: need toString() method?

}
