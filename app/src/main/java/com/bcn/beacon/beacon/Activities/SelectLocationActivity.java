package com.bcn.beacon.beacon.Activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;


import com.bcn.beacon.beacon.R;
import com.bcn.beacon.beacon.Utility.LocationUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class SelectLocationActivity extends AuthBaseActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
      View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private ActionBar actionBar;
    private FloatingActionButton resetLocationButton;
    private FloatingActionButton setLocationButton;


    private double userLat;
    private double userLng;
    private double currentLat;
    double currentLng;
    private String currentName;
    private Marker marker;

    private LocationUtil localUtil = new LocationUtil();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userLat = extras.getDouble("userlat");
            userLng = extras.getDouble("userlng");
            currentLng = extras.getDouble("curlng");
            currentLat = extras.getDouble("curlat");
        }

        resetLocationButton = (FloatingActionButton) findViewById(R.id.reset_location_fab);
        setLocationButton = (FloatingActionButton) findViewById(R.id.set_location_fab);

        resetLocationButton.setOnClickListener(this);
        setLocationButton.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //Log.i(TAG, "Place: " + place.getName());
                currentLat = place.getLatLng().latitude;
                currentLng = place.getLatLng().longitude;
                currentName = place.getName().toString();

                localUtil.setPinLocation(currentLat, currentLng, marker, mMap, true, getApplicationContext());
                marker.hideInfoWindow();
                marker.setTitle(currentName);
                marker.showInfoWindow();
            }

            @Override
            public void onError(Status status) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.reset_location_fab) :{

                localUtil.setPinLocation(userLat, userLng, marker, mMap, true, this);
                currentLat = userLat;
                currentLng = userLng;
                currentName = localUtil.getLocationName(currentLat, currentLng, this);

                break;
            }
            case(R.id.set_location_fab) :{
                Intent data = new Intent();

                data.putExtra("lat", currentLat);
                data.putExtra("lng", currentLng);
                data.putExtra("name", currentName);
                setResult(RESULT_OK, data);

                finish();

                break;
            }
        }

    }

    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();

        mAuth.addAuthStateListener(mAuthListener);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.locale_select_map);
        mapFragment.getMapAsync(this);
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

    public void initMap(){
        if(mMap != null){

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker arg0) {
                    Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                }

                @SuppressWarnings("unchecked")
                @Override
                public void onMarkerDragEnd(Marker arg0) {
                    // TODO Auto-generated method stub
                    Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);

                    currentLat = arg0.getPosition().latitude;
                    currentLng = arg0.getPosition().longitude;
                    localUtil.setPinLocation(currentLat, currentLng, arg0, mMap, false, getApplicationContext());
                    currentName = localUtil.getLocationName(currentLat, currentLng, getApplicationContext());
                }

                @Override
                public void onMarkerDrag(Marker arg0) {
                    Log.i("System out", "onMarkerDrag...");
                }
            });


            marker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                    .position(new LatLng(currentLat, currentLng)));

            marker.setDraggable(true);
            localUtil.setPinLocation(currentLat, currentLng, marker, mMap, true, this);
            currentName = localUtil.getLocationName(currentLat, currentLng, getApplicationContext());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMap();
    }
}
