package com.bcn.beacon.beacon.Activities;

import android.app.AlertDialog;
import android.app.Fragment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcn.beacon.beacon.BeaconApplication;
import com.bcn.beacon.beacon.Data.DistanceComparator;
import com.bcn.beacon.beacon.Data.Models.ListEvent;
import com.bcn.beacon.beacon.Data.Models.PhotoManager;
import com.bcn.beacon.beacon.Utility.SearchUtil;
import com.bcn.beacon.beacon.Fragments.FavouritesFragment;
import com.bcn.beacon.beacon.Fragments.ListFragment;
import com.bcn.beacon.beacon.Fragments.SettingsFragment;
import com.bcn.beacon.beacon.R;
import com.bcn.beacon.beacon.Utility.DataUtil;
import com.bcn.beacon.beacon.Utility.LocationUtil;
import com.bcn.beacon.beacon.Utility.UI_Util;
import com.firebase.client.annotations.Nullable;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

//import static com.bcn.beacon.beacon.R.id.container_all;
//import static com.bcn.beacon.beacon.R.id.container_current;
import static com.bcn.beacon.beacon.R.id.list;
import static com.bcn.beacon.beacon.R.id.time;
import static com.bcn.beacon.beacon.R.id.world;
import static java.lang.Math.cos;


public class MainActivity extends AuthBaseActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener {


    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private LocationUtil localUtil = new LocationUtil();

    private MapFragment mMapFragment;
    private ListFragment mListFragment;
    private SettingsFragment mSettingsFragment;
    private FavouritesFragment mFavouritesFragment;
    private RelativeLayout mContentLayout;
    private List<IconTextView> mTabs;
    private TextView mTitle;
    private Fragment mActiveFragment;
    private String pinAddress;

    private FloatingActionButton mCreateEvent;
    private MainActivity mContext;

    private FloatingActionButton searchButton;
    private SearchView searchBar;

    private boolean isAuthorized = false;

    private IconTextView mList;
    private IconTextView mWorld;
    private IconTextView mFavourites;
    private IconTextView mSettings;
    private static final String TAG = "MainActivity";

    public static int eventPageClickedFrom = -1;
    public static int REQUEST_CODE_EVENTPAGE = 10;
    public static int REQUEST_CODE_CREATEEVENT = 20;

    // constant for permission id
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 816;


    //private boolean showBarInMap = false;
    public boolean searchedMap = false;
    public boolean searchedList = false;
    public ArrayList<ListEvent> searchedEventsCache = new ArrayList<ListEvent>();

    /**
     * Copied over from BeaconListView
     */
    private DatabaseReference mDatabase;

    private double userLng, userLat, eventLng, eventLat;
    private static final double maxRadius = 100.0;
    private static final String FAVOURITES = "favourites";
    private static final String HOSTING = "hosting";


    // tracker for the temporary fix
    private static int tracker = 0; // -1

    private ArrayList<ListEvent> events = new ArrayList<>();
    private HashMap<String, ListEvent> eventsMap = new HashMap<>();
    private ArrayList<String> favouriteIds = new ArrayList<>();
    private ArrayList<String> hostingIds = new ArrayList<>();
    private ArrayList<ListEvent> favourites = new ArrayList<>();

    private ValueEventListener mEventsListener;
    private ValueEventListener mFavouritesListener;
    private ValueEventListener mHostingListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide the action bar
       // getSupportActionBar().hide();


//set default values for preferences if they haven't been modified yet
        PreferenceManager.setDefaultValues(this, R.xml.settings_fragment, false);

        Log.i("MAIN CREATED", "YES");


        //retrieve all the Views that we would want to modify here
        mContentLayout = (RelativeLayout) findViewById(R.id.activity_main);
        mList = (IconTextView) findViewById(list);
        mWorld = (IconTextView) findViewById(world);
        mFavourites = (IconTextView) findViewById(R.id.favourites);
        mSettings = (IconTextView) findViewById(R.id.settings);
        mCreateEvent = (FloatingActionButton) findViewById(R.id.create_event_fab);
        searchButton = (FloatingActionButton) findViewById(R.id.search_test);


        Window window = this.getWindow();
        //set the status bar color if the API version is high enough
        mContentLayout.setPadding(0, UI_Util.getStatusBarHeight(this), 0, 0);

//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showBarInMap = true;
//            }
//        });


        //customize the search bar
        searchBar = (SearchView) findViewById(R.id.searchBar);
        searchBar.setQueryHint(getString(R.string.query_string));

