package com.bcn.beacon.beacon.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.R;

import java.util.HashMap;

public class EventPageActivity extends AppCompatActivity {

    private int from;
    private String eventId;
    private HashMap<String, Event> eventsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        from = intent.getIntExtra("from", 0);
        eventId = intent.getStringExtra("eventId");
        eventsMap = MainActivity.getEventsMap();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, eventsMap.get(eventId).getName().toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        MainActivity.setEventPageClickedFrom(from);
        super.onBackPressed();
    }
}
