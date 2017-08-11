package org.freefinder.model;


import io.realm.RealmObject;
import io.realm.annotations.*;

/**
 * Created by rade on 22.6.17..
 */
public class Category extends RealmObject {

    @PrimaryKey
    private int id;

    @Index
    private String name;

    private Category parentCategory;

    public Category() {

    }

    public Category(int id, String name, Category parentCategory) {
        this.id = id;
        this.name = name;
        this.parentCategory = parentCategory;
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

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    @Override
    public String toString() {
        return name;
    }
}
