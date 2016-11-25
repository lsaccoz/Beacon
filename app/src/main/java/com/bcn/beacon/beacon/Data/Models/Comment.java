package com.bcn.beacon.beacon.Data.Models;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by neema on 2016-10-16.
 */
public class Comment {

    private String eventId;
    private String id;
    private String userId;
    private String text;
    private Long date;
    private String userName;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    /** TODO: Filter feature for comments, maybe show just two comments (1 top rated, 1 most recent)
     *  TODO: and add a "show all comments" button that opens a fragment
     */

    public Comment writeComment() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference events = database.getReference("Events");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String commentId = events.child(eventId).child("comments").push().getKey();
        setId(commentId);
        setUserId(user.getUid());
        setUserName(user.getDisplayName());

        events.child(eventId).child("comments").child(id).setValue(this);

        return this;
    }

    public String getId(){
        return id;
    }

    public String getUserId() { return userId; }

    public String getUserName() { return userName; }

    /*public String getUserName() {
        //userName = ""; // in case user is not found
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        users.child(getUserId()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return userName;
    }*/

    public String getText(){
        return text;
    }

    public Long getDate(){
        return date;
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

    public void setDate(Long date) { this.date = date; }

    public void setEventId(String eventId) { this.eventId = eventId; }

    public void setUserName(String userName) { this.userName = userName; }

}