package com.bcn.beacon.beacon.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.widget.IconTextView;

public class EventPageActivity extends AppCompatActivity {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GetEvent();

    }

    private void GetEvent() {

        String ID = getIntent().getStringExtra("Event");

        String eventId = ID;

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events/" + eventId);

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                populate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO add error catch
            }

        });
    }

    private void populate() {
        setTitle(event.getName());
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(event.getDescription());

    }

   /* private void on_directions_click(Button Directions) {

        double Lat = event.getLocation().getLatitude();
        double Long = event.getLocation().getLongitude();
        String Latitude = String.valueOf(Lat);
        String Longitude = String.valueOf(Long);

        Uri intent_uri = Uri.parse("google.navigation:q=" + Latitude + Longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intent_uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }*/


    /*private void showAlert(String toShow){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Event Name")
                .setMessage(toShow)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }*/

    /*@Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            Log.i("NEW:", Integer.toString(getFragmentManager().getBackStackEntryCount()));
        }
        else {
            super.onBackPressed();
        }
    }*/
}
