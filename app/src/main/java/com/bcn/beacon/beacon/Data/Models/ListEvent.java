package com.bcn.beacon.beacon.Data.Models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ListEvent {
    private String eventId;
    private String name;
    private String host;
    private Date date;
    private Location location;
    private String description;

    public double distance;
    //timestamp used to determine whether an event has expired or not
    private Long timestamp;

    public ListEvent() {

    }

    public ListEvent(Event event) {
        eventId = event.getEventId();
        name = event.getName();
        date = event.getDate();

        //generate a time stamp for this ListEvent based on it's date fields
        generateTimeStamp();
        location = event.getLocation();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        DatabaseReference nameRef = users.child(event.getHostId()).child("name");

        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                host = dataSnapshot.getValue(String.class);
                upload();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO add catch;
            }
        });
    }

    public void upload() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference list_events = database.getReference("ListEvents");
        list_events.child(eventId).removeValue();
        list_events.child(eventId).setValue(this);

    }

    public void delete() {
        this.setTimestamp(new Long(0));
        upload();
    }

    public void removeHosting(String hostId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        DatabaseReference hosting = users.child(hostId).child("hosting");

        hosting.child(eventId).removeValue();
    }

    /**
     * Generates a timestamp for this date object
     *
     * Pre-conditions: all fields must be initialized and not null
     * Post-conditions: timestamp will be set to a millisecond value
     *                  representing the start date of the event
     */
    public void generateTimeStamp(){

        Calendar calendar = Calendar.getInstance();
        calendar.set(
                date.getYear(),
                //Calendar object month is month mod 12, ie January is 0
                date.getMonth() - 1,
                date.getDay(), date.getHour(),
                date.getHour(), date.getMinute());

        setTimestamp(calendar.getTimeInMillis());
    }

    public Long getTimestamp() {return timestamp;}

    public String getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public String getHost() {
        return host;
    }

    public Location getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }


    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(Long time){this.timestamp = time;}
}
