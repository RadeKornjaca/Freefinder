package org.freefinder;

import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.EditText;

import org.freefinder.R;
import org.freefinder.activities.MainActivity;
import org.freefinder.login.LoginActivity;
import org.freefinder.shared.SharedPreferencesHelper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@LargeTest
public class LoginActivityInstrumentationTest {
    private final String EMAIL            = "john.doe@gmail.com";
    private final String VALID_PASSWORD   = "johndoe1";
    private final String INVALID_PASSWORD = "password";

    @Rule
    public ActivityTestRule loginActivityTestRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Before
    public void setup() {
        Intents.init();

        SharedPreferencesHelper.setAuthorizationToken(loginActivityTestRule.getActivity(), null); // clear any previous authorization token
    }

    @Test
    public void successfulLogin() {
        login(EMAIL, VALID_PASSWORD);

        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void unsuccessfulLogin() {
        login(EMAIL, INVALID_PASSWORD);

        onView(withId(R.id.password)).check(matches(withError(loginActivityTestRule.getActivity().getString(R.string.error_incorrect_password))));
    }

    @After
    public void teardown() {
        Intents.release();
    }

    private static void login(String EMAIL, String PASSWORD) {
        onView(ViewMatchers.withId(R.id.email)).perform(typeText(EMAIL));
        onView(withId(R.id.password)).perform(typeText(PASSWORD));

        onView(withId(R.id.email_sign_in_button)).perform(click());
    }

    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }
                EditText editText = (EditText) view;
                return editText.getError().toString().equals(expected);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}
