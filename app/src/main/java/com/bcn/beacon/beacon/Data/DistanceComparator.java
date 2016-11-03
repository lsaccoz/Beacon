package com.bcn.beacon.beacon.Data;

/**
 * Created by epekel on 01/11/2016.
 */

import com.bcn.beacon.beacon.Data.Models.Event;

import java.util.Comparator;

/**
 * Comparator class for sorting events list by distance
 */
public class DistanceComparator implements Comparator<Event> {
    public int compare(Event left, Event right) {
        if (left.getDistance() < right.getDistance()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
