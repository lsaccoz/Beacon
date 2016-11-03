package com.bcn.beacon.beacon.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
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
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private boolean mMapInitialized = false;

    MapFragment mMapFragment;
    SettingsFragment mSettingsFragment;
    LinearLayout mCustomActionBar;
    List<IconTextView> mTabs;
    TextView mTitle;
    MainActivity mContext;

    IconTextView mList;
    IconTextView mWorld;
    IconTextView mNavigation;
    IconTextView mFavourites;
    IconTextView mSettings;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set default values for preferences if they haven't been modified yet
        PreferenceManager.setDefaultValues(this, R.xml.settings_fragment, false);

        mContext = this;

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = LayoutInflater.from(this);

        mCustomActionBar = (LinearLayout) inflater.inflate(R.layout.custom_action_bar, null);
        actionBar.setCustomView(mCustomActionBar);
        Toolbar parent = (Toolbar) mCustomActionBar.getParent();//first get parent toolbar of current action bar
        parent.setContentInsetsAbsolute(0, 0);// set padding programmatically to 0dp

        ViewGroup.LayoutParams lp = mCustomActionBar.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mCustomActionBar.setLayoutParams(lp);

        mTitle = (TextView) mCustomActionBar.findViewById(R.id.my_title);
        mTitle.setTypeface(Typeface.MONOSPACE);

        mList = (IconTextView) findViewById(R.id.list);
        mWorld = (IconTextView) findViewById(R.id.world);
        mNavigation = (IconTextView) findViewById(R.id.settings);
        mFavourites = (IconTextView) findViewById(R.id.favourites);
        mSettings = (IconTextView) findViewById(R.id.settings_tab);

        mList.setOnClickListener(this);
        mWorld.setOnClickListener(this);
        mNavigation.setOnClickListener(this);
        mFavourites.setOnClickListener(this);
        mSettings.setOnClickListener(this);

        mMapFragment = MapFragment.newInstance();
        //final LinearLayout create_event = (LinearLayout) findViewById(R.id.create_event);

        mTabs = new ArrayList<>();

        mTabs.add(mList);
        mTabs.add(mWorld);
        mTabs.add(mFavourites);
        mTabs.add(mSettings);

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

        if (!mMapInitialized) {

            FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.events_view, mMapFragment, getString(R.string.map_fragment));

            fragmentTransaction.commit();


            mMapFragment.getMapAsync(this);
            mMapInitialized = true;
        }


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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.list): {
                resetTabColours();
                mList.setBackgroundResource(R.color.currentTabColor);
                break;
            }
            case (R.id.world): {
                resetTabColours();
                mWorld.setBackgroundResource(R.color.currentTabColor);

                //check if visible fragment is an instance of Map fragment already, if so do nothing
                Fragment currentFragment = getFragmentManager().findFragmentByTag(getString(R.string.map_fragment));
                if(!(currentFragment instanceof MapFragment)) {
//                    mMapFragment = MapFragment.newInstance();

                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.events_view, mMapFragment, getString(R.string.map_fragment));

                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();

                    mMapFragment.getMapAsync(mContext);
                }


                break;
            }
            case (R.id.settings):{
                signOut();
                break;
            }
            case (R.id.favourites):{
                resetTabColours();
                mFavourites.setBackgroundResource(R.color.currentTabColor);
                break;
            }
            case (R.id.settings_tab):{

                resetTabColours();
                mSettings.setBackgroundResource(R.color.currentTabColor);

                //check if visible fragment is an instance of settings fragment already, if so do nothing
                Fragment currentFragment = getFragmentManager().findFragmentByTag(getString(R.string.settings_fragment));

                if(!(currentFragment instanceof SettingsFragment)) {
                    mSettingsFragment = SettingsFragment.getInstance();
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.events_view, mSettingsFragment, getString(R.string.settings_fragment));

                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();
                }
                break;
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
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        Intent i = new Intent(MainActivity.this, SignInActivity.class);
                        MainActivity.this.startActivity(i);
                        MainActivity.this.finish();
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.beacon_icon))
                    .position(new LatLng(49.2606, -123.2460))
                    .title(mAuth.getCurrentUser().getDisplayName()));
            map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
            marker.showInfoWindow();
        } else {
            Marker marker = map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.beacon_icon))
                    .position(new LatLng(49.2606, -123.2460))
                    .title("BECON!"));
            map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()), 250, null);
            marker.showInfoWindow();
        }
    }

    private void resetTabColours() {
        for (IconTextView itv : mTabs) {
            itv.setBackgroundResource(R.color.otherTabColor);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
}
