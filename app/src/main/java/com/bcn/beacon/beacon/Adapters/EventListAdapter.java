package com.bcn.beacon.beacon.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcn.beacon.beacon.Activities.MainActivity;
import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.Data.Models.ListEvent;
import com.bcn.beacon.beacon.Data.Models.PhotoManager;
import com.bcn.beacon.beacon.R;
import com.bcn.beacon.beacon.Utility.UI_Util;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;

/**
 * Created by neema on 2016-10-16.
 * Implemented by epekel starting from 2016-10-28.
 */
public class EventListAdapter extends ArrayAdapter<ListEvent> {
    // view lookup cache for faster item loading

    ArrayList<String> mFavouritedEventIds;

    private static class ViewHolder {
        TextView title;
        TextView host;
        TextView distance;
        TextView start;
//        IconTextView favourite;
        ImageView thumb;
    }

    public EventListAdapter(Context context, int resourceId,  ArrayList<ListEvent> events, ArrayList<String> Ids){
        super(context, resourceId, events);
        mFavouritedEventIds = Ids;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the event for this position
        ListEvent event = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.host = (TextView) convertView.findViewById(R.id.host);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.start = (TextView) convertView.findViewById(R.id.start);
//            viewHolder.favourite = (IconTextView) convertView.findViewById(R.id.favourite);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        }
        else {
            // view recycled, retrieve view holder object
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.thumb = (ImageView) convertView.findViewById(R.id.eventPic);

//        if (mFavouritedEventIds.contains(event.getEventId()))
//            viewHolder.favourite.setText("{fa-star}");
//        else
//            viewHolder.favourite.setText("{fa-star-o}");
        // Populate data via viewHolder
        viewHolder.title.setText(event.getName());
        viewHolder.host.setText(event.getHost());

        //check if title too long, if so truncate it
        UI_Util.truncateText(viewHolder.title, 30);

        viewHolder.distance.setText(String.format("%.1f", event.distance) + " km");
        viewHolder.start.setText(event.getDate().formattedDate());

        Bitmap thumb = PhotoManager.getInstance().getThumb(event.getEventId());
        if(thumb != null) {
            viewHolder.thumb.setImageBitmap(thumb);
        }else{
            viewHolder.thumb.setImageDrawable(this.getContext().getDrawable(R.drawable.no_pic_icon));
        }

        // Return the completed view
        return convertView;
    }
}
