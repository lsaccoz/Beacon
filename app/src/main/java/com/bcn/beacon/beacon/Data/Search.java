package com.bcn.beacon.beacon.Data;

import com.bcn.beacon.beacon.Data.Models.ListEvent;

import java.util.ArrayList;
import java.util.List;

public class Search {

    public static ArrayList<ListEvent> searchEvents(String query, List<ListEvent> events) {
        ArrayList<ListEvent> queries = new ArrayList<>();
        query = query.toLowerCase();
        for (ListEvent e : events)
            if (e.getName().toLowerCase().contains(query))
                queries.add(e);

        // if query doesn't find an exact match, look for typos
        if (queries.isEmpty()) {

            for (ListEvent e : events)
                for (String typo : StringAlgorithms.getStringTypos(query))
                    if (e.getName().toLowerCase().contains(typo)) {
                        queries.add(e);
                        break;
                    }

            // if query with typos doesn't find matches,
            // look for only exact matches in descriptions
            if (queries.isEmpty()) {

                for (ListEvent e : events)
                    if (e.getDescription().toLowerCase().contains(query))
                        queries.add(e);
            }
        }

        return queries;
    }

}
