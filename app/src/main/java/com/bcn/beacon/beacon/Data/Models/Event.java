package com.bcn.beacon.beacon.Data.Models;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import com.bcn.beacon.beacon.Data.Models.Date;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by neema on 2016-10-16.
 */

public class Event {
    private String eventId;
    private String name;
    private String hostId;
    private String description;
    private Date date;
    private Location location;
    private String timeStart_Id;
    private String userName;
    private String email;
    private User host;
//    private int num_attendees;
//    private String locationId;
//    private String[] attendee_Ids;
//    private String[] postIds;
    private String[] tags;

    // temporary distance variable addition
    private double distance;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String eventId, String name, String hostId, double latitude, double longitude, String timeStart_Id, String description) {
        this.setEventId(eventId);
        this.setName(name);
        this.setHostId(hostId);
        this.setLocation(latitude, longitude);
        this.setTimeStart_Id(timeStart_Id);
        this.setDescription(description);

    }

    public void upload() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference events = database.getReference("Events");
        String eventId = events.push().getKey();
        setEventId(eventId);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setHostId(userId);

        events.child(eventId).setValue(this);

        DatabaseReference users = database.getReference("Users");
        users.child(userId).child("hosting").child(eventId).setValue(true);
        /* Commented out because Andy has done the same
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        users.child(userId).child("name").setValue(name);
        users.child(userId).child("email").setValue(email);*/

        new ListEvent(this);
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public void setHost(String uuid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        users.child(uuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUserName(dataSnapshot.child("name").getValue().toString());
                //Log.i("USERNAME", dataSnapshot.child("name").getValue().toString());
                setEmail(dataSnapshot.child("email").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //this.host = new User(uuid, userName, email);
    }

    public User getHost() {
        return host;
    }


    public String getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getHostId() {
        return hostId;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public Location getLocation() {
        return location;
    }

//    public int getNumAttendees(){
//        return num_attendees;
//    }

//    public String getLocationId(){
//        return locationId;
//    }

//    public String[] getAttendee_Ids(){
//        return attendee_Ids;
//    }

    public String getTimeStart_Id() {
        return timeStart_Id;
    }

    public double getDistance() {
        return distance;
    }


    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setTimeStart_Id(String timeStart_Id) {
        this.timeStart_Id = timeStart_Id;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setLocation(double latitude, double longitude) {
        // convention = {ycoord, xcoord}
        location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);

    }

//    public void setTags(String[] tags){
//        this.tags = tags;
//    }

//    public void setNumAttendees(int num_attendees){
//        this.num_attendees = num_attendees;
//    }

//    public void setLocationId(String locationId){
//        this.locationId = locationId;
//    }

//    public void setAttendee_Ids(String[] attendee_Ids){
//        this.attendee_Ids = attendee_Ids;
//    }
}