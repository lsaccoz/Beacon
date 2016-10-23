package com.bcn.beacon.beacon.Activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;


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
    LinearLayout mCustomActionBar;
    List<IconTextView> mTabs;
    TextView mTitle;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = LayoutInflater.from(this);

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

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTabColours();
                list.setBackgroundResource(R.color.currentTabColor);

            }
        });

        world.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                resetTabColours();
                world.setBackgroundResource(R.color.currentTabColor);
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
    }

    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.events_view, mMapFragment);
        fragmentTransaction.commit();


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
