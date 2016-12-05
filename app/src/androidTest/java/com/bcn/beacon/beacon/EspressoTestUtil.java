package com.bcn.beacon.beacon;

import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.BoundedMatcher;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.instanceOf;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bcn.beacon.beacon.Data.Models.ListEvent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by neema on 2016-12-04.
 */
public class EspressoTestUtil {


    public static void createTestEvent(String name, @Nullable String description) {

        onView(withId(R.id.create_event_fab)).perform(click());

        onView(withId(R.id.input_name)).perform(typeText(name), closeSoftKeyboard());

        if(description != null) {
            onView(withId(R.id.input_description)).perform(typeText(description), closeSoftKeyboard());
        }else{
            onView(withId(R.id.input_description)).perform(typeText(""), closeSoftKeyboard());
        }

        onView(withId(R.id.fab)).perform(click());


    }

    public static void deleteTestEvent(String name){

        onView(withId(R.id.list)).perform(click());

        onData(allOf(instanceOf(ListEvent.class), EspressoTestUtil.withName(name)))
                .perform(click());

        // Open the overflow menu OR open the options menu,
        // depending on if the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());

        onView(anyOf(withId(R.id.delete), withText("Delete your event"))).perform(click());

        onView(withText("Yes")).perform(click());

    }

    public static Matcher<Object> withName(final String expectedName) {
        return new BoundedMatcher<Object, ListEvent>(ListEvent.class) {
            @Override
            protected boolean matchesSafely(ListEvent event) {
                return event.getName().equals(expectedName);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Has Name: " + expectedName);
            }
        };
    }


    public static ViewAction setText(final String text) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(EditText.class);
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((EditText) view).setText(text);
            }
        };
    }

    public static ViewAction custom_click() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isClickable();
            }

            @Override
            public String getDescription() {
                return "click on search view toggle button";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.performClick();
            }
        };
    }

}
