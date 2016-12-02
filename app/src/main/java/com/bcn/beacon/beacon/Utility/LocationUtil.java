package com.bcn.beacon.beacon.Utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.bcn.beacon.beacon.Activities.MainActivity;
import com.bcn.beacon.beacon.Data.Models.ListEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.cos;

/**
 * Created by omar on 27/11/16.
 */

public class LocationUtil {

    // constant for permission id
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 816;

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

    /**
     * Method to filter events based on the user's set search radius
     *
     *
     * @param eventList
     * @param distanceLimit
     */
//    public static void filterEventsByDistance(ArrayList<ListEvent> eventList, int distanceLimit){
//
//        //find the first index for which an events distance is greater than the distanceLimit and
//        //remove the sublist for all array items afterwards since the list is already sorted
//        for(int i = 0; i < eventList.size(); i++){
//            ListEvent event = eventList.get(i);
//            if(event.distance > distanceLimit){
//                eventList.removeAll(eventList.subList(i, eventList.size()));
//                break;
//            }
//        }
//
//
//    }

    /**
     * Java implementation of the Haversine formula for calculating the distance between two locations.
     * Taken from http://stackoverflow.com/questions/120283
     * /how-can-i-measure-distance-and-create-a-bounding-box-based-on-two-latitudelongi/123305#123305
     *
     *
     * @param userLat  - latitude of the user's location
     * @param userLng  - longitude of the user's location
     * @param eventLat - latitude of the event's location
     * @param eventLng - longitude of the event's location
     * @return dist - distance between the two locations
     */
    public static double distFrom(double userLat, double userLng, double eventLat,
                                   double eventLng) {
        double earthRadius = 6371.0; // kilometers (or 3958.75 miles)
        double dLat = Math.toRadians(eventLat - userLat);
        double dLng = Math.toRadians(eventLng - userLng);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * cos(Math.toRadians(userLat)) * cos(Math.toRadians(eventLat));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        // for rounding to 3 decimal places
        dist = Math.floor(1000 * dist + 0.5) / 1000;

        return dist; // in kilometers
    }

    /**
     * Check for GPS permission
     *
     * @return true if user has allowed access to location, false otherwise
     */
    public static boolean checkGPSPermission(Context applicationContext) {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = applicationContext.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Gets the location of the user
     *
     * //TODO THIS CAN BE REFACTORED
     */
    public static Location getUserLocation(Context applicationContext) {

        LocationManager lm = (LocationManager) applicationContext.getSystemService(applicationContext.LOCATION_SERVICE);

        try {
            ActivityCompat.requestPermissions((MainActivity) applicationContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        }catch(Exception e){
            e.printStackTrace();
        }

        if (checkGPSPermission(applicationContext)) {
            List<String> providers = lm.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = lm.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            if (bestLocation != null) {
                return bestLocation;
                //Log.i("PERMISSION:", "ALLOWED");
            }
        }

        return null;
    }
}
