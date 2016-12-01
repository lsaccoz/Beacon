package com.bcn.beacon.beacon.DatabaseTests;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import com.bcn.beacon.beacon.Data.DistanceComparator;
import com.bcn.beacon.beacon.Data.Models.ListEvent;
import com.bcn.beacon.beacon.R;
import com.bcn.beacon.beacon.Utility.DataUtil;
import com.bcn.beacon.beacon.Utility.LocationUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by neema on 2016-11-30.
 */
public class DataFetchTest {

    private static Context instrumentationContext;

    @BeforeClass
    public static void initContext() {

        //initalize the app context so we can get user location
        instrumentationContext = InstrumentationRegistry.getContext();
    }




    @Test
    public void expiredEventsTest() {

        //get Firebase reference

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //create search query like we do in main activity
        Query searchParams = mDatabase.child("ListEvents").orderByChild("timestamp")
                .startAt(DataUtil.getExpiredDate());

        final ArrayList<ListEvent> events = new ArrayList<>();

        //add all events to our list according to the Query parameters
        searchParams.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    ListEvent event = eventSnapshot.getValue(ListEvent.class);
                    events.add(event);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //if any pulled events are expired then the implementation is broken
        Long expiredDate = DataUtil.getExpiredDate();
        for (ListEvent event : events) {
            if (event.getTimestamp() < expiredDate) {
                org.junit.Assert.fail("An expired list event was pulled from the database");
            }
        }

    }

    @Test
    public void outOfRangeEventsTest(){

        //get firebase reference
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final ArrayList<ListEvent> events = new ArrayList<ListEvent>();
        //get the searchRangeLimit for this user
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(instrumentationContext);

        final int searchRangeLimit = prefs.getInt("Search Range", 0);

        mDatabase.child("ListEvents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double distance;
                for (DataSnapshot event_snapshot : dataSnapshot.getChildren()) {
                    ListEvent event = event_snapshot.getValue(ListEvent.class);

                    double eventLat = event.getLocation().getLatitude();
                    double eventLng = event.getLocation().getLongitude();

                    double userLat;
                    double userLng;

                    Location userLocation = LocationUtil.getUserLocation(instrumentationContext);
                    userLat = userLocation.getLatitude();
                    userLng = userLocation.getLongitude();


                    distance = LocationUtil.distFrom(userLat, userLng, eventLat, eventLng);

                    if (distance <= searchRangeLimit) {
                        event.distance = distance;
                        events.add(event);

                    }
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        for(ListEvent event: events){
            assert (event.distance <= searchRangeLimit);
        }

    }

}
