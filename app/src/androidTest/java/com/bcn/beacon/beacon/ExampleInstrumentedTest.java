package com.bcn.beacon.beacon;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.view.View;

import com.bcn.beacon.beacon.Activities.CreateEventActivity;
import com.bcn.beacon.beacon.Activities.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest extends InstrumentationTestCase {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Instrumentation mInstrumentation = getInstrumentation();

        Instrumentation.ActivityMonitor monitor = mInstrumentation.addMonitor(MainActivity.class.getName(), null, false);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(mInstrumentation.getTargetContext(), MainActivity.class.getName());
        mInstrumentation.startActivitySync(intent);

        Activity currentActivity = getInstrumentation().waitForMonitor(monitor);
        assertNotNull(currentActivity);

        View v = currentActivity.findViewById(R.id.create_event_fab);
        assertNotNull(v);
        TouchUtils.clickView(this, v);

        mInstrumentation.removeMonitor(monitor);
        monitor = mInstrumentation.addMonitor(CreateEventActivity.class.getName(), null, false);

        assertEquals("com.bcn.beacon.beacon", appContext.getPackageName());
    }

}
