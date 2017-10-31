package org.freefinder.model;


import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.*;

/**
 * Created by rade on 22.6.17..
 */
public class Category extends RealmObject implements Parcelable {

    @PrimaryKey
    private long id;

    @Index
    private String name;

    private Category parentCategory;

    private RealmList<AdditionalField> additionalFields;

    public Category() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public RealmList<AdditionalField> getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(RealmList<AdditionalField> additionalFields) {
        this.additionalFields = additionalFields;
    }

    protected Category(Parcel in) {
        id = in.readLong();
        name = in.readString();
        parentCategory = (Category) in.readValue(Category.class.getClassLoader());
        additionalFields = (RealmList) in.readValue(RealmList.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeValue(parentCategory);
        dest.writeValue(additionalFields);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
