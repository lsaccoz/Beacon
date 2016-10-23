package com.bcn.beacon.beacon.Data.Models;

/**
 * Created by neema on 2016-10-16.
 */
public class User {

    private Long _id;
    private String uuid;
    private String name;
    //private String[] host_EventIds;
    //private String[] favourite_EventIds;

    public String getId(){
        return uuid;
    }

    public String getName(){
        return name;
    }

//    public String[] getHost_EventIds(){
//        return host_EventIds;
//    }

//    public String[] getFavourite_EventIds(){
//        return favourite_EventIds;
//    }

    public void setId(String uuid){
        this.uuid = uuid;
    }

    public void setName(String name){
        this.name = name;
    }

//    public void setHost_EventIds(String[] host_eventIds){
//        this.host_EventIds = host_eventIds;
//    }

//    public void setFavourite_EventIds(String[] favourite_eventIds){
//        this.favourite_EventIds = favourite_eventIds;
//    }
}
