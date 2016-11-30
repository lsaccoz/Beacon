package com.bcn.beacon.beacon.Utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by omar on 27/11/16.
 */

public class LocationUtil {
    public void setPinLocation(double lat, double lng, Marker arg, GoogleMap mMap, boolean zoom, Context context){
        arg.hideInfoWindow();
        arg.setTitle(getLocationName(lat, lng, context));
        arg.setPosition(new LatLng(lat,lng));

        if(zoom) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(lat, lng)), 15));
        }else{
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
        }

        arg.showInfoWindow();
    }

    public String getLocationName(double lat, double lng, Context context){
        String name = "";
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat,lng, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses != null) {
            if (!addresses.isEmpty()) {
                name = addresses.get(0).getAddressLine(0);
            } else {
                name = "";
            }
        }

        return name;
    }
}
