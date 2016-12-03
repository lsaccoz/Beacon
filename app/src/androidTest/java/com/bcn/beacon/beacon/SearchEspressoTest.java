package com.bcn.beacon.beacon;

import android.support.test.filters.FlakyTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.bcn.beacon.beacon.Activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by neema on 2016-12-02.
 */

@RunWith(AndroidJUnit4.class)
public class SearchEspressoTest {

    private static String TEST_EVENT1_NAME = "MY_TEST_EVENT1";
    private static String TEST_EVENT2_NAME = "MY_TEST_EVENT2";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);


    @Before
    public void initTestEvent(){

        //change activity to create event
        onView(withId(R.id.create_event_fab)).perform(click());

        //create first test event
        onView(withId(R.id.input_name)).perform(typeText(TEST_EVENT1_NAME), closeSoftKeyboard());

        onView(withId(R.id.fab)).perform(click());

        //change activity to create event
        onView(withId(R.id.create_event_fab)).perform(click());

        //create second test event
        onView(withId(R.id.input_name)).perform(typeText(TEST_EVENT2_NAME), closeSoftKeyboard());

        onView(withId(R.id.fab)).perform(click());

        try {
            Thread.sleep(1000);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void SearchOnListTest(){

    }


}
