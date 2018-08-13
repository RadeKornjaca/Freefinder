package org.freefinder.login;

public interface LoginContract {
    // Interaction View <-> Presenter interfaces

    interface View {
        void showProgress(boolean isLoading);
        void successfulLoginRedirection();
        void showLoginError();
        void displayServerErrorMessage();
    }

    interface UserActionsListener {
        void submitLogin(String email,
                         String password);
    }

    // Interaction Presenter <-> Model interfaces
//    interface Model {
//        void register(String email,
//                      String password,
//                      String repeatedPassword);
//    }

    interface LoginStatus {
        void onSuccess(String authorizationToken);
        void onFailure();
    }
}
