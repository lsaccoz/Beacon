package com.bcn.beacon.beacon.Adapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bcn.beacon.beacon.Activities.MainActivity;
import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.R;

import java.util.List;

/**
 * Created by neema on 2016-11-09.
 * <p/>
 * This class is the adapter for Image items in the event page activity
 * <p/>
 * We are using a recycler view to implement a horizontal image scroller
 */
public class EventImageAdapter extends RecyclerView.Adapter<EventImageAdapter.MyViewHolder> {

    //List of Drawables that populate the images in the list
    private List<Drawable> mImageList;

    private boolean hasDummy = true;

    //static view holder class that contains the views that we will be changing in each list item
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImage;

        public MyViewHolder(View view) {
            super(view);

            //only taking the image view for now so assert it's type
            assert (view instanceof ImageView);
            mImage = (ImageView) view.findViewById(R.id.event_image);


        }
    }

    //constructor for setting the image list
    public EventImageAdapter(List<Drawable> mImageList) {
        this.mImageList = mImageList;
    }

    public void addPhoto(Drawable photo) {
        if (hasDummy) {
            mImageList.clear();
            hasDummy = false;
        }
        mImageList.add(photo);
        notifyDataSetChanged();
    }

    //inflate the root view and create a new viewholder to contain the image view
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_image, parent, false);

        return new MyViewHolder(itemView);
    }

    //set this image's drawable
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mImage.setImageDrawable(mImageList.get(position));

    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }


}
