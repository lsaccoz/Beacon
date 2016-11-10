package com.bcn.beacon.beacon.Data;

/**
 * Created by epekel on 01/11/2016.
 */

import com.bcn.beacon.beacon.Data.Models.ListEvent;

import java.util.Comparator;

/**
 * Comparator class for sorting events list by distance
 */
public class DistanceComparator implements Comparator<ListEvent> {
    public int compare(ListEvent left, ListEvent right) {
        if (left.distance < right.distance) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
