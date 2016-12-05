package com.bcn.beacon.beacon;

import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.bcn.beacon.beacon.Activities.CreateEventActivity;
import com.bcn.beacon.beacon.Activities.MainActivity;
import com.bcn.beacon.beacon.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class CreateEventEspressoTest {


    @Rule
    public ActivityTestRule<CreateEventActivity> mActivityRule =
            new ActivityTestRule<>(CreateEventActivity.class);

    @Test
    public void check_features_displayed() {
        items_are_viewable();
    }

    @Test
    public void create_invalid_event(){
        //clear event name
        onView(withId(R.id.input_name)).perform(clearText());
        //attempt to create event
        onView(withId(R.id.fab)).perform(click());

        //should be in same activity
        items_are_viewable();
    }

    @Test
    public void populate_current_time_and_date(){
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);

        int mYear = mCurrentTime.get(Calendar.YEAR);
        int mMonth = mCurrentTime.get(Calendar.MONTH);
        int mDay = mCurrentTime.get(Calendar.DAY_OF_MONTH);

        boolean isPM = (hour >= 12);

        mMonth = mMonth + 1;

        onView(withId(R.id.event_date)).check(matches(withText("" + mDay + "/" + mMonth + "/" + mYear)));

        onView(withId(R.id.event_time)).check(matches(withText(String.format(Locale.US, "%02d:%02d %s",
                (hour == 12 || hour == 0) ? 12 : hour % 12, minute,
                isPM ? "PM" : "AM"))));
    }


    @Test
    public void create_valid_event() {
        onView(withId(R.id.input_name)).perform(typeText("espresso test event"), closeSoftKeyboard());
        onView(withId(R.id.input_description)).perform(typeText("this event was created with an espresso test"), closeSoftKeyboard());

        onView(withId(R.id.input_name))
                .check(matches(withText("espresso test event")));

        onView(withId(R.id.input_description))
                .check(matches(withText("this event was created with an espresso test")));

        onView(withId(R.id.fab)).perform(click());

       // onView(withId(R.id.create_event_fab)).check(matches(isDisplayed()));
    }

    @Test
    public void click_image_button() {
        onView(withId(R.id.addImageButton)).perform(click());
        onView(withText("Choose from Library")).check(matches(isDisplayed()));
        onView(withText("Take Photo")).check(matches(isDisplayed()));
        onView(withText("Cancel")).check(matches(isDisplayed()));

        onView(withText("Cancel")).perform(click());

        onView(withText("Choose from Library")).check(doesNotExist());
        onView(withText("Take Photo")).check(doesNotExist());
        onView(withText("Cancel")).check(doesNotExist());
    }

    @Test
    public void enter_location_picker(){
        onView(withId(R.id.input_address)).perform(click());
        items_are_not_viewable();
        location_items_viewable();

        onView(withId(R.id.set_location_fab)).perform(click());
        items_are_viewable();
        location_items_not_viewable();
    }

    @Test
    public void set_location_to_userlocale(){
        onView(withId(R.id.input_address)).perform(click());
        items_are_not_viewable();
        location_items_viewable();

        onView(withId(R.id.reset_location_fab)).perform(click());
        onView(withId(R.id.set_location_fab)).perform(click());
        items_are_viewable();
        location_items_not_viewable();
    }

/* TODO: set location in google map API (not possible with espresso framework
    @Test
    public void set_location_pin(){
        onView(withId(R.id.input_address)).perform(click());
        items_are_not_viewable();
        location_items_viewable();

        onView(withId(R.id.set_location_fab)).perform(click());
        items_are_viewable();
        location_items_not_viewable();
    }


*/

    /* TODO: set location using place auto complete fragment (not possible with espresso framework
    @Test
    public void set_location_pin(){
        onView(withId(R.id.input_address)).perform(click());
        items_are_not_viewable();
        location_items_viewable();

        onView(withId(R.id.set_location_fab)).perform(click());
        items_are_viewable();
        location_items_not_viewable();
    }
*/

    public void items_are_viewable(){
        onView(withId(R.id.input_description)).check(matches(isDisplayed()));
        onView(withId(R.id.input_name)).check(matches(isDisplayed()));
        onView(withId(R.id.event_time)).check(matches(isDisplayed()));
        onView(withId(R.id.event_date)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
        onView(withId(R.id.addImageButton)).check(matches(isDisplayed()));
        onView(withId(R.id.input_name)).check(matches(isDisplayed()));
    }

    public void items_are_not_viewable(){
        onView(withId(R.id.input_description)).check(doesNotExist());
        onView(withId(R.id.input_name)).check(doesNotExist());
        onView(withId(R.id.event_time)).check(doesNotExist());
        onView(withId(R.id.event_date)).check(doesNotExist());
        onView(withId(R.id.fab)).check(doesNotExist());
        onView(withId(R.id.addImageButton)).check(doesNotExist());
        onView(withId(R.id.input_name)).check(doesNotExist());
    }

    public void location_items_viewable(){
        onView(withId(R.id.set_location_fab)).check(matches(isDisplayed()));
        onView(withId(R.id.reset_location_fab)).check(matches(isDisplayed()));
    }

    public void location_items_not_viewable(){
        onView(withId(R.id.set_location_fab)).check(doesNotExist());
        onView(withId(R.id.reset_location_fab)).check(doesNotExist());
    }

}