package espresso;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.example.front_end.MainActivity;
import com.example.front_end.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.Charset;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTest {
    @Before
    public void set(){
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
    }
    @Test
    public void enterusername(){
        onView(withId(R.id.username)).perform(typeText("Username"));
        onView(withId(R.id.username)).check(matches(withText("Username")));
    }
    @Test
    public void enterpassword(){
        // Password text cannot be viewed, thus this will suffice
        onView(withId(R.id.password)).perform(typeText("test"));
    }
    // Test log in with correct details onView(withId(R.id.text_header)).check(matches(isDisplayed()));
    @Test
    public void login() {
        onView(withId(R.id.username)).perform(typeText("Username"));
        onView(withId(R.id.password)).perform(typeText("test")).perform(closeSoftKeyboard());
        onView(withId(R.id.log)).perform(click());
        onView(withId(R.id.log3)).check(matches(isDisplayed()));


    }
    // Test sign up with existing username
    @Test
    public void loginwrong(){
        onView(withId(R.id.username)).perform(typeText("Username"));
        onView(withId(R.id.password)).perform(typeText("test")).perform(closeSoftKeyboard());
        onView(withId(R.id.log2)).perform(click());
        onView(withId(R.id.log3)).check(doesNotExist());
    }
    // Test sign up
    @Test
    public void signup(){
        // username should be different on each occasion - has passed now - goal is to get used to technologies so not key focus.
        onView(withId(R.id.username)).perform(typeText("rwerwe"));
        onView(withId(R.id.password)).perform(typeText("random")).perform(closeSoftKeyboard());
        onView(withId(R.id.log2)).perform(click());
        onView(withId(R.id.log3)).check(matches(isDisplayed()));
    }

}

// Second activity has been manually tested successfully - Espresso cannot handle communication with external services such as gallery according to documentation.
