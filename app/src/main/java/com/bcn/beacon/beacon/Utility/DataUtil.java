package com.bcn.beacon.beacon.Utility;

import android.widget.TextView;

/**
 * Created by neema on 2016-11-09.
 */
public class DataUtil {

    /**
     * Static helper method that converts an int representation of a month
     * to a three character (ALL-CAPS) string representation of a month
     *
     * @param month
     * @return
     */
    public static String convertMonthToString(int month) {
        switch (month) {

            case 1:
                return "JAN";
            case 2:
                return "FEB";
            case 3:
                return "MAR";
            case 4:
                return "APR";
            case 5:
                return "MAY";
            case 6:
                return "JUN";
            case 7:
                return "JUL";
            case 8:
                return "AUG";
            case 9:
                return "SEP";
            case 10:
                return "OCT";
            case 11:
                return "NOV";
            case 12:
                return "DEC";
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
