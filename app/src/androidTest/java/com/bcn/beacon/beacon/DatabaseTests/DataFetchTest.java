package com.bcn.beacon.beacon.DatabaseTests;

import com.bcn.beacon.beacon.Data.Models.ListEvent;
import com.bcn.beacon.beacon.Utility.DataUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by neema on 2016-11-30.
 */
public class DataFetchTest {

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

}
