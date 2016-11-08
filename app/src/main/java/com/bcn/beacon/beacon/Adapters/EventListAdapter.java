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
    // view lookup cache for faster item loading
    private static class ViewHolder {
        TextView title;
        TextView host;
        TextView distance;
        TextView start;
    }

    public EventListAdapter(Context context, int resourceId,  ArrayList<Event> events){
        super(context, resourceId, events);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the event for this position
        Event event = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.host = (TextView) convertView.findViewById(R.id.host);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.start = (TextView) convertView.findViewById(R.id.start);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        }
        else {
            // view recycled, retrieve view holder object
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate data via viewHolder
        viewHolder.title.setText(event.getName());
        viewHolder.host.setText(event.getUserName());
        viewHolder.distance.setText(Double.toString(event.getDistance()));
        viewHolder.start.setText(event.getTimeStart_Id());

        /*// Lookup view for data population (commented out for now)
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView host = (TextView) convertView.findViewById(R.id.host);
        TextView distance = (TextView) convertView.findViewById(R.id.distance);
        TextView start = (TextView) convertView.findViewById(R.id.start);
        // Populate events into the list view using the Event's getter methods
        title.setText(event.getName());
        host.setText(event.getHostId());
        distance.setText(Double.toString(Math.floor(event.getDistance())));
        start.setText(event.getTimeStart_Id());*/

        // Return the completed view
        return convertView;
    }
}
