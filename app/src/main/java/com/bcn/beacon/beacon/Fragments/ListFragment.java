package com.bcn.beacon.beacon.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bcn.beacon.beacon.Activities.EventPageActivity;
import com.bcn.beacon.beacon.Activities.MainActivity;
import com.bcn.beacon.beacon.Adapters.EventListAdapter;
import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.R;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by neema on 2016-10-16.
 * Implemented by epekel starting from 2016-10-28.
 */
public class ListFragment extends Fragment {

    private ListView listView;
    private ArrayList<Event> events;
    private Context appContext;
    EventListAdapter adapter;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    public ListFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appContext = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.listView);

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        events = ((MainActivity) getActivity()).getEventList();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Populate the list view
        adapter = new EventListAdapter(appContext, 0, events);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), EventPageActivity.class);
                intent.putExtra("eventId", event.getEventId());

                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}