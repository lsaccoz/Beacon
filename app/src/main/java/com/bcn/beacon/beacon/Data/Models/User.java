package com.bcn.beacon.beacon.Data.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String uuid;
    private String name;
    private String email;
    private String[] host_EventIds;
    private String[] favourite_EventIds;


    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uuid, String name, String email){
        setId(uuid);
        setName(name);
        setEmail(email);
    }

    public void upload(){

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

            uploadName();
            uploadEmail();
        }
    }

    public void uploadName(){
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
        users.child(getId()).child("name").setValue(getName());
    }

    public void uploadEmail(){
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
        users.child(getId()).child("email").setValue(getEmail());
    }

    public String getId(){
        return uuid;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String[] getHost_EventIds(){
        return host_EventIds;
    }

    public String[] getFavourite_EventIds(){
        return favourite_EventIds;
    }

    public void setId(String uuid){
        this.uuid = uuid;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setHost_EventIds(String[] host_eventIds){
        this.host_EventIds = host_eventIds;
    }

}
