package com.bcn.beacon.beacon;

import android.support.test.espresso.DataInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.bcn.beacon.beacon.Activities.CreateEventActivity;
import com.bcn.beacon.beacon.Activities.EventPageActivity;
import com.bcn.beacon.beacon.Activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class CommentsEspressoTest {

    private static boolean listEmpty = false;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    public void setup() {
        onView(withId(R.id.list)).perform(click());
        DataInteraction event = onData(anything()).inAdapterView(withId(R.id.listView)).atPosition(0);
        if (event.equals(null)) {
            // list emtpy, assert nothing
            listEmpty = true;
            System.out.println("List is empty");
            return;
        }
        else {
            event.perform(click());
        }
    }

    @Test
    public void check_features_displayed() {
        setup();
        items_are_viewable();
    }

    @Test
    public void post_invalid_comment() throws InterruptedException {
        setup();
        try {
            // click the comment button
            onView(withId(R.id.comment_button)).perform(click());
            // check if edittext has focus
            onView(withId(R.id.write_comment)).check(matches(hasFocus()));
            // make sure edittext is empty
            onView(withId(R.id.write_comment)).perform(typeText(""));
            // try to post an empty comment
            // this click is not performed
            onView(withId(R.id.post_comment)).perform(click());
            // the comment should not be there
            // onView(withId(R.id.write_comment)).check(matches(hasFocus()));
            // onData(anything()).inAdapterView(withId(R.id.comments_list)).atPosition(0).check(doesNotExist());
            // check if post comment button is not displayed
            onView(withId(R.id.post_comment)).check(matches(not(isDisplayed())));
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /*@Test
    public void post_valid_comment() {
        setup();
        // click the comment button
        onView(withId(R.id.comment_button)).perform(click());
        // write something
        onView(withId(R.id.write_comment)).perform(typeText("Test"));
        // post comment
        // TODO: Somehow, this click cannot be performed, fix
        onView(withId(R.id.post_comment)).perform(click());
        // check to see if it is displayed
        onData(anything()).inAdapterView(withId(R.id.comments_list)).atPosition(0).check(matches(isDisplayed()));
        // check the text
        onData(anything()).inAdapterView(withId(R.id.comments_list)).atPosition(0)
                .onChildView(withId(R.id.comment)).check(matches(withText("Test")));
    }*/

    /*@Test
    public void edit_comment() {
    }*/

    @Test
    public void discard_changes_to_comment() {
        setup();
        // click the comment button
        onView(withId(R.id.comment_button)).perform(click());
        // write something
        onView(withId(R.id.write_comment)).perform(typeText("Test"));
        // press back
        pressBack();
        // check dialog
        onView(withText("DISCARD COMMENT?")).check(matches(isDisplayed()));
        // press back again to confirm discard
        pressBack();
        // check if comments tab is closed
        onView(withId(R.id.post_comment)).check(matches(not(isDisplayed())));

    }

    /*@Test
    public void delete_comment() {

    }*/


    // Trivial check if items in event page are viewable
    public void items_are_viewable(){
        if (!listEmpty) {
            onView(withId(R.id.event_title)).check(matches(isDisplayed()));
            onView(withId(R.id.favourite_button)).check(matches(isDisplayed()));
            onView(withId(R.id.icon_text)).check(matches(isDisplayed()));
            onView(withId(R.id.event_description)).check(matches(isDisplayed()));
            onView(withId(R.id.cover)).check(matches(isDisplayed()));
            onView(withId(R.id.time_address)).check(matches(isDisplayed()));
            onView(withId(R.id.card_view_comments)).check(matches(isDisplayed()));
        }
    }

}