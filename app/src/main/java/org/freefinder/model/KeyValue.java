package org.freefinder.model;

import io.realm.RealmObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rade on 27.10.17..
 */

public class KeyValue extends RealmObject implements Parcelable {
    private String key;
    private String value;

    public KeyValue() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    protected KeyValue(Parcel in) {
        key = in.readString();
        value = (String) in.readValue(String.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeValue(value);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<KeyValue> CREATOR = new Parcelable.Creator<KeyValue>() {
        @Override
        public KeyValue createFromParcel(Parcel in) {
            return new KeyValue(in);
        }

        @Override
        public KeyValue[] newArray(int size) {
            return new KeyValue[size];
        }
    };

}
