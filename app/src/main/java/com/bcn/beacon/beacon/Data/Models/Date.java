package com.bcn.beacon.beacon.Data.Models;

import java.util.Calendar;
import java.util.Locale;

public class Date {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    //timestamp used to determine whether an event has expired or not
    private Long timestamp;


    public String formatted() {
        boolean isPM = 12 <= hour && hour < 24;

        return String.format(Locale.US, "%02d:%02d %s",
                (hour == 12 || hour == 0) ? 12 : hour % 12, minute,
                isPM ? "PM" : "AM");
    }

    /**
     * Generates a timestamp for this date object
     *
     * Pre-conditions: all fields must be initialized and not null
     * Post-conditions: timestamp will be set to a millisecond value
     *                  representing the start date of the event
     */
    public void generateTimeStamp(){

        Calendar calendar = Calendar.getInstance();
        calendar.set(this.getYear(), this.getMonth(),
                this.getDay(), this.getHour(),
                this.getHour(), this.getMinute());

        setTimestamp(calendar.getTimeInMillis());
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public Long getTimestamp() {return timestamp;}


    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setTimestamp(Long time){this.timestamp = time;}

}