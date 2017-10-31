package org.freefinder.model;

import io.realm.RealmObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rade on 24.10.17..
 */

public class AdditionalField extends RealmObject implements Parcelable {
    private String name;
    private String fieldType;

    public AdditionalField() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    protected AdditionalField(Parcel in) {
        name = in.readString();
        fieldType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(fieldType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AdditionalField> CREATOR = new Parcelable.Creator<AdditionalField>() {
        @Override
        public AdditionalField createFromParcel(Parcel in) {
            return new AdditionalField(in);
        }

        @Override
        public AdditionalField[] newArray(int size) {
            return new AdditionalField[size];
        }
    };
}
