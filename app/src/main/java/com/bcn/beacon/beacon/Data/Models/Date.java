package com.bcn.beacon.beacon.Data.Models;



/**
 * Created by neema on 2016-10-16.
 */

public class Date {

    private Long _id;

    private String uuid;
    private int day;
    private int month;
    private int year;
    private int timestamp;

    public String getId(){
        return uuid;
    }

    public int getDay(){
        return day;
    }

    public int getMonth(){
        return month;
    }

    public int getYear(){
        return year;
    }

    public int getTimestamp(){
        return timestamp;
    }

    public void setId(String uuid){
        this.uuid = uuid;
    }

    public void setDay(int day){
        this.day = day;
    }

    public void setMonth(int month){
        this.month = month;
    }

    public void setYear(int year){
        this.year = year;
    }

    public void setTimestamp(int timestamp){
        this.timestamp = timestamp;
    }
}