        SearchView.SearchAutoComplete searchAutoComplete =
                (SearchView.SearchAutoComplete)searchBar
                        .findViewById(android.support.v7.appcompat.R.id.search_src_text);

        searchBar.setIconifiedByDefault(true);

        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.light_gray));
        searchAutoComplete.setTextSize(20);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                searchBar.clearFocus();
                if (tracker == 1) { // list
                    searchedList = true;
                    ListFragment fragment = (ListFragment) getFragmentManager().findFragmentByTag(getString(R.string.list_fragment));
                    fragment.updateListForSearch(query);
                } else if (tracker == 0) { // map
                    searchedMap = true;
                    searchedEventsCache = SearchUtil.searchEvents(query, getEventList());
                    putEventPinsOnMap(searchedEventsCache);
                }
                return false;
            }

            int counter = 0;

            @Override
            public boolean onQueryTextChange(String newText) {
                searchedEventsCache = SearchUtil.searchEvents(newText, getEventList());
                if (tracker == 1) { // list
                    searchedList = newText.length() != 0; // if not empty
                    ListFragment fragment = (ListFragment) getFragmentManager().findFragmentByTag(getString(R.string.list_fragment));
                    if (fragment != null) {
                        fragment.updateListForSearch(newText);
                    }

                } else if (tracker == 0) { // world
                    searchedMap = newText.length() != 0; // if not empty
                    putEventPinsOnMap(searchedEventsCache);
                }
                return false;
            }
        });

        searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (tracker == 1) { // list
                    searchedList = false;
                    ListFragment fragment = (ListFragment) getFragmentManager().findFragmentByTag(getString(R.string.list_fragment));
                    fragment.updateListAllEvents();
                } else if (tracker == 0) { // world
                    searchedMap = false;
                    initMap(); // reset the map with all the events
                    searchBar.setEnabled(false);
                    searchBar.setVisibility(View.GONE);
                    searchButton.setEnabled(true);
                    searchButton.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar.setEnabled(true);
                searchBar.setVisibility(View.VISIBLE);
                searchButton.setEnabled(false);
                searchButton.setVisibility(View.GONE);
            }
        });


        //set the onClickListener to this activity
        mList.setOnClickListener(this);
        mWorld.setOnClickListener(this);
        mFavourites.setOnClickListener(this);
        mSettings.setOnClickListener(this);
        mCreateEvent.setOnClickListener(this);

        //create an initial map fragment
        mMapFragment = MapFragment.newInstance();
        mMapFragment.getMapAsync(this);

        //create our tab array to keep track of the state of each tab
        mTabs = new ArrayList<>();
        mTabs.add(mList);
        mTabs.add(mWorld);
        mTabs.add(mFavourites);
        mTabs.add(mSettings);

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .build();


        //.addApi(AppIndex.API).build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && !isAuthorized) {
                    setDataListeners();
                    mFirebaseUser = user;
                    Log.d(TAG, "onAuthStateChanged_Main:signed_in:" + mFirebaseUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged_Main:signed_out");
                }
            }
        };

        // get events from firebase
//        getNearbyEvents();
        // get user favourite ids from firebase
//        getFavouriteIds();
        // get the user location
        Location location = LocationUtil.getUserLocation(this);
        if (location != null) {
            userLat = location.getLatitude();
            userLng = location.getLongitude();
        }

    }

    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();

        mAuth.addAuthStateListener(mAuthListener);

