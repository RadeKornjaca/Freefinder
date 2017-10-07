package org.freefinder.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rade on 3.10.17..
 */

public class Revision implements Parcelable {
    private long id;
    private Parcelable proposable;

    public Revision() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Parcelable getProposable() {
        return proposable;
    }

    public void setProposable(Parcelable proposable) {
        this.proposable = proposable;
    }


    protected Revision(Parcel in) {
        id = in.readLong();
        proposable = (Parcelable) in.readValue(Parcelable.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeValue(proposable);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Revision> CREATOR = new Parcelable.Creator<Revision>() {
        @Override
        public Revision createFromParcel(Parcel in) {
            return new Revision(in);
        }

        @Override
        public Revision[] newArray(int size) {
            return new Revision[size];
        }
    };
}
