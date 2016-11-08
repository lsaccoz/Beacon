package com.bcn.beacon.beacon.Activities;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.preference.PreferenceManager;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.bcn.beacon.beacon.Fragments.SettingsFragment;
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
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AuthBaseActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener{

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;

    private MapFragment mMapFragment;
    private ListFragment mListFragment;
    private SettingsFragment mSettingsFragment;
    private List<IconTextView> mTabs;
    private TextView mTitle;

    private Map<String, Event> eventsMap = new HashMap<String, Event>();

    private FloatingActionButton mCreateEvent;
    private MainActivity mContext;

    private IconTextView mList;
    private IconTextView mWorld;
    private IconTextView mFavourites;
    private IconTextView mSettings;
    private static final String TAG = "MainActivity";
    public static int eventPageClickedFrom = 0;

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 816;

    /**
     * Copied over from BeaconListView
     */
    private DatabaseReference mDatabase;
    private ArrayList<Event> events = new ArrayList<Event>();
    private double userLng, userLat, eventLng, eventLat;
    private static final double maxRadius = 100.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide the action bar
        getSupportActionBar().hide();

//set default values for preferences if they haven't been modified yet
        PreferenceManager.setDefaultValues(this, R.xml.settings_fragment, false);

        //get the users location using location services
        getUserLocation();

        //retrieve all the Views that we would want to modify here
        mList = (IconTextView) findViewById(R.id.list);
        mWorld = (IconTextView) findViewById(R.id.world);
        mFavourites = (IconTextView) findViewById(R.id.favourites);
        mSettings = (IconTextView) findViewById(R.id.settings);
        mCreateEvent = (FloatingActionButton) findViewById(R.id.create_event_fab);

        //set the onClickListener to this activity
        mList.setOnClickListener(this);
        mWorld.setOnClickListener(this);
        mFavourites.setOnClickListener(this);
        mSettings.setOnClickListener(this);
        mCreateEvent.setOnClickListener(this);

        //create an initial map fragment
        mMapFragment = MapFragment.newInstance();

        //create our tab array to keep track of the state of each tab
        mTabs = new ArrayList<>();
        mTabs.add(mList);
        mTabs.add(mWorld);
        mTabs.add(mFavourites);
        mTabs.add(mSettings);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mFirebaseUser = user;
                    initMap();
                    Log.d(TAG, "onAuthStateChanged_Main:signed_in:" + mFirebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged_Main:signed_out");
                }
            }
        };
    }

    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        // get events from firebase
        getNearbyEvents();

        // added a condition to avoid creating a new instance of map fragment everytime we go back to main activity
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            mMapFragment = MapFragment.newInstance();
            FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.events_view, mMapFragment, getString(R.string.map_fragment));
            // push to stack so that the fragment transaction is recorded and the fragment will be
            // obtainable from the fragment manager
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

    /**
     * Here we implement the listener for all the views in this activity's view hierarchy
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.list): {
                //change tab colour
                resetTabColours();
                mList.setBackgroundResource(R.color.currentTabColor);

                //show create event button on this page
                mCreateEvent.setEnabled(true);
                mCreateEvent.setVisibility(View.VISIBLE);

                //get List fragment if exists
                Fragment fragment = getFragmentManager().findFragmentByTag(getString(R.string.list_fragment));
                if (fragment == null || !fragment.isVisible()) {
                    if (fragment == null) {
                        //if fragment hasn't been created, get a new one
                        mListFragment = ListFragment.newInstance();
                    } else {
                        //if fragment already exists, use it
                        mListFragment = (ListFragment) fragment;
                    }

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    //attach this fragment to the screen
                    transaction.replace(R.id.events_view, mListFragment, getString(R.string.list_fragment));
                    transaction.addToBackStack(null);

                    //allows for smoother transitions between screens
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                    transaction.commit();
                }
                break;
            }
            case (R.id.world): {

                //change tab colours
                resetTabColours();
                mWorld.setBackgroundResource(R.color.currentTabColor);

                //show create event button on this page
                mCreateEvent.setEnabled(true);
                mCreateEvent.setVisibility(View.VISIBLE);

                Fragment fragment = getFragmentManager().findFragmentByTag(getString(R.string.map_fragment));

                if (fragment == null || !fragment.isVisible()) {
                    //if fragment hasn't been created, create a new instance
                    if (fragment == null) {
                        mMapFragment = MapFragment.newInstance();

                        //else, set map fragment to retrieved fragment
                    } else {
                        mMapFragment = (MapFragment) fragment;
                    }

                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.events_view, mMapFragment, getString(R.string.map_fragment));
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();

                    mMapFragment.getMapAsync(this);
                }
                break;

            }

            case (R.id.favourites): {
                //TODO need to attach a fragment for this tab also
                resetTabColours();
                mFavourites.setBackgroundResource(R.color.currentTabColor);

                //hide create event button on this page
                mCreateEvent.setEnabled(false);
                mCreateEvent.setVisibility(View.GONE);

                signOut();
                break;
            }
            case (R.id.settings): {
                //change tab colours
                resetTabColours();
                mSettings.setBackgroundResource(R.color.currentTabColor);

                //hide create event button on this page
                mCreateEvent.setEnabled(false);
                mCreateEvent.setVisibility(View.GONE);

                //check if visible fragment is an instance of settings fragment already, if so do nothing
                Fragment fragment = getFragmentManager().findFragmentByTag(getString(R.string.settings_fragment));

                if (fragment == null || !fragment.isVisible()) {
                    if (fragment == null) {
                        mSettingsFragment = SettingsFragment.getInstance();
                    } else {
                        mSettingsFragment = (SettingsFragment) fragment;
                    }

                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.events_view, mSettingsFragment, getString(R.string.settings_fragment));
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();

                }
                break;
            }

            //if the user presses the floating button, launch the create event activity
            case (R.id.create_event_fab): {
                Intent intent = new Intent(this, CreateEventActivity.class);
                intent.putExtra("userlat", userLat);
                intent.putExtra("userlng", userLng);
                startActivity(intent);
            }
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
     *
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
    public void getUserLocation() {
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
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getApplicationContext(), "You need to enable location services in order to use Beacon", Toast.LENGTH_LONG);
            }
            return;
        }
    }

    /**
     * Gets nearby events according to the user's location
     */
    private void getNearbyEvents() {
        /* commented this out for now to fix the bug, look into memory leaks! */
        /*if (!events.isEmpty()) {
            events.clear();
        }*/
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
                        eventsMap.put(event.getEventId(), event);
                    }
                }
                Collections.sort(events, new DistanceComparator());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Java implementation of the Haversine formula for calculating the distance between two locations.
     * Taken from http://stackoverflow.com/questions/120283
     * /how-can-i-measure-distance-and-create-a-bounding-box-based-on-two-latitudelongi/123305#123305
     *
     * @param userLat  - latitude of the user's location
     * @param userLng  - longitude of the user's location
     * @param eventLat - latitude of the event's location
     * @param eventLng - longitude of the event's location
     * @return dist - distance between the two locations
     */
    private static double distFrom(double userLat, double userLng, double eventLat, double eventLng) {
        double earthRadius = 6371.0; // kilometers (or 3958.75 miles)
        double dLat = Math.toRadians(eventLat - userLat);
        double dLng = Math.toRadians(eventLng - userLng);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(eventLat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist; // in kilometers
    }

    /**
     * Getter method for that returns the events list
     *
     * @return list of nearby events
     */
    public ArrayList<Event> getEventList() {
        return events;
    }

    public ArrayList<Event> getRefreshedEventList() {
        getNearbyEvents();
        return events;
    }

    public Map<String, Event> getEventsMap() {
        return eventsMap;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getUserLocation();
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
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        Intent i = new Intent(MainActivity.this, SignInActivity.class);
                        MainActivity.this.startActivity(i);
                        MainActivity.this.finish();
                    }
                });
    }

    private void initMap() {
        if (mMap != null) {
            mMap.clear();
            if (mAuth.getInstance().getCurrentUser() != null) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                        .position(new LatLng(userLat, userLng))
                        .title(mAuth.getInstance().getCurrentUser().getDisplayName()));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13));
                marker.showInfoWindow();

            } else {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                        .position(new LatLng(49.2606, -123.2460))
                        .title("You ;)"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
                marker.showInfoWindow();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        initMap();
    }

    /**
     * resets all the tabs to the unselected color
     */
    private void resetTabColours() {
        for (IconTextView itv : mTabs) {
            itv.setBackgroundResource(R.color.otherTabColor);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
    /**
     * This method overrides the default back button functionality
     *
     * If the user is looking at a different tab the world tab will be loaded,
     * otherwise the activity will end and the user will return to the android home screen
     *
     */
    @Override
    public void onBackPressed() {
        //currently viewing the map
        if (mMapFragment != null && mMapFragment.isVisible()) {
            //return to home screen
            finish();

            //map fragment is active but not currently shown
        } else if (mMapFragment != null && !mMapFragment.isVisible()) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.events_view, mMapFragment, getString(R.string.map_fragment));
            transaction.addToBackStack(null);

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();

            //set the world tab as being selected
            resetTabColours();
            mWorld.setBackgroundResource(R.color.currentTabColor);

            mMapFragment.getMapAsync(this);

            //ensure that the create event tab is visible again
            mCreateEvent.setEnabled(true);
            mCreateEvent.setVisibility(View.VISIBLE);

        } else {
            finish();
        }
    }

    // To keep track of the view the event page was clicked on
    public static void setEventPageClickedFrom(int from) {
        eventPageClickedFrom = from;
    }

    @Override
    public void onResume() {
        // Temporary fix for going back to list view from event page
        // it actually shows fragment but the action bar goes back to map view
        if (eventPageClickedFrom == 1) {
            //set the world tab as being selected
            resetTabColours();
            mList.setBackgroundResource(R.color.currentTabColor);
            eventPageClickedFrom = 0;
        }
        super.onResume();
    }
}
