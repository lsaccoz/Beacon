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
        AdapterView.OnItemSelectedListener, View.OnClickListener {

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
            //The key argument here must match that used in the other activity
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
                // TODO: Get info about the selected place.
                //Log.i(TAG, "Place: " + place.getName());
                currentLat = place.getLatLng().latitude;
                currentLng = place.getLatLng().longitude;
                currentName = place.getName().toString();

                marker.hideInfoWindow();

                marker.setPosition(place.getLatLng());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((place.getLatLng()), 15));

                marker.setTitle(currentName);
                marker.showInfoWindow();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.reset_location_fab) :{
                currentLat = userLat;
                currentLng = userLng;

                setLocationAndPin(userLat, userLng, marker, true);
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
                    // TODO Auto-generated method stub
                    Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                }

                @SuppressWarnings("unchecked")
                @Override
                public void onMarkerDragEnd(Marker arg0) {
                    // TODO Auto-generated method stub
                    Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);

                    setLocationAndPin(arg0.getPosition().latitude, arg0.getPosition().longitude, arg0, false);
                }

                @Override
                public void onMarkerDrag(Marker arg0) {
                    // TODO Auto-generated method stub
                    Log.i("System out", "onMarkerDrag...");
                }
            });


            marker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                    .position(new LatLng(currentLat, currentLng)));

            marker.setDraggable(true);
            setLocationAndPin(currentLat, currentLng, marker, true);

        }
    }

    void setLocationAndPin(double lat, double lng, Marker arg, boolean zoom){
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat,lng, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        arg.hideInfoWindow();

        if(!addresses.isEmpty()) {
            currentName = addresses.get(0).getAddressLine(0);
            arg.setTitle(currentName);
        }else{
            currentName = "";
            arg.setTitle("");
        }

        arg.setPosition(new LatLng(lat,lng));
        if(zoom) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(lat, lng)), 15));
        }else{
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
        }
        currentLat = lat;
        currentLng = lng;
        arg.showInfoWindow();
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
