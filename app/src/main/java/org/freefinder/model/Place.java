package org.freefinder.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rade on 9.8.17..
 */

public class Place extends RealmObject {
    @PrimaryKey
    private int id;

    private String name;

    private double lat;

    private double lng;

    private int likes;

    private int dislikes;

    public Place() {

    }

    public Place(int id, String name, double lat, double lng, int likes, int dislikes) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
