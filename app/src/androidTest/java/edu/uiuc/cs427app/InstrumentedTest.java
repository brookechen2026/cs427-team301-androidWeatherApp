package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.SystemClock;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.allOf;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    /**
     * Use {@link ActivityScenarioRule to create and launch the activity under test,
     * and close it
     * after test completes. It opens LoginActicity automatically.
     */
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    // helper function that helps each test function login to the application
    private void login(String username) throws InterruptedException {
        // enter username, password, and theme
        onView(withId(R.id.etUsername)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("test_pass"), closeSoftKeyboard());
        onView(withId(R.id.etThemePrompt)).perform(typeText("default theme"), closeSoftKeyboard());

        // tries account regardless and signs in
        onView(withId(R.id.btnSignUp)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.btnSignIn)).perform(click());
        Thread.sleep(1000);
    }

    // helper function which helps add and test functionality of adding a new city
    private void populateCity(String cityname) {
        // finds and clicks add location
        onView(withId(R.id.buttonAddLocation)).perform(click());
        SystemClock.sleep(1000);
        // types name into box
        onView(isAssignableFrom(android.widget.EditText.class)).perform(typeText(cityname), closeSoftKeyboard());
        // clicks add in add city
        onView(withText("Add")).perform(click());
        SystemClock.sleep(1000);
        // checks if city displayed matches cityname
        onView(withText(cityname)).check(matches(isDisplayed()));
    }

    /**
     * Tests the addCity function from LocationManager and UI
     */
    @Test
    public void testAddCity() throws InterruptedException {
        login("test_username1");
        populateCity("Chicago,IL,US");
    }

    /**
     * Tests the removeCity function from LocationManager and UI
     */
    @Test
    public void testRemoveCity() throws InterruptedException {
        login("test_username2");
        populateCity("Champaign,IL,US");

        // checks if city with specific name does not exist after clicking remove
        onView(allOf(withText("Remove"), hasSibling(withText("Champaign,IL,US")))).perform(click());
        Thread.sleep(1000);

        onView(withText("Champaign,IL,US")).check(doesNotExist());
    }

    /**
     * Tests the logout function and UI
     */
    @Test
    public void testLogout() throws InterruptedException {
        login("test_username3");
        onView(withId(R.id.buttonLogout)).perform(click());
        Thread.sleep(1000);

        // checks if the sign in screen reapeears. This means we logged out
        onView(withId(R.id.btnSignIn)).check(matches(isDisplayed()));
        onView(withId(R.id.etUsername)).check(matches(isDisplayed()));
    }

    /**
     * Tests the Weather View Functionality and Checks if correct information
     * is displayed such as the city. (City 1 Chicago)
     */
    @Test
    public void testWeatherFeatureCityChicago() throws InterruptedException {
        login("test_username4");
        String test_city = "Chicago,IL,US";
        populateCity(test_city);

        // click weather to view weather of chicago
        onView(allOf(withText("Weather"), hasSibling(withText(test_city)))).perform(click());
        Thread.sleep(2000);

        // assertions check if the city name displayed is of correct city
        // checks if all weather information is displayed
        onView(withText(org.hamcrest.Matchers.containsString(test_city))).check(matches(isDisplayed()));
        onView(withId(R.id.cityInfo)).check(matches(withText(org.hamcrest.Matchers.allOf(
                org.hamcrest.Matchers.containsString("TEMPERATURE"),
                org.hamcrest.Matchers.containsString("WEATHER"),
                org.hamcrest.Matchers.containsString("HUMIDITY"),
                org.hamcrest.Matchers.containsString("WIND SPEED")))));
    }

    /**
     * Tests the Weather View Functionality and Checks if correct information
     * is displayed such as the city. (City 2 Champaign)
     */
    @Test
    public void testWeatherFeatureCityChampaign() throws InterruptedException {
        login("test_username5");
        String test_city = "Champaign,IL,US";
        populateCity(test_city);

        // click weather to view weather of champaign
        onView(allOf(withText("Weather"), hasSibling(withText(test_city)))).perform(click());
        Thread.sleep(2000);

        // assertions check if the city name displayed is of correct city
        // checks if all weather information is displayed
        onView(withText(org.hamcrest.Matchers.containsString(test_city))).check(matches(isDisplayed()));
        onView(withId(R.id.cityInfo)).check(matches(withText(org.hamcrest.Matchers.allOf(
                org.hamcrest.Matchers.containsString("TEMPERATURE"),
                org.hamcrest.Matchers.containsString("WEATHER"),
                org.hamcrest.Matchers.containsString("HUMIDITY"),
                org.hamcrest.Matchers.containsString("WIND SPEED")))));
    }

    /**
     * Tests the location while adding multiple cities
     */
    // casuing issues with jacoco
    // @Test
    public void locationMultipleCities() throws InterruptedException {
        login("test_username6");

        String ChampaignTest = "Champaign,IL,US";
        String UrbanaTest = "Urbana,IL,US";

        populateCity(ChampaignTest);
        populateCity(UrbanaTest);

        // Make sure both cities are populated to begin with
        onView(withText(ChampaignTest)).check(matches(isDisplayed()));
        onView(withText(UrbanaTest)).check(matches(isDisplayed()));

        // click weather to view weather of Champaign
        onView(allOf(withText("Weather"), hasSibling(withText(ChampaignTest)))).perform(click());
        Thread.sleep(5000);

        onView(withText(org.hamcrest.Matchers.containsString(ChampaignTest)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.cityInfo)).check(matches(withText(org.hamcrest.Matchers.allOf(
                org.hamcrest.Matchers.containsString("TEMPERATURE"),
                org.hamcrest.Matchers.containsString("WEATHER"),
                org.hamcrest.Matchers.containsString("HUMIDITY"),
                org.hamcrest.Matchers.containsString("WIND SPEED")))));

        pressBack();
        Thread.sleep(5000);

        // check weather of Urbana
        onView(allOf(withText("Weather"), hasSibling(withText(UrbanaTest)))).perform(click());
        Thread.sleep(5000);

        onView(withText(org.hamcrest.Matchers.containsString(UrbanaTest)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.cityInfo)).check(matches(withText(org.hamcrest.Matchers.allOf(
                org.hamcrest.Matchers.containsString("TEMPERATURE"),
                org.hamcrest.Matchers.containsString("WEATHER"),
                org.hamcrest.Matchers.containsString("HUMIDITY"),
                org.hamcrest.Matchers.containsString("WIND SPEED")))));
    }

    /**
     * Tests removeCity with multiple cities present
     */
    @Test
    public void removeMultipleCities() throws InterruptedException {
        login("test_username7");
        populateCity("Champaign,IL,US");
        populateCity("Urbana,IL,US");

        // Remove only Champaign
        onView(allOf(withText("Remove"), hasSibling(withText("Champaign,IL,US")))).perform(click());
        Thread.sleep(1000);

        onView(withText("Champaign,IL,US")).check(doesNotExist());
        onView(withText("Urbana,IL,US")).check(matches(isDisplayed()));

        // Remove Urbana after Champaign
        onView(allOf(withText("Remove"), hasSibling(withText("Urbana,IL,US")))).perform(click());
        Thread.sleep(1000);

        onView(withText("Urbana,IL,US")).check(doesNotExist());
    }
}
