package org.freefinder.registration;

/**
 * Created by rade on 13.12.17..
 */

public interface RegistrationContract {
    // Interaction View <-> Presenter interfaces

    interface View {
        void showProgress(boolean isLoading);
        void successfulRegistrationRedirection();
        void showRegistrationError();
    }

    interface UserActionsListener {
        void submitRegistration(String email,
                                String password,
                                String repeatedPassword);
    }

    // Interaction Presenter <-> Model interfaces
    interface Model {
        void register(String email,
                      String password,
                      String repeatedPassword);
    }

    interface RegistrationStatus {
        void onSuccess();
        void onFailure();
    }
}
