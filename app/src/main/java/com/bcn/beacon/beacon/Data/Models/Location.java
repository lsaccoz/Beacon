package com.bcn.beacon.beacon.Data.Models;


/**
 * Created by neema on 2016-10-16.
 */

public class Location {

    private double latitude;
    private double longitude;
    private String address;

    public String getAddress(){return address; }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address){this.address = address;}

}