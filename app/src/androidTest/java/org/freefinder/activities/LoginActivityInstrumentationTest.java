package org.freefinder.activities;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import org.freefinder.R;
import org.freefinder.activities.LoginActivity;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by rade on 9.7.17..
 */

public class LoginActivityInstrumentationTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Test
    public void validLogin() {
        onView(ViewMatchers.withId(R.id.email)).perform(typeText("john@doe.org"));
        onView(withId(R.id.password)).perform(typeText("freefinder123"));

        onView(withId(R.id.email_sign_in_button)).perform(click());
    }
}
