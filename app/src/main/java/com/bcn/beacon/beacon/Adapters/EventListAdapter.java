package com.bcn.beacon.beacon.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bcn.beacon.beacon.Data.Models.Event;

import java.util.List;

/**
 * Created by neema on 2016-10-16.
 */
public class EventListAdapter extends ArrayAdapter<Event> {

    public EventListAdapter(Context context, int resourceId,  List<Event> events){
        super(context, resourceId, events);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //TODO need to implement this method to return an inflated view for each event in "events"

        //return this if not null, no need to inflate a new View for events that already have one
        return convertView;
    }
}
