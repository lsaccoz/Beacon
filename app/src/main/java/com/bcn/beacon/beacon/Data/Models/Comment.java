package com.bcn.beacon.beacon.Data.Models;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by neema on 2016-10-16.
 */
public class Comment {

    private Long _id;
    private String eventId;
    private String id;
    private String userId;
    private String text;
    private String dateId;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }


    public Comment writeComment() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference events = database.getReference("Events");

        String commentId = events.child(eventId).child("comments").push().getKey();
        setId(commentId);
        setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());

        events.child(eventId).child("comments").child(id).setValue(this);

        return this;
    }

    public String getId(){
        return id;
    }

    public String getUserId() { return userId; }

    public String getText(){
        return text;
    }

    public String getDateId(){
        return dateId;
    }

    public String getEventId() { return eventId; }

    public void setId(String id){
        this.id = id;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setText(String text){
        this.text = text;
    }

    public void setDateId(String dateId) { this.dateId = dateId; }

    public void setEventId(String eventId) { this.eventId = eventId; }

}