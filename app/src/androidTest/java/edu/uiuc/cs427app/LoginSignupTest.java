package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginSignupTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    private void enterCredentials(String username) {
        onView(withId(R.id.etUsername))
                .perform(clearText(), typeText(username), closeSoftKeyboard());

        onView(withId(R.id.etPassword))
                .perform(clearText(), typeText("test_pass"), closeSoftKeyboard());

        onView(withId(R.id.etThemePrompt))
                .perform(clearText(), typeText("default theme"), closeSoftKeyboard());
    }

    @Test
    public void testLLMGeneratedUserSignUp() throws InterruptedException {
        String username = "llm_signup_user_" + System.currentTimeMillis();

        enterCredentials(username);

        onView(withId(R.id.btnSignUp)).perform(click());

        Thread.sleep(1000);

        // After sign-up, use the same credentials to sign in.
        enterCredentials(username);

        onView(withId(R.id.btnSignIn)).perform(click());

        Thread.sleep(1000);

        // Assertion: signed-up user should be able to enter MainActivity.
        onView(withId(R.id.buttonAddLocation)).check(matches(isDisplayed()));
    }

    @Test
    public void testLLMGeneratedUserLogin() throws InterruptedException {
        String username = "llm_login_user_" + System.currentTimeMillis();

        enterCredentials(username);

        onView(withId(R.id.btnSignUp)).perform(click());

        Thread.sleep(1000);

        enterCredentials(username);

        onView(withId(R.id.btnSignIn)).perform(click());

        Thread.sleep(1000);

        // Assertion: after successful login, MainActivity should be displayed.
        onView(withId(R.id.buttonAddLocation)).check(matches(isDisplayed()));
    }
}