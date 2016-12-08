package com.bcn.beacon.beacon.Utility;

import android.util.Log;
import android.widget.TextView;

import com.bcn.beacon.beacon.Data.Models.ListEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by neema on 2016-11-09.
 */
public class DataUtil {

    private static long EVENT_LIFETIME_IN_MILLIS = 172800000;
    private static String LOG_TAG = "DataUtil";

    public static int UPPER_CASE = 1;
    public static int LOWER_CASE = 0;

    /**
     * Static helper method that converts an int representation of a month
     * to a three character (ALL-CAPS) string representation of a month
     *
     * @param month
     * @return
     */
    public static String convertMonthToString(int month, int textCase) {
        switch (month) {

            case 1:
                return textCase == 1 ? "JAN" : "Jan";
            case 2:
                return textCase == 1 ? "FEB" : "Feb";
            case 3:
                return textCase == 1 ? "MAR" : "Mar";
            case 4:
                return textCase == 1 ? "APR" : "Apr";
            case 5:
                return textCase == 1 ? "MAY" : "May";
            case 6:
                return textCase == 1 ? "JUN" : "Jun";
            case 7:
                return textCase == 1 ? "JUL" : "Jul";
            case 8:
                return textCase == 1 ? "AUG" : "Aug";
            case 9:
                return textCase == 1 ? "SEP" : "Sep";
            case 10:
                return textCase == 1 ? "OCT" : "Oct";
            case 11:
                return textCase == 1 ? "NOV" : "Nov";
            case 12:
                return textCase == 1 ? "DEC" : "Dec";
        }
        return "";
    }

    /**
     * Static helper method for converting an int representation of a day to a
     * three character (ALL-CAPS) string representation
     */

    public static String convertDayToString(int day) {
        System.out.println(day);
        switch (day) {
            case 0:
                return "Sat";
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
        }
        return "";
    }

    /**
     * Get the earliest timestamp for which an event is still valid for retrieving
     * from the database
     *
     * @return
     *          A date represented in milliseconds
     */
    public static Long getExpiredDate(){

        Calendar calendar = Calendar.getInstance();
        Long currentTime = calendar.getTimeInMillis();

        Log.i(LOG_TAG, currentTime.toString());

        /** FOR DEBUGGING
        System.out.println(calendar.get(Calendar.YEAR));
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println(calendar.get(Calendar.MONTH));
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
         **/

        return currentTime - EVENT_LIFETIME_IN_MILLIS;
    }

    /**
     * Function that converts the ListEvent time/ date fields to a
     * formatted string containing information relating to the
     * ListEvent's starting time, the day it starts, and the month it
     * starts in.
     *
     * TODO THIS CAN DEFINITELY BE REFACTORED
     *
     * @param e must have time/ date fields
     * @return String s
     */

    public static String getTime(ListEvent e) {

        boolean time_of_day = false;

        if (e.getDate().getHour() >= 12)
            time_of_day = true;

        com.bcn.beacon.beacon.Data.Models.Date d = e.getDate();

        String s = String.format(Locale.US, "%02d:%02d %s",
                (d.getHour() == 12 || d.getHour() == 0) ? 12 : d.getHour() % 12, d.getMinute(),
                time_of_day ? "PM" : "AM");

        StringBuilder sb = new StringBuilder(s);

        if (s.charAt(0) == '0')
            sb.deleteCharAt(0);

        s = sb.toString();

        String date = DataUtil.convertMonthToString(d.getMonth(), DataUtil.LOWER_CASE) + " " + d.getDay();

        date = date + "," + " " + s;

        return date;
    }

    /**
     * Static help method that formats TextView Length
     *
     *
     */

//    public static void textViewFormatter(TextView textView, String text, int maxLineLength){
//
//        String new_text = text;
//        int length = 0;
//
//        for(int i = 0; i < new_text.length(); i++){
//            if(length >= maxLineLength && new_text.charAt(i) == ' '){
//                //new_text.(text.charAt(i), '\n');
//                length = 0;
//            }else{
//                length++;
//            }
//        }
//
//        textView.setText(new_text);
//    }
}
