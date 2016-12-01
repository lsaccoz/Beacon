package com.bcn.beacon.beacon.DatabaseTests;

import com.bcn.beacon.beacon.Data.DistanceComparator;
import com.bcn.beacon.beacon.Data.Models.ListEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by neema on 2016-11-30.
 */
public class DataSortingTest {

    private ArrayList<ListEvent> listEvents = new ArrayList<>();


    @Test
    public void SortListEventsTest1(){

        ListEvent event1 = new ListEvent();
        event1.distance = 3;
        listEvents.add(event1);

        ListEvent event2 = new ListEvent();
        event2.distance = 1;
        listEvents.add(event2);

        ListEvent event3 = new ListEvent();
        event3.distance = 0;
        listEvents.add(event3);

        ListEvent event4 = new ListEvent();
        event4.distance = 4;
        listEvents.add(event4);

        ListEvent event5 = new ListEvent();
        event5.distance = 2;
        listEvents.add(event5);

        //sort the listEvent by distance
        Collections.sort(listEvents, new DistanceComparator());

        //assert that the ordering is correct
        for(int i = 0; i < listEvents.size(); i++){
            assert(listEvents.get(i).distance == i);
        }

    }

    @Test
    public void SortListEventsTest2(){

        ListEvent event1 = new ListEvent();
        event1.distance = 0.003;

        listEvents.add(event1);

        ListEvent event2 = new ListEvent();
        event2.distance = 0.001;
        listEvents.add(event2);

        ListEvent event3 = new ListEvent();
        event3.distance = 0.001;
        listEvents.add(event3);

        ListEvent event4 = new ListEvent();
        event4.distance = 0.003;
        listEvents.add(event4);

        ListEvent event5 = new ListEvent();
        event5.distance = 1.02;
        listEvents.add(event5);

        ListEvent event6 = new ListEvent();
        event6.distance = 0.003;
        listEvents.add(event6);


        Collections.sort(listEvents, new DistanceComparator());

        // this time we have duplicate elements, ensure that the elements
        // are sorted in non-descending order for event.distance
        for(int i = 1; i < listEvents.size(); i++){
            assert(listEvents.get(i).distance
                    >= listEvents.get(i-1).distance);

        }



    }

    @After
    public void clear(){
        listEvents.clear();
    }


}
