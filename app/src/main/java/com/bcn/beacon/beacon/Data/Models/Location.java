package com.bcn.beacon.beacon.Data.Models;

/**
 * Created by neema on 2016-10-16.
 */
public class Location {

    private Long _id;
    private String uuid;
    private double latitude;
    private double longitude;

    public String getId(){
        return uuid;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setId(String uuid){
        this.uuid = uuid;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

}
