package org.freefinder.model;

import com.google.gson.annotations.SerializedName;

/**
 * Retrofit POJO class used for sending a new user registration request
 */
public class User {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("password_confirmation")
    private String passwordConfirmation;

    public User(String email, String password, String passwordConfirmation) {
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
}
