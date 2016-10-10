package com.bcn.beacon.beacon.Activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcn.beacon.beacon.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.widget.IconTextView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapFragment mMapFragment;
    LinearLayout mCustomActionBar;

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


        final IconTextView home = (IconTextView) findViewById(R.id.home);
        final IconTextView world = (IconTextView) findViewById(R.id.world);
        final TextView create_event_text = (TextView) findViewById(R.id.create_event_text);

        create_event_text.setTypeface(Typeface.MONOSPACE);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.setBackgroundResource(R.color.currentTabColor);
                world.setBackgroundResource(R.color.otherTabColor);
            }
        });

        world.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                home.setBackgroundResource(R.color.otherTabColor);
                world.setBackgroundResource(R.color.currentTabColor);
            }
        });

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.events_view, mMapFragment);
        fragmentTransaction.commit();


        mMapFragment.getMapAsync(this);





//        ActionBar actionBar = getActionBar();
//        actionBar.setCustomView()


    }


    @Override
    public void onMapReady(GoogleMap map){
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
