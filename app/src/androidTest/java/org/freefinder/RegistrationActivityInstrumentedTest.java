package org.freefinder;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

import com.jakewharton.espresso.OkHttp3IdlingResource;

import org.freefinder.activities.LoginActivity;
import org.freefinder.registration.RegistrationActivity;
import org.freefinder.shared.SharedPreferencesHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
public class RegistrationActivityInstrumentedTest {

    @Rule
    public ActivityTestRule<RegistrationActivity> registrationActivityTestRule =
            new ActivityTestRule<>(RegistrationActivity.class);

    @Before
    public void setup() {
        Intents.init();

        SharedPreferencesHelper.setAuthorizationToken(registrationActivityTestRule.getActivity(), null); // clear any previous authorization token
    }

    @Test
    public void testNewUserRegistration() {
        final String email = "john.doe@gmail.com";
        final String password = "johndoe1";
        final String passwordConfirmation = "johndoe1";

        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.confirm_password)).perform(typeText(passwordConfirmation));

        onView(withId(R.id.email_sign_in_button)).perform(click());


        intended(hasComponent(LoginActivity.class.getName()));
    }

    @After
    public void teardown() {
        Intents.release();
    }
}
