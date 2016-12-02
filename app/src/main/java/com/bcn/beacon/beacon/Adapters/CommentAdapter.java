package com.bcn.beacon.beacon.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcn.beacon.beacon.Activities.EventPageActivity;
import com.bcn.beacon.beacon.Data.Models.Comment;
import com.bcn.beacon.beacon.Data.Models.ListEvent;
import com.bcn.beacon.beacon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by epekel on 2016-11-20.
 */
public class CommentAdapter extends ArrayAdapter<Comment> {
    private Context mContext;
    private ArrayList<Comment> mComments;

    // view lookup cache for faster item loading
    private static class ViewHolder {
        TextView user;
        TextView comment;
        TextView time;
        ImageView userPic;
        IconTextView edit;
        IconTextView delete;
    }

    public CommentAdapter(Context context, int resourceId, ArrayList<Comment> comments){
        super(context, resourceId, comments);
        this.mContext = context;
        this.mComments = comments;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the event for this position
        final Comment comment = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
            viewHolder.user = (TextView) convertView.findViewById(R.id.user);
            viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.userPic = (ImageView) convertView.findViewById(R.id.userPic);
            viewHolder.edit = (IconTextView) convertView.findViewById(R.id.edit);
            viewHolder.delete = (IconTextView) convertView.findViewById(R.id.delete);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        }
        else {
            // view recycled, retrieve view holder object
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String timeSpan = DateUtils.getRelativeTimeSpanString(comment.getDate(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS).toString();

        // Populate data via viewHolder
        viewHolder.user.setText(comment.getUserName() + " says:");
        viewHolder.comment.setText(comment.getText());
        viewHolder.time.setText(timeSpan);
        Picasso.with(getContext()) //Context
                .load(comment.getImageUrl()) //URL/FILE
                .into(viewHolder.userPic);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (comment.getUserId().equals(userId)) {
            viewHolder.edit.setVisibility(View.VISIBLE);
            viewHolder.edit.setEnabled(true);
            viewHolder.delete.setVisibility(View.VISIBLE);
            viewHolder.delete.setEnabled(true);
        }
        final IconTextView mEdit = viewHolder.edit;
        final IconTextView mDelete = viewHolder.delete;

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EventPageActivity) mContext).setCurrentCommentPos(mComments.indexOf(comment));
                ((EventPageActivity) mContext).editComment(comment);
            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EventPageActivity) mContext).deleteComment(comment, mComments.indexOf(comment));
                mEdit.setVisibility(View.GONE);
                mEdit.setEnabled(false);
                mDelete.setVisibility(View.GONE);
                mDelete.setEnabled(false);
            }
        });

        // Return the completed view
        return convertView;
    }


}