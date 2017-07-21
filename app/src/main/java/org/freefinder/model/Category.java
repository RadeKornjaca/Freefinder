package org.freefinder.model;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rade on 22.6.17..
 */
public class Category extends RealmObject {

    @PrimaryKey
    private String name;

    private Category category;

    public Category() {

    }

    public Category(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name;
    }
}