//        // get user favourite ids from firebase
//        getIds(FAVOURITES, favouriteIds);
//
//        // get user hosting ids from firebase
//        getIds(HOSTING, hostingIds);


        // added a condition to avoid creating a new instance of map fragment everytime we go back to main activity
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            mMapFragment = MapFragment.newInstance();
            FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.events_view, mMapFragment, getString(R.string.map_fragment));
            // push to stack so that the fragment transaction is recorded and the fragment will be
            // obtainable from the fragment manager
            fragmentTransaction.addToBackStack(null);
            mActiveFragment = mMapFragment;
            fragmentTransaction.commit();
            Log.i("BACKSTACK COUNT", "0");
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
            case (list): {
                //change tab colour
                resetTabColours();
                mList.setTextColor(getResources().getColor(R.color.colorPrimary));

                //show create event button on this page
                mCreateEvent.setEnabled(true);
                mCreateEvent.setVisibility(View.VISIBLE);

//                searchButton.setEnabled(false);
//                searchButton.setVisibility(View.GONE);

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
                    mActiveFragment = mListFragment;

                    //allows for smoother transitions between screens
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                    transaction.commit();
                }
                tracker = 1;
                //searchBar.setVisibility(View.VISIBLE);

                break;
            }
            case (world): {

                //change tab colours
                resetTabColours();
                mWorld.setTextColor(getResources().getColor(R.color.colorPrimary));

                //show create event button on this page
                mCreateEvent.setEnabled(true);
                mCreateEvent.setVisibility(View.VISIBLE);

                searchBar.setEnabled(searchedMap);
                searchBar.setVisibility(searchedMap ? View.VISIBLE : View.GONE);

                Fragment fragment = getFragmentManager().findFragmentByTag(getString(R.string.map_fragment));

                if (fragment == null || !fragment.isVisible()) {
                    //if fragment hasn't been created, create a new instance
                    if (fragment == null) {
                        mMapFragment = MapFragment.newInstance();

                        //else, set map fragment to retrieved fragment
                    } else {
                        mMapFragment = (MapFragment) fragment;
                    }
                    mActiveFragment = mMapFragment;

                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.events_view, mMapFragment, getString(R.string.map_fragment));
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();

                    mMapFragment.getMapAsync(this);
                }
                tracker = 0;

                searchButton.setEnabled(!searchedMap);
                searchButton.setVisibility(!searchedMap ? View.VISIBLE : View.GONE);

                break;
            }


            case (R.id.favourites): {

                resetTabColours();
                mFavourites.setTextColor(getResources().getColor(R.color.colorPrimary));

                //hide create event button on this page
                mCreateEvent.setEnabled(true);
                mCreateEvent.setVisibility(View.VISIBLE);

                searchButton.setEnabled(false);
                searchButton.setVisibility(View.GONE);

                //get List fragment if exists
                Fragment fragment = getFragmentManager().findFragmentByTag(getString(R.string.favourites_fragment));
                if (fragment == null || !fragment.isVisible()) {
                    if (fragment == null) {
                        //if fragment hasn't been created, get a new one
                        mFavouritesFragment = FavouritesFragment.newInstance();
                    } else {
                        //if fragment already exists, use it
                        mFavouritesFragment = (FavouritesFragment) fragment;
                    }

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    //attach this fragment to the screen
                    transaction.replace(R.id.events_view, mFavouritesFragment, getString(R.string.favourites_fragment));
                    transaction.addToBackStack(null);
                    mActiveFragment = mFavouritesFragment;

                    //allows for smoother transitions between screens
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                    transaction.commit();
                }

                searchBar.setEnabled(false);
                searchBar.setVisibility(View.GONE);

                tracker = 2;
                break;
            }
            case (R.id.settings): {
                //change tab colours
                resetTabColours();
                mSettings.setTextColor(getResources().getColor(R.color.colorPrimary));

                //hide create event button on this page
                mCreateEvent.setEnabled(false);
                mCreateEvent.setVisibility(View.GONE);

                searchButton.setEnabled(false);
                searchButton.setVisibility(View.GONE);

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
                //searchBar.setVisibility(View.GONE);
                break;
            }

            //if the user presses the floating button, launch the create event activity
            case (R.id.create_event_fab): {

                //check if the user has a stable internet connection, if not
                //display an alert dialog
                if (!BeaconApplication.isNetworkAvailable(this)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            new ContextThemeWrapper(this, R.style.DialogTheme));

                    //set message notifying user that the create event feature is unavailable
                    //with no internet connection
                    builder.setMessage(getString(R.string.no_internet_create_event_message))
                            .setTitle(getString(R.string.no_internet_title))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog signInDialog = builder.create();

                    //allow user to dismiss dialog by touching outside it's scope
                    signInDialog.setCanceledOnTouchOutside(true);

                    signInDialog.show();

                    UI_Util.setDialogStyle(signInDialog, this);

                } else {
                    Intent intent = new Intent(this, CreateEventActivity.class);
                    intent.putExtra("userlat", userLat);
                    intent.putExtra("userlng", userLng);
                    // for temporary fix
                    if (mActiveFragment != null && mActiveFragment == mListFragment) {
                        intent.putExtra("from", 1);
                    } else if (mActiveFragment != null && mActiveFragment == mMapFragment) {
                        // don't really need this, but keep for now
                        //Log.i("ACTIVE", "MAP");
                        intent.putExtra("from", 0);
                    } else if (tracker == 1) {
                        //Log.i("ACTIVE", "NOT MAP AND MAP NOT NULL");
                        intent.putExtra("from", 1);
                    }

                    // startActivity(intent);
                    // start activity for result using same code for now
                    startActivityForResult(intent, REQUEST_CODE_EVENTPAGE);
                }
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
     * Call back method: app supposedly calls this again after user allows location services
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (requestCode == PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getApplicationContext(), "You need to enable location services in order to use Beacon", Toast.LENGTH_LONG);
            }
            return;
        }
    }

    /**
     * method to set all our data listeners for main activity, this includes
     * nearby events, favourited events and hosting events
     */
    private void setDataListeners() {

        getNearbyEvents();
        getIds(FAVOURITES, favouriteIds);
        getIds(HOSTING, hostingIds);
    }

    /**
     * TODO THIS CAN BE REFACTORED
     */
    public void getNearbyEvents() {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // use firebase query to only return events who's start
        // date is greater than 2 days prior to now
        Query searchParams = mDatabase.child("ListEvents").orderByChild("timestamp")
                .startAt(DataUtil.getExpiredDate());

        if (mEventsListener != null) {
            searchParams.removeEventListener(mEventsListener);
        }
        mEventsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!events.isEmpty()) {
                    events.clear();
                    eventsMap.clear();
                }

                double distance;

                PhotoManager photoManager = PhotoManager.getInstance();

                //get the searchRangeLimit for this user
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                int searchRangeLimit = prefs.getInt(getString(R.string.pref_range_key), 0);

                for (DataSnapshot event_snapshot : dataSnapshot.getChildren()) {
                    ListEvent event = event_snapshot.getValue(ListEvent.class);

                    double eventLat = event.getLocation().getLatitude();
                    double eventLng = event.getLocation().getLongitude();
                    distance = LocationUtil.distFrom(userLat, userLng, eventLat, eventLng);

                    if (distance <= searchRangeLimit) {
                        event.distance = distance;

                        eventsMap.put(event.getEventId(), event);
                        events.add(event);
                        photoManager.downloadThumbs(event.getEventId());
                    }
                }
                Collections.sort(events, new DistanceComparator());

                // if this listener is triggered we know that the user is authorized so we don't
                // need to try and set the listener anymore
                isAuthorized = true;
                initMap();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        searchParams.addValueEventListener(mEventsListener);

    }

    /**
     * Function to get the event ids of user's favourites
     */
    private void getIds(final String eventType, final ArrayList<String> idList) {
        try {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference users = database.getReference("Users");

            if (eventType.equals(HOSTING)) {
                if (mHostingListener != null) {
                    users.child(userId).child(eventType).removeEventListener(mHostingListener);
                }
                mHostingListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!idList.isEmpty()) {
                            idList.clear();
                        }
                        //HashMap<String, ListEvent> eventsMap = MainActivity.getEventsMap();
                        for (DataSnapshot fav_snapshot : dataSnapshot.getChildren()) {
                            //Log.i("FAV_SNAPSHOT", fav_snapshot.getKey());
                            idList.add(fav_snapshot.getKey());
                        }
                        // if this listener is triggered we know that the user is authorized so we don't
                        // need to try and set the listener anymore
                        isAuthorized = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                users.child(userId).child(eventType).addValueEventListener(mHostingListener);
            } else if (eventType.equals(FAVOURITES)) {
                if (mFavouritesListener != null) {
                    users.child(userId).child(eventType).removeEventListener(mFavouritesListener);
                }
                mFavouritesListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!idList.isEmpty()) {
                            idList.clear();
                        }
                        //HashMap<String, ListEvent> eventsMap = MainActivity.getEventsMap();
                        for (DataSnapshot fav_snapshot : dataSnapshot.getChildren()) {
                            //Log.i("FAV_SNAPSHOT", fav_snapshot.getKey());
                            idList.add(fav_snapshot.getKey());
                        }
                        // if this listener is triggered we know that the user is authorized so we don't
                        // need to try and set the listener anymore
                        isAuthorized = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                users.child(userId).child(eventType).addValueEventListener(mFavouritesListener);
            }


        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationUtil.getUserLocation(this);
        if (location != null) {
            userLat = location.getLatitude();
            userLng = location.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }


    //ArrayList<Event> event_list = new ArrayList<Event>();

    HashMap<String, String> m = new HashMap<String, String>();
    HashMap<String, ListEvent> events_list = new HashMap<String, ListEvent>();

    private void initMap() {

        if (mMap != null) {
            mMap.clear();

            ArrayList<ListEvent> toPopulate = new ArrayList<ListEvent>(events);

            if (searchedMap) {
                toPopulate = searchedEventsCache;
            }


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            int pref = prefs.getInt(getString(R.string.pref_range_key), 0);

            LatLngBounds Bound = new LatLngBounds(
                    new LatLng(userLat - (pref / 110.574), userLng - (pref / 111.320 * cos(pref / 110.574))),
                    new LatLng(userLat + (pref / 110.574), userLng + (pref / 111.320 * cos(pref / 110.574))));

            mMap.setLatLngBoundsForCameraTarget(Bound);

            mMap.setMaxZoomPreference(17);
            mMap.setMinZoomPreference(12);

            if (mAuth != null && mAuth.getCurrentUser() != null) {

                mMap.setInfoWindowAdapter(new InfoWindow());
                mMap.setOnMarkerClickListener(this);
                mMap.setOnInfoWindowClickListener(this);

                if (!toPopulate.isEmpty()) {
                    putEventPinsOnMap(toPopulate);

                    //add marker for user
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_location_icon))
                            .position(new LatLng(userLat, userLng)));
                } else {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_location_icon))
                            .position(new LatLng(userLat, userLng))
                            .title("Your Location"));

                    mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 50, null);
                }


                LatLng UserLocation = new LatLng(userLat, userLng);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(UserLocation)
                        .zoom(13)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

        }
    }

    public void putEventPinsOnMap(List<ListEvent> events) {
        if (events.isEmpty()) return;
        if (mMap != null) {
            mMap.clear();

            //add marker for user
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_location_icon))
                    .position(new LatLng(userLat, userLng)));

            for (ListEvent e : events) {

                double latitude = e.getLocation().getLatitude();
                double longitude = e.getLocation().getLongitude();

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                        .position(new LatLng(latitude, longitude)));

                m.put(marker.getId(), e.getEventId());
                events_list.put(marker.getId(), e);

            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (m.get(marker.getId()) != null) {
            String event = m.get(marker.getId());

            Intent i = new Intent(this, EventPageActivity.class);
            i.putExtra("Event", event);
            startActivity(i);
        }
    }

    List<Address> addresses = null;

    @Override
    public boolean onMarkerClick(Marker marker) {

        ListEvent event = events_list.get(marker.getId());

        if (event != null) {
            pinAddress = event.getLocation().getAddress();
        } else {
            pinAddress = "";
        }

        float zoom = mMap.getCameraPosition().zoom;

        if (zoom < 14.00) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(marker.getPosition())
                    .zoom(14)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));


        marker.showInfoWindow();
        return true;
    }


    public void onMapReady(GoogleMap map) {
        mMap = map;

        initMap();

    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    /**
     * This method overrides the default back button functionality
     * <p/>
     * If the user is looking at a different tab the world tab will be loaded,
     * otherwise the activity will end and the user will return to the android home screen
     */
    @Override
    public void onBackPressed() {
        // fix for search bar being visible on back pressed in fragments
        if (tracker == 1 || tracker == 2) {
            tracker = 0;
            searchBar.setEnabled(false);
            searchBar.setVisibility(View.GONE);
        }


        //map fragment is active but not currently shown
        if (mMapFragment != null && !mMapFragment.isVisible()) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.events_view, mMapFragment, getString(R.string.map_fragment));
            // we need to handle the back stack so it pops
            transaction.addToBackStack(null);

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
            //Log.i("STATUS", "MAP FRAG ACTIVE, BUT NOT SHOWN");

            //set the world tab as being selected
            resetTabColours();
            mWorld.setTextColor(getResources().getColor(R.color.colorPrimary));

            mMapFragment.getMapAsync(this);
            mActiveFragment = mMapFragment;

            //ensure that the create event tab is visible again
            mCreateEvent.setEnabled(true);
            mCreateEvent.setVisibility(View.VISIBLE);

        } else {

            //currently viewing the map, go back to android home screen
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


        }
    }

    // To keep track of the view the event page was clicked on

    public static void setEventPageClickedFrom(int from) {
        eventPageClickedFrom = from;
    }

    // To set the visibility of search bar in fragments
    public SearchView getSearchBar() {
        return searchBar;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * This is the temporary fix for displaying the current tab correctly
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_EVENTPAGE && resultCode == RESULT_CANCELED) {
            //Fragment fragment =
            //Log.i("ON RESULT", "YES");
            switch (eventPageClickedFrom) {
                case (1): {
                    resetTabColours();
                    mList.setTextColor(getResources().getColor(R.color.colorPrimary));
                    //eventPageClickedFrom = 0;
                    searchButton.setEnabled(false);
                    searchButton.setVisibility(View.GONE);
                    searchBar.setEnabled(true);
                    searchBar.setVisibility(View.VISIBLE);

                    break;
                }
                case (2): {
                    resetTabColours();
                    mFavourites.setTextColor(getResources().getColor(R.color.colorPrimary));
                    mCreateEvent.setEnabled(true);
                    mCreateEvent.setVisibility(View.VISIBLE);

                    searchButton.setEnabled(false);
                    searchButton.setVisibility(View.GONE);
                    searchBar.setEnabled(true);
                    searchBar.setVisibility(View.VISIBLE);
                    break;
                }
            }

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("SAVE STATE", "YES");
        outState.putInt("tracker", tracker);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        tracker = savedInstanceState.getInt("tracker");

    }

    @Override
    public void onDestroy() {
        Log.i("DESTROYED", "YES");
        Log.i("BACK STACK COUNT", Integer.toString(getFragmentManager().getBackStackEntryCount()));
        super.onDestroy();
        Log.i("FINISHING?", Boolean.toString(this.isFinishing()));
    }

    /**
     * TODO THIS COULD POSSIBLY BE REFACTORED ??
     */

    public class InfoWindow implements GoogleMap.InfoWindowAdapter {

        private View v;

        InfoWindow() {
            v = getLayoutInflater().inflate(R.layout.custom_info_window, null, true);
        }

        @Override
        public View getInfoWindow(Marker marker) {

            ListEvent e = events_list.get(marker.getId());

            TextView Title = ((TextView) v.findViewById(R.id.title));
            TextView Time = ((TextView) v.findViewById(time));
            TextView Address = ((TextView) v.findViewById(R.id.address));
            IconTextView fav = ((IconTextView) v.findViewById(R.id.map_fav));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.FILL_PARENT);

            if (e != null) {

                lp.gravity = Gravity.LEFT;

                Title.setLayoutParams(lp);

                String title = e.getName();
                if (title.length() < 30)
                    Title.setText(title);

                else
                    Title.setText(title.substring(0, 30) + "...");

                String time = DataUtil.getTime(e);
                assert Time != null;
                Time.setText(time);
                Time.setTextSize(12);


                assert Address != null;
                Address.setText(pinAddress);

                //this is a shoddy solution as we are relying on user names being unique
                if (FirebaseAuth.getInstance().getCurrentUser()
                        .getDisplayName().equals(e.getHost())) {

                    fav.setText("{fa-user}");
                } else {

                    ArrayList favs = getFavouriteIdsList();

                    if (favs.contains(e.getEventId()))
                        fav.setText("{fa-star}");
                    else
                        fav.setText("{fa-star-o}");
                }

                return v;

            } else {

                lp.gravity = Gravity.CENTER;

                Title.setLayoutParams(lp);
                Title.setText("You!");
                Address.setText(pinAddress);
                fav.setText("");
                Time.setTextSize(0);

                return v;
            }
        }


        @Override
        public View getInfoContents(Marker marker) {

            return null;

        }

    }


    /**
     * TODO COULD THIS BE MOVED TO AUTHBASEACTIVITY
     */
    public void signOut() {

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

    /**
     * resets all the tabs to the unselected color
     */
    private void resetTabColours() {
        for (IconTextView itv : mTabs) {
            itv.setTextColor(getResources().getColor(R.color.dark_gray));
        }
    }

    /**
     * Getter method for that returns the events list
     *
     * @return list of nearby events
     */
    public ArrayList<ListEvent> getEventList() {
        ArrayList<ListEvent> allEvents = new ArrayList<>(events);
        return allEvents;
    }


    public HashMap<String, ListEvent> getEventsMap() {
        return eventsMap;
    }

    public ArrayList<ListEvent> searchEvents(String query) {
        return SearchUtil.searchEvents(query, getEventList());
    }

    public ArrayList<String> getFavouriteIdsList() {
        return favouriteIds;
    }

    public ArrayList<String> getHostIdsList() {
        return hostingIds;
    }
}

