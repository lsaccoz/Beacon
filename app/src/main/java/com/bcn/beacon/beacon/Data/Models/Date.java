package com.bcn.beacon.beacon.Data.Models;

import com.bcn.beacon.beacon.Utility.DataUtil;

import java.util.Calendar;
import java.util.Locale;

public class Date {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;


    public String formattedTime() {
        boolean isPM = 12 <= hour && hour < 24;

        String s = String.format(Locale.US, "%02d:%02d %s",
                (hour == 12 || hour == 0) ? 12 : hour % 12, minute,
                isPM ? "PM" : "AM");

        StringBuilder sb = new StringBuilder(s);

        if (s.charAt(0) == '0')
            sb.deleteCharAt(0);

        s = sb.toString();
        return s;
    }

    public String formattedDate(){

        return DataUtil.convertMonthToString(getMonth(), DataUtil.LOWER_CASE)
                + " " + getDay()
                + ", " + formattedTime();
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

}