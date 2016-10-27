package com.bcn.beacon.beacon.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.Manifest;
import com.bcn.beacon.beacon.R;
import com.google.firebase.database.*;
import com.joanzapata.iconify.widget.IconTextView;

import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by epekel on 2016-10-23.
 * Edited by atertzakian on 2016-10-27
 */
public class BeaconListView extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ArrayList<Event> events = new ArrayList<Event>();;
    //private ArrayList<Event> events;
    private ArrayList<String> eventNames;
    double userLng, userLat, eventLng, eventLat;
    private static final double maxRadius = 100.0;

    List<IconTextView> mTabs;

    private boolean checkGPSPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = getApplicationContext().checkCallingPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void getNearbyEvents() {
        LocationManager lm = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (checkGPSPermission()) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            userLng = location.getLongitude();
            userLat = location.getLatitude();
            //System.out.println(userLng);
            //System.out.println(userLat);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double distance;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    eventLng = Double.parseDouble(child.child("location").child("xcoord").getValue().toString());
                    eventLat = Double.parseDouble(child.child("location").child("ycoord").getValue().toString());
                    distance = distFrom(userLng, userLat, eventLng, eventLat);
                    if (distance <= maxRadius) {
                        Event event = new Event(child.child("title").getValue().toString(),
                                                child.child("host").getValue().toString(),
                                                distance, child.child("start").getValue().toString());
                        Log.i("NAME:", event.getName());
                        Log.i("DISTANCE:", Double.toString(distance));
                        /*event.setName(child.child("title").getValue().toString());
                        event.setHostId(child.child("host").getValue().toString());
                        event.setDistance(distance);*/
                        events.add(event);
                        /*int index = 0;
                        for (Event order : events) {
                            if (event.getDistance() < order.getDistance()) {
                                index = events.indexOf(order);
                            }
                        }
                        events.add(index, event);*/

                        //Log.i("DISTANCE:", Double.toString(distance));
                    }
                }
                Collections.sort(events, new DistanceComparator());

                // ListView stuff
                ListView beaconListView = (ListView) findViewById(R.id.listView);

                eventNames = new ArrayList<String>();
                for (Event event : events) {
                    eventNames.add(event.getName());
                    Log.i("EVENT NAME:", event.getName());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(BeaconListView.this, android.R.layout.simple_list_item_1, eventNames);

                beaconListView.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    /**
     * Java implementation of the Haversine formula for calculating the distance between two locations.
     * Taken from http://stackoverflow.com/questions/120283
     *                  /how-can-i-measure-distance-and-create-a-bounding-box-based-on-two-latitudelongi/123305#123305
     * @param userLat - latitude of the user's location
     * @param userLng - longitude of the user's location
     * @param eventLat - latitude of the event's location
     * @param eventLng - longitude of the event's location
     * @return dist - distance between the two locations
     */
    public static double distFrom(double userLat, double userLng, double eventLat, double eventLng) {
        double earthRadius = 6371.0; // kilometers (or 3958.75 miles)
        double dLat = Math.toRadians(eventLat-userLat);
        double dLng = Math.toRadians(eventLng-userLng);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(eventLat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        //Log.i("distFrom:", Double.toString(dist));

        return dist; // in kilometers
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        final IconTextView list = (IconTextView) findViewById(R.id.list);
        final IconTextView world = (IconTextView) findViewById(R.id.world);
        final IconTextView favourites = (IconTextView) findViewById(R.id.favourites);

        final LinearLayout create_event = (LinearLayout) findViewById(R.id.create_event);


        world.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTabColours();
                world.setBackgroundResource(R.color.currentTabColor);
                Intent intent = new Intent(BeaconListView.this, MainActivity.class);
                startActivity(intent);
            }
        });


        getNearbyEvents();

    }

    private void resetTabColours(){
        for(IconTextView itv : mTabs){
            itv.setBackgroundResource(R.color.otherTabColor);
        }
    }
}

class DistanceComparator implements Comparator<Event> {
    public int compare(Event left, Event right) {
        if (left.getDistance() < right.getDistance()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}



/*public class EventsAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<Event> events;

    private EventsAdapter(Context context, List<Event> events) {
        this.inflater = LayoutInflater.from(context);
        this.events = events;
    }

    @Override
    public int getCount() {
        return this.events.size();
    }

    @Override
    public Event getItem(int position) {
        return this.events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.events.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Event event = getItem(position);

        if(convertView == null) {
            // If convertView is null we have to inflate a new layout
            convertView = this.inflater.inflate(R.layout.activity_list_view, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvDisplayText = (TextView) convertView.findViewById(R.id.tvDisplayText);
            viewHolder.tvMeta = (TextView) convertView.findViewById(R.id.tvMeta);

            // We set the view holder as tag of the convertView so we can access the view holder later on.
            convertView.setTag(viewHolder);
        }

        // Retrieve the view holder from the convertView
        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        // Bind the values to the views
        viewHolder.tvDisplayText.setText(item.getDisplayText());
        viewHolder.tvMeta.setText(item.getMeta());

        return convertView;
    }
}*/
