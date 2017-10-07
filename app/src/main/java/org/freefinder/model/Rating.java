package org.freefinder.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rade on 24.8.17..
 */

public class Rating extends RealmObject implements Parcelable {

    @PrimaryKey
    private long id;
    private String rating;
    private Place place;

    public Rating() {

    }

    public Rating(long id, String rating, Place place) {
        this.id = id;
        this.rating = rating;
        this.place = place;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    protected Rating(Parcel in) {
        id = in.readLong();
        rating = in.readString();
        place = (Place) in.readValue(Place.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(rating);
        dest.writeValue(place);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Rating> CREATOR = new Parcelable.Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel in) {
            return new Rating(in);
        }

        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };
}
