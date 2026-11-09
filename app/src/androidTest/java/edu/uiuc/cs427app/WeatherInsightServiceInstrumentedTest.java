package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WeatherInsightServiceInstrumentedTest {
    // using log activit for comprehsnive test
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    // method to login super quick so we can actually test
    private void doLoginStuffRealQuick(String uName) throws Exception {
        // type name
        onView(withId(R.id.etUsername)).perform(androidx.test.espresso.action.ViewActions.typeText(uName),
                androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
        // try some pass
        onView(withId(R.id.etPassword)).perform(androidx.test.espresso.action.ViewActions.typeText("test_pass"),
                androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
        onView(withId(R.id.etThemePrompt)).perform(androidx.test.espresso.action.ViewActions.typeText("default theme"),
                androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
        onView(withId(R.id.btnSignUp)).perform(click());
        // give system wroom to not crash it
        Thread.sleep(1000);
        onView(withId(R.id.btnSignIn)).perform(click());
        Thread.sleep(3000);
    }

    private void addCityHelperCuzWeNeedCities(String c) {
        onView(withId(R.id.buttonAddLocation)).perform(click());
        android.os.SystemClock.sleep(1000);
        // sleep it
        onView(androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom(android.widget.EditText.class)).perform(
                androidx.test.espresso.action.ViewActions.typeText(c),
                androidx.test.espresso.action.ViewActions.closeSoftKeyboard());
        // click add btn
        onView(withText("Add")).perform(click());
        android.os.SystemClock.sleep(1000);
    }

    @Test
    public void testNavigateToInsightAndVerifyUI() throws Exception {
        doLoginStuffRealQuick("test_some_guy_3");
        String my_city = "Chicago,IL,US";
        addCityHelperCuzWeNeedCities(my_city);
        // finding weather btn
        onView(org.hamcrest.CoreMatchers.allOf(withText("Weather"),
                androidx.test.espresso.matcher.ViewMatchers.hasSibling(withText(my_city)))).perform(click());
        Thread.sleep(30000);
        // check if city info is there
        onView(withId(R.id.cityInfo)).check(matches(isDisplayed()));
        // click on insight btn
        onView(withId(R.id.buttonWeatherInsight)).perform(click());
        // give llm some time
        Thread.sleep(30000);
        // check if buttons are on screen (used messy ids from before lol)
        onView(withId(R.id.insightTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.button3)).check(matches(isDisplayed()));
        onView(withId(R.id.button4)).check(matches(isDisplayed()));
    }

    @Test
    public void testInsightSelectionShowsAnswer() throws Exception {
        doLoginStuffRealQuick("test_some_guy_4");
        String someCity = "Champaign,IL,US";
        addCityHelperCuzWeNeedCities(someCity);
        // goto details
        onView(org.hamcrest.CoreMatchers.allOf(withText("Weather"),
                androidx.test.espresso.matcher.ViewMatchers.hasSibling(withText(someCity)))).perform(click());
        Thread.sleep(30000);
        // go to insights
        onView(withId(R.id.buttonWeatherInsight)).perform(click());
        Thread.sleep(30000);
        // click the 1st question button (button3)
        onView(withId(R.id.button3)).perform(click());
        Thread.sleep(30000);
        // llm answer
        // make sure the textview with anwser pops up
        onView(withId(R.id.textView4)).check(matches(isDisplayed()));
    }
}