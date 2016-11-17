package com.bcn.beacon.beacon.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
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
import com.bcn.beacon.beacon.Utility.UI_Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The fragment for favourites page
 * TODO: Implementing adding to favourites inside Event or User
 * TODO: Adding empty views to lists
 * TODO: Implement the temporary fix to every fragment / or better: fix the bug :)
 */
public class FavouritesFragment extends Fragment {

    private ListView favouritesView;
    private ArrayList<ListEvent> favourites = new ArrayList<>();
    private ArrayList<String> favouriteIds = new ArrayList<>();
    private HashMap<String, ListEvent> eventsMap = new HashMap<>();
    private Context appContext;
    private String userId;
    EventListAdapter adapter;

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    public FavouritesFragment() {
        // Required empty public constructor
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new EventListAdapter(appContext, 0, favourites);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // create our view from the xml doc
        View view = inflater.inflate(R.layout.favourites_fragment, container, false);

        favouritesView = (ListView) view.findViewById(R.id.favouritesView);

        //hide the list view divider
        UI_Util.hideListViewDivider(favouritesView);

        // set adapter for the events list view
        favouritesView.setAdapter(adapter);
        favouritesView.setLongClickable(true);

        //Log.i("FAV VIEW", "CREATED");

        // the listener for item click to go to event page
        favouritesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListEvent event = (ListEvent) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), EventPageActivity.class);

                // pass the event id to the new activity
                intent.putExtra("Event", event.getEventId());
                // to indicate that event page was clicked from favourites view
                intent.putExtra("from", 2);

                getActivity().startActivity(intent);
            }
        });

        // the listener for item long click to ask to remove event from favourites
        favouritesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                ListEvent event = (ListEvent) parent.getAdapter().getItem(position);
                // play with the themes to find the best one
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                alert.setIcon(R.drawable.fire);
                alert.setTitle("NOT LIT ENOUGH?");
                alert.setMessage("Remove '" + event.getName() + "' from favourites?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    // check for android.view.WindowLeaked: exception!
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // remove event from favourites view and user favourites
                        removeFav(pos);
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                });

                alert.show();

                return true;
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        populateFav();
        adapter.notifyDataSetChanged();
        super.onResume();
    }


    /**
     * Function to be called in order get the data for populating the list
     */
    public void populateFav() {
        favouriteIds = ((MainActivity) getActivity()).getFavouriteIdsList();
        //Log.i("IDS SIZE", Integer.toString(favouriteIds.size()));
        eventsMap = ((MainActivity) getActivity()).getEventsMap();
        if (!favourites.isEmpty()) {
            favourites.clear();
        }
        for (int i = 0; i < favouriteIds.size(); i++) {
            //Log.i("SIZE", Integer.toString(eventsMap.size()));
            ListEvent event = eventsMap.get(favouriteIds.get(i));
            if (event != null) {
                favourites.add(event);
            }
        }
    }

    /**
     * Function to remove the favourite from the database as well as the list view
     * @param pos - position of the event to be removed from favourites in the list view
     */
    public void removeFav(int pos) {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String eventId = favourites.get(pos).getEventId();
        //favouritesView.removeViewAt(pos);
        favourites.remove(pos);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        users.child(userId).child("favourites").child(eventId).removeValue();
        adapter.notifyDataSetChanged();
    }

}
