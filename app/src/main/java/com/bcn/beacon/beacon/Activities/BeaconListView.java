package com.bcn.beacon.beacon.Activities;

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
import android.view.View;
import android.widget.ListView;

import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.Manifest;
import com.bcn.beacon.beacon.R;
import com.google.firebase.database.*;

import java.security.Security;
import java.util.ArrayList;

/**
 * Created by epekel on 2016-10-23.
 */
public class BeaconListView extends AppCompatActivity {

    private DatabaseReference mDatabase;
    double userLng, userLat, eventLng, eventLat;
    private static final double maxRadius = 100.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

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
        mDatabase.child("Events").child("Event").child("location").child("xcoord").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventLng = Double.parseDouble(dataSnapshot.getValue().toString());
                //System.out.println(Double.parseDouble(dataSnapshot.getValue().toString()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        mDatabase.child("Events").child("Event").child("location").child("ycoord").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventLat = Double.parseDouble(dataSnapshot.getValue().toString());
                //System.out.println(Double.parseDouble(dataSnapshot.getValue().toString()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        double distance = distFrom(userLng, userLat, eventLng, eventLat);

        if (distance <= maxRadius) {
            // then event should be shown in list view
        }

        ListView beaconListView = (ListView) findViewById((R.id.listView));

        ArrayList<Event> events = new ArrayList<Event>();

    }

    private boolean checkGPSPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = getApplicationContext().checkCallingPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
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

        return dist; // in kilometers
    }

}
