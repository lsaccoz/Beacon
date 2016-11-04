package com.bcn.beacon.beacon;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseUser;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


/**
 * Created by neema on 2016-10-09.
 */
public class BeaconState extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        Iconify.
                with(new FontAwesomeModule());

        Firebase.setAndroidContext(this);
    }
}
