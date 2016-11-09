package com.bcn.beacon.beacon.Activities;


import android.annotation.TargetApi;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcn.beacon.beacon.Data.Models.Date;
import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.R;
import com.bcn.beacon.beacon.Utility.DataUtil;
import com.bcn.beacon.beacon.Utility.UI_Util;
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
import com.joanzapata.iconify.widget.IconButton;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.Calendar;
import java.util.Locale;

public class EventPageActivity extends AppCompatActivity {

    private Event mEvent;
    private Date mDate;

    private TextView mTitle;
    private TextView mDescription;
    private IconTextView mFavourite;
    private RecyclerView mImageScroller;
    private ImageView mCoverPhoto;
    private TextView mStartTime;
    private TextView mStartMonth;
    private TextView mStartDay;
    private TextView mAddress;

    private boolean favourited = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        Window window = this.getWindow();
        //set the status bar color if the API version is high enough
        UI_Util.setStatusBarColor(window, this.getResources().getColor(R.color.colorPrimary));

        //retrieve all the views from the view hierarchy
        mTitle = (TextView) findViewById(R.id.event_title);
        mDescription = (TextView) findViewById(R.id.event_description);
        mFavourite = (IconTextView) findViewById(R.id.favourite_button);
        mImageScroller = (RecyclerView) findViewById(R.id.image_scroller);
        mCoverPhoto = (ImageView) findViewById(R.id.cover_photo);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mStartDay = (TextView) findViewById(R.id.start_day);
        mStartMonth = (TextView) findViewById(R.id.start_month);
        mAddress = (TextView) findViewById(R.id.address);

        //change look of favourite icon when user presses it
        //filled in means favourited, empty means not favourited
        mFavourite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!favourited){
                    ((IconTextView) view).setText("{fa-star}");
                    favourited = true;
//                    ((IconTextView) view).setBackgroundColor(getBaseContext()
//                            .getResources().getColor(R.color.colorPrimary));
                }else{
                    favourited = false;
                    ((IconTextView) view).setText("{fa-star-o}");
//                    ((IconTextView) view).setBackgroundColor(getBaseContext()
//                            .getResources().getColor(R.color.colorPrimary));
                }
            }

        });


        //TODO THE FOLLOWING CODE IS SIMPLY TEST CODE TO CHECK THE XML LAYOUT, NEEDS TO BE REPLACED WITH CODE TO RETRIEVE FIELDS OF REAL EVENT OBJECT

        //create a fake date object and set all the fields to the current time
        mDate = new Date();
        Calendar mcurrentDate = Calendar.getInstance();
        int defaultWeekDay = mcurrentDate.get(Calendar.DAY_OF_WEEK);
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

        //create dummy event to populate the event page UI
        mEvent = new Event("blabla", "Cool Party", "blabla", 5, 100, null, "Come out for the Donald Trump Election Celebration Party!");
        mEvent.setDate(mDate);

        //set the event title
        mTitle.setText(mEvent.getName());

        //set the description
        mDescription.setText(mEvent.getDescription());

        int hour = mDate.getHour();
        int minute = mDate.getMinute();
        int day = mDate.getDay();
        int month = mDate.getMonth();
        int year = mDate.getYear();
        boolean isPM = false;

        if (hour >= 12){
            isPM = true;
        }

        mStartDay.setText("" + mEvent.getDate().getDay());
        mStartMonth.setText("" + DataUtil.convertMonthToString(mEvent.getDate().getMonth()));
        mStartTime.setText(DataUtil.convertDayToString(defaultWeekDay) + " " + String.format(Locale.US, "%02d:%02d %s",
                (hour == 12 || minute == 0) ? 12 : hour % 12, minute,
                 isPM ? "PM" : "AM"));

        mAddress.setText("666 Trump Ave.");

        //TODO move date formatting code into a utility class

//        //create a formatted date string for the start time
//        String formattedDateStringStart = "Start Time: " + day + "/" + month + "/" + year + " " + String.format(Locale.US, "%02d:%02d %s",
//                (hour == 12 || minute == 0) ? 12 : hour % 12, minute,
//                isPM ? "PM" : "AM");
//
//        //create a formatted date string for the end time
//        String formattedDateStringEnd = "End Time: " + day + "/" + month + "/" + year +  " " + String.format(Locale.US, "%02d:%02d %s",
//                (hour == 12 || minute == 0) ? 12 : hour % 12, minute,
//                isPM ? "PM" : "AM");
//
//        // populate the start time and end time with the same date string for now
//        mStartTime.setText(formattedDateStringStart);
//        mEndTime.setText(formattedDateStringEnd );



        //setTitle(mEvent.getName());

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
