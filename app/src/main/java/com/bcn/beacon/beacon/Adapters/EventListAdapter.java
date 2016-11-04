package com.bcn.beacon.beacon.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.R;

import java.util.ArrayList;

/**
 * Created by neema on 2016-10-16.
 * Implemented by epekel starting from 2016-10-28.
 */
public class EventListAdapter extends ArrayAdapter<Event> {

    public EventListAdapter(Context context, int resourceId,  ArrayList<Event> events){
        super(context, resourceId, events);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the event for this position
        Event event = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView host = (TextView) convertView.findViewById(R.id.host);
        TextView distance = (TextView) convertView.findViewById(R.id.distance);
        TextView start = (TextView) convertView.findViewById(R.id.start);
        // Populate events into the list view using the Event's getter methods
        title.setText(event.getName());
        host.setText(event.getHostId());
        distance.setText(Double.toString(Math.floor(event.getDistance())));
        start.setText(event.getTimeStart_Id());

        // Return the completed view
        return convertView;
    }
}
