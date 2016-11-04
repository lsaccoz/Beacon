package com.bcn.beacon.beacon.Data.Models;


import com.google.firebase.database.FirebaseDatabase;

import com.bcn.beacon.beacon.Data.Models.Date;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by neema on 2016-10-16.
 */

public class Event {

    //    private Long _id;
    private String eventId;
    private String name;
    private String hostId;
    private String description;
    private Date date;
    private Location location;
    private String timeStart_Id;
//    private int num_attendees;
//    private String locationId;
//    private String[] attendee_Ids;
//    private String timeEnd_Id;
//    private String[] postIds;
//    private String[] tags;

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
    }

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


    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public double getDistance() { return distance; }

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

//    public void setNumAttendees(int num_attendees){
//        this.num_attendees = num_attendees;
//    }

//    public void setLocationId(String locationId){
//        this.locationId = locationId;
//    }

//    public void setAttendee_Ids(String[] attendee_Ids){
//        this.attendee_Ids = attendee_Ids;
//    }


    public void setTimeStart_Id(String timeStart_Id){
        this.timeStart_Id = timeStart_Id;
    }


    public void setDistance(double distance) { this.distance = distance; }

    public void setLocation(double latitude, double longitude) {
        // convention = {ycoord, xcoord}
        location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);

    }


//    public void setTags(String[] tags){
//        this.tags = tags;
//    }



}