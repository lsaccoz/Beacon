package com.bcn.beacon.beacon;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


/**
 * Created by neema on 2016-10-09.
 */
public class BeaconApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        Iconify.
                with(new FontAwesomeModule());

        //enable persistence so that data is stored locally
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Firebase.setAndroidContext(this);
    }



}
