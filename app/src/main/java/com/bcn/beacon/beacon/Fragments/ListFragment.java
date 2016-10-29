package com.bcn.beacon.beacon.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bcn.beacon.beacon.Activities.MainActivity;
import com.bcn.beacon.beacon.Adapters.EventListAdapter;
import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.R;

import java.util.ArrayList;

/**
 * Created by neema on 2016-10-16.
 * Implemented by epekel starting from 2016-10-28.
 */
public class ListFragment extends Fragment {

    private ListView listView;
    private ArrayList<Event> events;
    private Context appContext;

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
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        events = ((MainActivity)getActivity()).getEventList();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Populate the list view
        EventListAdapter adapter = new EventListAdapter(appContext, 0, events);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

}