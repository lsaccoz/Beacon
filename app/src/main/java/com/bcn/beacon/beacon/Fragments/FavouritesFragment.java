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
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bcn.beacon.beacon.Activities.EventPageActivity;
import com.bcn.beacon.beacon.Activities.MainActivity;
import com.bcn.beacon.beacon.Adapters.EventListAdapter;
import com.bcn.beacon.beacon.Data.DistanceComparator;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;


/**
 * The fragment for favourites page
 */
public class FavouritesFragment extends Fragment
        implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener{

    private ListView favouritesView;
    private ListView hostingView;
    private ArrayList<ListEvent> favourites = new ArrayList<>();
    private ArrayList<ListEvent> hosting = new ArrayList<>();
    private ArrayList<String> favouriteIds = new ArrayList<>();
    private ArrayList<String> hostingIds = new ArrayList<>();
    private HashMap<String, ListEvent> eventsMap = new HashMap<>();
    private Context appContext;
    private String userId;

    private TextView mHosting;
    private TextView mFavourites;
    private TextView mEmptyMessage;
    //EventListAdapter adapter;
    EventListAdapter hostingAdapter;
    EventListAdapter favAdapter;


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
        favouriteIds = ((MainActivity) getActivity()).getFavouriteIdsList();
        hostingIds = ((MainActivity) getActivity()).getHostIdsList();
//        populate(favourites, favouriteIds);
//        populate(hosting, hostingIds);

        favAdapter = new EventListAdapter(appContext, 0, favourites, favouriteIds);
        hostingAdapter = new EventListAdapter(appContext, 0, hosting, hostingIds);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // create our view from the xml doc
        View view = inflater.inflate(R.layout.favourites_fragment, container, false);

        mHosting = (TextView) view.findViewById(R.id.hosting_title);
        mFavourites = (TextView)  view.findViewById(R.id.favourites_title);
        mEmptyMessage = (TextView) view.findViewById(R.id.empty_fav);
        favouritesView = (ListView) view.findViewById(R.id.favouritesView);
        hostingView = (ListView) view.findViewById(R.id.hostingView);

        //hide the list view divider
        UI_Util.hideListViewDivider(favouritesView);
        UI_Util.hideListViewDivider(hostingView);

        // set adapter for the events list view
        favouritesView.setAdapter(favAdapter);
        favouritesView.setLongClickable(true);

        // set adapter for the hosting events list view
        hostingView.setAdapter(hostingAdapter);
        hostingView.setLongClickable(true);


        // the listener for item click to go to event page
        favouritesView.setOnItemClickListener(this);
        hostingView.setOnItemClickListener(this);

        // the listener for item long click to ask to remove event from favourites
        favouritesView.setOnItemLongClickListener(this);

        hostingView.setOnItemLongClickListener(this);

        resizeListView();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        favouriteIds = ((MainActivity) getActivity()).getFavouriteIdsList();
        hostingIds = ((MainActivity) getActivity()).getHostIdsList();

        populate(favourites, favouriteIds);
        populate(hosting, hostingIds);
        favAdapter.notifyDataSetChanged();
        hostingAdapter.notifyDataSetChanged();
        //((MainActivity) getActivity()).getSearchBar().setEnabled(true);
        //((MainActivity) getActivity()).getSearchBar().setVisibility(View.VISIBLE);
        super.onResume();

        resizeListView();
    }

    public void resizeListView() {
        if(hostingIds.size() == 0){
            mHosting.setVisibility(View.GONE);
        }else{
            mHosting.setVisibility(View.VISIBLE);
        }
        ViewGroup.LayoutParams params = hostingView.getLayoutParams();
        params.height = hostingIds.size()*450-20;
        hostingView.setLayoutParams(params);

        if(favouriteIds.size() == 0){
            mFavourites.setVisibility(View.GONE);
        }else{
            mFavourites.setVisibility(View.VISIBLE);
        }
        params = favouritesView.getLayoutParams();
        params.height = favouriteIds.size()*450-20;
        favouritesView.setLayoutParams(params);

        if(hostingIds.size() + favouriteIds.size() == 0)
            mEmptyMessage.setVisibility(View.VISIBLE);
        else
            mEmptyMessage.setVisibility(View.GONE);
    }



    /**
     * Function to be called in order get the data for populating the hosting and/or favourite list
     */
    public void populate(ArrayList<ListEvent> events, ArrayList<String> ids) {
        Calendar mcurrentDate = Calendar.getInstance();
        eventsMap = ((MainActivity) getActivity()).getEventsMap();
        if (!events.isEmpty()) {
            events.clear();
        }
        for (int i = 0; i < ids.size(); i++) {
            ListEvent event = eventsMap.get(ids.get(i));
            //only populates valid events that haven't expired
            if (event != null) {
                if(event.getTimestamp() + 2*DateUtils.DAY_IN_MILLIS > mcurrentDate.getTimeInMillis()) {
                    events.add(event);
                } else {
                    removeFav(ids.get(i));
                    events.remove(event);
                    break;
                }
            }else{
                removeFav(ids.get(i));
                break;
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
        favourites.remove(pos);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        users.child(userId).child("favourites").child(eventId).removeValue();
        favAdapter.notifyDataSetChanged();
        resizeListView();
    }

    public void removeFav(String eventId) {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        users.child(userId).child("favourites").child(eventId).removeValue();
        favouriteIds.remove(eventId);
        favourites.clear();
        populate(favourites, favouriteIds);
    }

    /**
     * Function to remove the hosted event from the database as well as the list view
     * @param pos - position of the event to be removed from favourites in the list view
     */
    public void removeHosting(int pos) {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String eventId = hosting.get(pos).getEventId();
        hosting.remove(pos);
        eventsMap.get(eventId).removeHosting(userId);
        eventsMap.get(eventId).delete();
        hostingIds.remove(eventId);
        hostingAdapter.notifyDataSetChanged();
        favAdapter.notifyDataSetChanged();

        resizeListView();
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final int pos = position;
        ListEvent event = (ListEvent) parent.getAdapter().getItem(position);
        // play with the themes to find the best one
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

        switch(parent.getId()) {
            case(R.id.favouritesView):{
                alert.setTitle("Remove '" + event.getName() + "' from favourites?");

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                });
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    // check for android.view.WindowLeaked: exception!
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // remove event from favourites view and user favourites
                        removeFav(pos);
                        dialog.dismiss();
                    }
                });

                alert.show();
                break;
            }
            case(R.id.hostingView):{
                alert.setTitle("Delete your event '" + event.getName() + "'?");

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                });

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    // check for android.view.WindowLeaked: exception!
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // remove event from favourites view and user favourites
                        removeHosting(pos);
                        dialog.dismiss();
                    }
                });

                alert.show();
                break;
            }
        }

        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ListEvent event = (ListEvent) parent.getAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), EventPageActivity.class);

        // pass the event id to the new activity
        intent.putExtra("Event", event.getEventId());
        // to indicate that event page was clicked from favourites view
        intent.putExtra("from", 2);


        getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_EVENTPAGE);

    }
}
