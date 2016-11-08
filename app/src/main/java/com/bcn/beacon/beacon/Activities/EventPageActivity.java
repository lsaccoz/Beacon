package com.bcn.beacon.beacon.Activities;


import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bcn.beacon.beacon.Data.Models.Date;
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

import java.util.Calendar;

public class EventPageActivity extends AppCompatActivity {

    private Event mEvent;
    private Date mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO THE FOLLOWING CODE IS SIMPLY TEST CODE TO CHECK THE XML LAYOUT, NEEDS TO BE REPLACED WITH CODE TO RETRIEVE FIELDS OF REAL EVENT OBJECT

        mDate = new Date();
        Calendar mcurrentDate = Calendar.getInstance();
        int defaultYear = mcurrentDate.get(Calendar.YEAR);
        int defaultMonth = mcurrentDate.get(Calendar.MONTH);
        int defaultDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        int defaultHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        int defaultMinute = mcurrentDate.get(Calendar.MINUTE);

        mDate.setYear(defaultYear);
        mDate.setMonth(defaultMonth);
        mDate.setDay(defaultDay);
        mDate.setHour(defaultHour);
        mDate.setMinute(defaultMinute);

        mEvent = new Event("blabla", "Cool Party", "blabla", 5, 100, null, "This will be lit!");
        mEvent.setDate(mDate);

        setTitle(mEvent.getName());

        //TODO END OF TEST BLOCK



       // GetEvent();
    }

    private void GetEvent() {

//        String ID = getIntent().getStringExtra("Event");
//
//        String eventId = ID;
//
//        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events/" + eventId);
//
//        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                event = dataSnapshot.getValue(Event.class);
//                populate();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                //TODO add error catch
//            }
//
//        });
    }

    private void populate() {
        setTitle(mEvent.getName());
        TextView description = (TextView) findViewById(R.id.description);
        assert description != null;
        description.setText(mEvent.getDescription());

    }
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
=======
    @Override
>>>>>>> Staging
    public void onBackPressed() {
        MainActivity.setEventPageClickedFrom(from);
        super.onBackPressed();
    }
}*/
