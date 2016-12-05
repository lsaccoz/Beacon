package com.bcn.beacon.beacon;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.EditText;

import com.bcn.beacon.beacon.Activities.MainActivity;
import com.bcn.beacon.beacon.Data.Models.ListEvent;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


/**
 * Created by neema on 2016-12-02.
 */

@RunWith(AndroidJUnit4.class)
public class SearchDestroyEspressoTest {

    private static String TEST_EVENT1_NAME = "TEST_EVENT1";
    private static String TEST_EVENT2_NAME = "TEST_EVENT2";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);


    @Before
    public void initTestEvent(){

        EspressoTestUtil.createTestEvent(TEST_EVENT1_NAME, null);
        EspressoTestUtil.createTestEvent(TEST_EVENT2_NAME, null);

        try {
            Thread.sleep(1000);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void SearchAndDeleteTest(){

        //switch to list view tab
        onView(withId(R.id.list)).perform(click());

        //check that the first test event is displayed
        onData(allOf(instanceOf(ListEvent.class), EspressoTestUtil.withName(TEST_EVENT1_NAME)))
                .check(matches(isDisplayed()));

        //check that the second test event is displayed
        onData(allOf(instanceOf(ListEvent.class), EspressoTestUtil.withName(TEST_EVENT2_NAME)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.search_test)).perform(EspressoTestUtil.custom_click());

        onView(isAssignableFrom(EditText.class))
                .perform(EspressoTestUtil.setText(TEST_EVENT1_NAME), closeSoftKeyboard());

        //assert that the first event is shown
        onData(allOf(instanceOf(ListEvent.class), EspressoTestUtil.withName(TEST_EVENT1_NAME)))
                .check(matches(isDisplayed()));


//        //assert that the second event isn't shown
//        onData(allOf(instanceOf(ListEvent.class), EspressoTestUtil.withName(TEST_EVENT2_NAME)))
//                .check(matches(not(isDisplayed())));

        //delete the event that is shown
        EspressoTestUtil.deleteTestEvent(TEST_EVENT1_NAME);

//        //
//        onData(allOf(instanceOf(ListEvent.class), EspressoTestUtil.withName(TEST_EVENT1_NAME)))
//                .check(doesNotExist());

    }



}
