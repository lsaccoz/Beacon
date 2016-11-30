package com.bcn.beacon.beacon.Data.Models;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class Event {

    private String eventId;
    private String name;
    private String hostId;
    private String description;
    private Date date;
    private Location location;
    private ArrayList<Comment> commentsList;
    private HashMap<String, Comment> comments;


    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
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

        new ListEvent(this);
    }

    //untested
    public void update() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference events = database.getReference("Events");

        events.child(eventId).setValue(this);

        new ListEvent(this);
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

    public ArrayList<Comment> getCommentsList() {
        return commentsList;
    }

    public Location getLocation() {
        return location;
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

    public void setComments(ArrayList<Comment> commentsList) {
        this.commentsList = commentsList;
    }
}