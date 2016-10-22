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
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapFragment mMapFragment;
    LinearLayout mCustomActionBar;
    List<IconTextView> mTabs;
    TextView mTitle;

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

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.events_view, mMapFragment);
        fragmentTransaction.commit();


        mMapFragment.getMapAsync(this);





//        ActionBar actionBar = getActionBar();
//        actionBar.setCustomView()


    }

    private void resetTabColours(){
        for(IconTextView itv : mTabs){
            itv.setBackgroundResource(R.color.otherTabColor);
        }
    }

    @Override
    public void onMapReady(GoogleMap map){
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
