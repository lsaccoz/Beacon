package com.bcn.beacon.beacon.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.bcn.beacon.beacon.Data.Models.ListEvent;
import com.bcn.beacon.beacon.R;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by neema on 2016-10-16.
 * Implemented by epekel starting from 2016-10-28.
 */
public class ListFragment extends Fragment {

    private ListView listView;
    private ArrayList<ListEvent> events;
    private Context appContext;
    private SwipeRefreshLayout swipeContainer;
    EventListAdapter adapter;
    public Parcelable state;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    public ListFragment() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //since onAttach isn't called on versions of android with sdk level < 23
        //we need to check the version before we set the context
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            this.appContext = context;
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //for older versions of android, this callback method is used instead
        //so set the context within this method
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.appContext = activity;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //create our view from the xml doc
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        listView = (ListView) view.findViewById(R.id.listView);

        //set adapter for the events list view
        listView.setAdapter(adapter);
        Log.i("VIEW","CREATED");

        //Launch the event details page if the user clicks on an event item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListEvent event = (ListEvent) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), EventPageActivity.class);

                // pass the event id to the new activity
                intent.putExtra("eventId", event.getEventId());
                // to indicate that event page was clicked from list view
                intent.putExtra("from", 1);

                getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_EVENTPAGE);

            }
        });

        // initialize the swap container variable with the view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // refresh listener for loading new data
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //events.clear();
                events = ((MainActivity) getActivity()).getEventList();
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_purple);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the events from the parent activity
        //TODO ensure this gets an updated version of the events list
        events = ((MainActivity) getActivity()).getEventList();
        // Populate the list view
        adapter = new EventListAdapter(appContext, 0, events);

    }

    @Override
    public void onStart() {
        super.onStart();
    }


    /* Code for future functionality, in case we want to restore scroll position in list view
    @Override
    public void onPause() {
        state = listView.onSaveInstanceState();
        super.onPause();
    }

    /*@Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.setAdapter(adapter);

        if (state != null) {
            listView.onRestoreInstanceState(state);
        }

    }*/

    @Override
    public void onResume() {
        //adapter.notifyDataSetChanged();
        /*if (state != null) {
            listView.onRestoreInstanceState(state);
            Log.i("SUP", "MAN");
        }*/
        super.onResume();
    }

}