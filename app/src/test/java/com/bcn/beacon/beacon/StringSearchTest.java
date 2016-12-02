package com.bcn.beacon.beacon;

import com.bcn.beacon.beacon.Data.Models.ListEvent;
import com.bcn.beacon.beacon.Data.Search;
import com.bcn.beacon.beacon.Data.StringAlgorithms;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StringSearchTest {

    @Test
    public void TestStringTypos(){

        List<String> typos = StringAlgorithms.getStringTypos("stuff");

        assert(typos != null);

        assert(typos.contains("stufg")); // keyboard typo

        assert(typos.contains("sturf")); // keyboard typo

        assert(typos.contains("sfuff")); // keyboard typo

        assert(typos.contains("styff")); // keyboard typo

        assert(typos.contains("stfuf")); // switched chars

        assert(typos.contains("tsuff")); // switched chars

        assert(typos.contains("stuf")); // missing char

        assert(typos.contains("stff")); // missing char

        assert(typos.contains("tuff")); // missing char

        typos = StringAlgorithms.getStringTypos("");

        assert(typos != null);
        assert(typos.isEmpty());

        typos= StringAlgorithms.getStringTypos(null);

        assert(typos != null);
        assert(typos.isEmpty());

    }

    @Test
    public void TestSearchEvents() {

        ListEvent le1 = new ListEvent();
            le1.setName("Poker night!");
            le1.setDescription("poker night at my place. bring cash.");

        ListEvent le2 = new ListEvent();
            le2.setName("halloween party place");
            le2.setDescription("in the spirit of October");

        ListEvent le3 = new ListEvent();
            le3.setName("soccer at the park");
            le3.setDescription("Bring shinguards! It will be fun but rough...");

        ListEvent le4 = new ListEvent();
            le4.setName("Wanna make cookies?");
            le4.setDescription("Baking cookies at my place. Delicious and fun!");

        ArrayList<ListEvent> eventList = new ArrayList<ListEvent>();
            eventList.add(le1); eventList.add(le2);
            eventList.add(le3); eventList.add(le4);

        ArrayList<ListEvent> Query1 = Search.searchEvents("Poker", eventList);
        ArrayList<ListEvent> Query2 = Search.searchEvents("Pokr", eventList);
        ArrayList<ListEvent> Query3 = Search.searchEvents("pojer", eventList);

        assert(Query1 != null && Query2 != null && Query3 != null);
        assert(containsUnique(Query1, le1));
        assert(containsUnique(Query2, le1));
        assert(containsUnique(Query3, le1));

        ArrayList<ListEvent> Query4 = Search.searchEvents("halloweem", eventList);
        ArrayList<ListEvent> Query5 = Search.searchEvents("October", eventList);

        assert(Query4 != null && Query5 != null);
        assert(containsUnique(Query4, le2));
        assert(containsUnique(Query5, le2));

        ArrayList<ListEvent> Query6 = Search.searchEvents("socxer", eventList);

        assert(Query6 != null);
        assert(containsUnique(Query6, le3));

        ArrayList<ListEvent> Query7 = Search.searchEvents("fun", eventList);

        assert(Query7 != null);
        assert(contains(Query7,le3) && contains(Query7,le4)); // fun is in both descriptions

        ArrayList<ListEvent> Query8 = Search.searchEvents("place", eventList);

        assert(Query8 != null);
        assert(containsUnique(Query8, le2)); // note how le4 is not included here because
                                             // title gets priority over description

        ArrayList<ListEvent> Query9  = Search.searchEvents("pokexx", eventList);
        ArrayList<ListEvent> Query10  = Search.searchEvents("", eventList);
        ArrayList<ListEvent> Query11 = Search.searchEvents("nothing", eventList);

        assert(Query9 != null && Query10 != null && Query11 != null);
        assert(Query9.isEmpty());
        assert(Query10.size() == 4);
        assert(Query11.isEmpty());

    }

    private boolean contains(ArrayList<ListEvent> events, ListEvent key) {
        for (ListEvent event : events)
            if (event.getName() == key.getName())
                return true;
        return false;
    }

    private boolean containsUnique(ArrayList<ListEvent> events, ListEvent key) {
        return events.size() == 1 && contains(events,key);
    }
}
