package com.bcn.beacon.beacon.Activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcn.beacon.beacon.Data.DistanceComparator;
import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.Fragments.ListFragment;
import com.bcn.beacon.beacon.R;
import com.firebase.client.annotations.Nullable;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity
            implements OnMapReadyCallback,
            GoogleMap.OnMapClickListener,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    MapFragment mMapFragment;
    ListFragment mListFragment;
    LinearLayout mCustomActionBar;
    List<IconTextView> mTabs;
    TextView mTitle;

    private static final String TAG = "MainActivity";

    /**
     * Copied over from BeaconListView
     */
    private DatabaseReference mDatabase;
    private ArrayList<Event> events = new ArrayList<Event>();;
    private double userLng, userLat, eventLng, eventLat;
    private static final double maxRadius = 100.0;

    // Map for easily populating Event Pages
    private Map<String, Event> eventsMap = new HashMap<String, Event>();

    // constant for permission id
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 816;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = LayoutInflater.from(this);

        getUserLocation();

        mCustomActionBar = (LinearLayout) inflater.inflate(R.layout.custom_action_bar, null);
        actionBar.setCustomView(mCustomActionBar);
        Toolbar parent =(Toolbar) mCustomActionBar.getParent();//first get parent toolbar of current action bar
        parent.setContentInsetsAbsolute(0,0);// set padding programmatically to 0dp

        ViewGroup.LayoutParams lp = mCustomActionBar.getLayoutParams();
        lp.width= ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mCustomActionBar.setLayoutParams(lp);

        mTitle = (TextView) mCustomActionBar.findViewById(R.id.my_title);
        mTitle.setTypeface(Typeface.MONOSPACE);

        final IconTextView list = (IconTextView) findViewById(R.id.list);
        final IconTextView world = (IconTextView) findViewById(R.id.world);
        final IconTextView favourites = (IconTextView) findViewById(R.id.favourites);

        final LinearLayout create_event = (LinearLayout) findViewById(R.id.create_event);

        mTabs = new ArrayList <>();
        mTabs.add(list);
        mTabs.add(world);
        mTabs.add(favourites);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mFirebaseUser = user;
                    Log.d(TAG, "onAuthStateChanged_Main:signed_in:" + mFirebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged_Main:signed_out");
                }
            }
        };


        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                world.setEnabled(true);
                if (list.isEnabled()) {
                    resetTabColours();
                    list.setBackgroundResource(R.color.currentTabColor);

                    if (savedInstanceState == null) {
                        // This null check is apparently good to have in order to not have fragments created over and over again
                        mListFragment = ListFragment.newInstance();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();

                        transaction.replace(R.id.events_view, mListFragment);
                        transaction.addToBackStack(null);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                        transaction.commit();
                    }
                }
                list.setEnabled(false);
            }
        });

        world.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                list.setEnabled(true);
                if (world.isEnabled()) {
                    resetTabColours();
                    world.setBackgroundResource(R.color.currentTabColor);

                    getFragmentManager().popBackStackImmediate();
                }
                world.setEnabled(false);
            }
        });

        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                resetTabColours();
                favourites.setBackgroundResource(R.color.currentTabColor);
            }
        });

        create_event.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signOut();
            }
        });
    }

    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);

        getNearbyEvents();

        // added a condition to avoid creating a new instance of map fragment everytime we go back to main activity
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            mMapFragment = MapFragment.newInstance();
            FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.events_view, mMapFragment);
            // push to stack in order to switch between fragments with ease
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        mMapFragment.getMapAsync(this);
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    /**
     * Check for GPS permission
     * @return true if user has allowed access to location, false otherwise
     */
    private boolean checkGPSPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Gets the location of the user
     */
    private void getUserLocation() {
        LocationManager lm = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        if (checkGPSPermission()) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                userLat = location.getLatitude();
                userLng = location.getLongitude();
                //Log.i("PERMISSION:", "ALLOWED");
            }
        }
    }

    /**
     * Call back method: app supposedly calls this again after user allows location services
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Location location = null;
                LocationManager lm = (LocationManager) getSystemService(this.LOCATION_SERVICE);
                try {
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                catch (SecurityException e) {
                    e.printStackTrace();
                }
                if (location != null) {
                    userLng = location.getLongitude();
                    userLat = location.getLatitude();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "You need to enable location services in order to use this app", Toast.LENGTH_LONG);
            }
            return;
        }
    }

    /**
     * Gets nearby events according to the user's location
     */
    private void getNearbyEvents() {
        if (!events.isEmpty()) {
            events.clear();
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double distance;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    eventLat = Double.parseDouble(child.child("location").child("latitude").getValue().toString());
                    eventLng = Double.parseDouble(child.child("location").child("longitude").getValue().toString());
                    distance = distFrom(userLat, userLng, eventLat, eventLng);
                    if (distance <= maxRadius) {
                        Event event = new Event(child.getValue().toString(),
                                                child.child("name").getValue().toString(),
                                                child.child("hostId").getValue().toString(),
                                                eventLat, eventLng,
                                                child.child("date").child("hour").getValue().toString() + ':' + child.child("date").child("minute").getValue().toString(),
                                                child.child("description").getValue().toString());

                        // The arraylist "events" is specific to each user, and will be different for each Android phone.
                        // The distance field for events would not be on the Firebase database, but it is required to keep track
                        // of it here in order to do the comparisons (for distance sorting) and list view (for showing distance).
                        event.setDistance(distance);

                        //Log.i("NAME:", event.getName());
                        //Log.i("DISTANCE:", Double.toString(distance));
                        events.add(event);
                        eventsMap.put(event.getId(), event);
                    }
                }
                Collections.sort(events, new DistanceComparator());
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
    private static double distFrom(double userLat, double userLng, double eventLat, double eventLng) {
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

    /**
     * Getter method for that returns the events list
     * @return list of nearby events
     */
    public ArrayList<Event> getEventList() {
        return events;
    }
    public Map<String, Event> getEventsMap() { return eventsMap; }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    private void signOut() {

        mAuth.signOut();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast toast = Toast.makeText(MainActivity.this,
                                "Signed Out Successfully",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();

                        Intent i = new Intent(MainActivity.this, SignInActivity.class);
                        MainActivity.this.startActivity(i);
                        MainActivity.this.finish();
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if(mAuth != null && mAuth.getCurrentUser() != null){
            Marker marker = map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.beacon_icon))
                    .position(new LatLng(49.2606, -123.2460))
                    .title(mAuth.getCurrentUser().getDisplayName()));
            map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
            marker.showInfoWindow();}
        else{
            Marker marker = map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.beacon_icon))
                    .position(new LatLng(49.2606, -123.2460))
                    .title("BECON!"));
            map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
            marker.showInfoWindow();
        }
    }


    private void resetTabColours(){
        for(IconTextView itv : mTabs){
            itv.setBackgroundResource(R.color.otherTabColor);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
}
