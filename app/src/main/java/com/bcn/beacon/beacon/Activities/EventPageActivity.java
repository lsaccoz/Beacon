package com.bcn.beacon.beacon.Activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.Window;
import android.widget.TextView;

import com.bcn.beacon.beacon.Adapters.CommentAdapter;
import com.bcn.beacon.beacon.CustomViews.CommentEditText;
import com.bcn.beacon.beacon.Data.Models.Comment;
import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.Adapters.EventImageAdapter;
import com.bcn.beacon.beacon.Data.Models.Date;
import com.bcn.beacon.beacon.Data.Models.Location;
import com.bcn.beacon.beacon.Data.Models.PhotoManager;
import com.bcn.beacon.beacon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import com.bcn.beacon.beacon.Utility.DataUtil;
import com.bcn.beacon.beacon.Utility.UI_Util;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import java.util.ArrayList;

public class EventPageActivity extends AppCompatActivity {

    private static final int COMMENT_CHARACTER_LIMIT = 2;
    private Event mEvent;
    private String mEventId;
    private Context mContext;
    private CommentAdapter mAdapter;
    private ArrayList<Comment> commentsList = new ArrayList<>();

    //the root view of the layout
    private View mContentView;

    private ArrayList<Drawable> mImageDrawables;
    private TextView mTitle;
    private TextView mDescription;
    private IconTextView mFavourite;
    private RecyclerView mImageScroller;
    private TextView mStartTime;
    private TextView mStartMonth;
    private TextView mStartDay;
    private TextView mAddress;
    private TextView mTags;
    private ListView mCommentsList;
    private IconTextView mCommentButton;
    private IconTextView mPostComment;
    private CommentEditText mWriteComment;

    private boolean mFavourited = false;
    private boolean commentTab = false;
    private int mAnimDuration;

    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        Intent intent = getIntent();
        from = intent.getIntExtra("from", -1);

        Window window = this.getWindow();
        //set the status bar color if the API version is high enough
        UI_Util.setStatusBarColor(window, this.getResources().getColor(R.color.colorPrimary));

        //initialize array of image drawables, we will retrieve this from the event
        mImageDrawables = new ArrayList<>();

        //set the context for use in callback methods
        mContext = this;
        //this.mWriteComment = new CommentEditText(mContext);

        // Retrieve and cache the system's default "medium" animation time.
        mAnimDuration = getResources().getInteger
                (android.R.integer.config_shortAnimTime);


        //get the event id
        mEventId = getIntent().getStringExtra("Event");

        //get boolean value for whether event is favourited or not
        mFavourited = getIntent().getBooleanExtra("Favourited", false);

        System.out.println(mEventId);

        // check if event is favourited and set favourited accordingly
        setFavourited();

        //fetch the event from the firebase database
        getEvent(mEventId);


        //retrieve all the views from the view hierarchy
        mContentView = findViewById(R.id.event_page_root);

        mTitle = (TextView) findViewById(R.id.event_title);
        mDescription = (TextView) findViewById(R.id.event_description);
        mFavourite = (IconTextView) findViewById(R.id.favourite_button);
        mImageScroller = (RecyclerView) findViewById(R.id.image_scroller);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mStartDay = (TextView) findViewById(R.id.start_day);
        mStartMonth = (TextView) findViewById(R.id.start_month);
        mAddress = (TextView) findViewById(R.id.address);
        mTags = (TextView) findViewById(R.id.tags);
        mCommentsList = (ListView) findViewById(R.id.comments_list);
        mCommentButton = (IconTextView) findViewById(R.id.comment_button);
        mPostComment = (IconTextView) findViewById(R.id.post_comment);
        mWriteComment = (CommentEditText) findViewById(R.id.write_comment);

        // input manager for showing keyboard immediately
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // set empty view if there are no favourites
        mCommentsList.setEmptyView(findViewById(R.id.empty));

        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!commentTab) {
                    commentTab = true;
                    mCommentButton.setText("{fa-comment}");
                    mPostComment.setVisibility(View.VISIBLE);
                    mPostComment.setEnabled(true);
                    mWriteComment.setVisibility(View.VISIBLE);
                    mWriteComment.setEnabled(true);
                    mWriteComment.requestFocus();
                    imm.showSoftInput(mWriteComment, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    showDiscardAlert();
                    imm.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }
        });

        mPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                long time = c.getTimeInMillis();
                Comment comment = new Comment();
                String text = mWriteComment.getText().toString();
                if (text.length() >= COMMENT_CHARACTER_LIMIT) {
                    comment.setText(text);
                    comment.setEventId(mEventId);
                    comment.setDate(time);
                    comment = comment.writeComment();
                    mWriteComment.setText("");
                    hideCommentTab();
                    imm.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Toast toast = Toast.makeText(mContext, "Comment posted", Toast.LENGTH_SHORT);
                    toast.show();
                    commentsList.add(0, comment);
                    mAdapter.notifyDataSetChanged();
                } else {
                    String alert = "You need to enter at least " + COMMENT_CHARACTER_LIMIT + " characters";
                    Toast toast = Toast.makeText(mContext, alert, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        initFavourite();

        //Add dummy image to the event page
        mImageDrawables.add(getResources().getDrawable(R.drawable.default_event_photo));

        //create adapter between image list and recycler view
        EventImageAdapter eventImageAdapter = new EventImageAdapter(mImageDrawables);
        PhotoManager.getInstance().setEventPhotos(mEventId, eventImageAdapter);

        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mImageScroller.setLayoutManager(horizontalLayoutManagaer);

        mImageScroller.setAdapter(eventImageAdapter);
    }

    // Method for hiding comment tab on back press from EditText
    public void hideCommentTab() {
        if (commentTab) {
            commentTab = false;
            mCommentButton.setText("{fa-comment-o}");
            mPostComment.setVisibility(View.GONE);
            mPostComment.setEnabled(false);
            mWriteComment.setVisibility(View.GONE);
            mWriteComment.setText("");
            mWriteComment.setEnabled(false);
            mWriteComment.clearFocus();
        }
    }

    public void showDiscardAlert() {
        if (mWriteComment.getText().toString().length() >= COMMENT_CHARACTER_LIMIT) {
            // show alert if the user entered more than required characters
            AlertDialog.Builder alert = new AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
            alert.setIcon(R.drawable.attention);
            alert.setTitle("DISCARD COMMENT?");
            alert.setMessage("Your changes will be discarded.");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                // check for android.view.WindowLeaked: exception!
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // hide the comment tab if yes
                    hideCommentTab();
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mWriteComment, InputMethodManager.SHOW_IMPLICIT);
                    dialog.dismiss();
                }
            });
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                // this listener is for if the user presses back button when the dialog is visible
                @Override
                public void onCancel(DialogInterface dialog) {
                    hideCommentTab();
                    dialog.dismiss();
                }
            });

            alert.show();
        } else {
            // if not, just hide the comment tab
            hideCommentTab();
        }
    }

    /**
     * This method fetches the Event data from the firebase database
     *
     * @param eventId The event Id as a string
     */
    private void getEvent(String eventId) {

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events/" + eventId);

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get the event
                mEvent = dataSnapshot.getValue(Event.class);

                if (!commentsList.isEmpty()) {
                    commentsList.clear();
                }
                // for comments
                for (DataSnapshot comment : dataSnapshot.child("comments").getChildren()) {
                    commentsList.add(comment.getValue(Comment.class));
                }
                Log.i("DATA:", "CHANGED");
                Collections.reverse(commentsList);

                mEvent.setComments(commentsList);

                //populate the views in the view hierarchy with actual event data
                populateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                //TODO add error catch
            }

        });
    }

    /**
     * Function to check if the event is favourited, and changes icon fill accordingly
     */
    public void setFavourited() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/"
                + FirebaseAuth.getInstance().getCurrentUser().getUid()
                + "/favourites/" + mEventId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // since we only set the value to false, this check is alright
                if (dataSnapshot.getValue() != null) {
                    mFavourited = true;
                    mFavourite.setText("{fa-star}");
                } else {
                    mFavourited = false;
                    mFavourite.setText("{fa-star-o}");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Function for adding the event to user's favourites
     *
     * @return true if successful, otherwise return false
     */

    private boolean addFavourite() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        users.child(userId).child("favourites").child(mEventId).setValue(true);
//        Toast.makeText(EventPageActivity.this, "Event added to favourites!", Toast.LENGTH_SHORT).show();

        return true;
    }

    /**
     * Function to remove this event from the user's favourites
     *
     * @return true if successful, otherwise return false
     */
    private boolean removeFavourite() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference("Users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        users.child(userId).child("favourites").child(mEventId).removeValue();

        return true;
    }

    @Override
    public void onBackPressed() {
        hideCommentTab();
        MainActivity.setEventPageClickedFrom(from);
        super.onBackPressed();
        //finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }*/
    }

    // for fixing the clicking favourites twice bug
    // TODO: this may be another way of fixing the clicking favourite button twice bug, by finish()ing activity on back press
    // is it better to finish() activity everytime for less work done by the activity, I don't know...
    // also, don't know if it will work, since onSaveInstance is usually called after onStart
    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("favourited", favourited);
        outState.putString("favText", mFavourite.getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        favourited = savedInstanceState.getBoolean("favourited");
        mFavourite.setText(savedInstanceState.getString("favText"));
    }*/

    private void populateUI() {

        //initially hide the layout
        mContentView.setVisibility(View.GONE);

        mDescription.setText(mEvent.getDescription());
        mTitle.setText(mEvent.getName());

        //fetch the date
        Date date = mEvent.getDate();

        //display the tags for this event
        mTags.setText(" #Party\n #Music\n #Fun");

        //retrieve and set the address
        mAddress.setText(mEvent.getLocation().getAddress());

        //set the time and day fields
        mStartDay.setText("" + date.getDay());
        mStartMonth.setText("" + DataUtil.convertMonthToString(date.getMonth()));
        mStartTime.setText(date.formatted());

        mAdapter = new CommentAdapter(mContext, 0, commentsList);
        if (commentsList != null) {
            mCommentsList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

        showUI();
    }


    /**
     * This class represents a helper task that will initialize the UI
     * <p>
     * 1. Set all the text fields to the value returned from the getEvent() method
     * <p>
     * 2. Start a background thread to retrieve the address from the location using
     * an API call
     * <p>
     * 3. Blur in the entire view hierarchy after the API call is complete
     * <p>
     * //TODO we should store address as a field variable in the event data model so that an API call is unneeded
     */
//    private class PopulateUITask extends AsyncTask<Void, Void, List<Address>> {
//
//        @Override
//        protected void onPreExecute() {
//

//
//        @Override
//        protected List<Address> doInBackground(Void... params) {
//
//            Geocoder coder = new Geocoder(mContext);
//            List<Address> addresses = new ArrayList<>();
//
//            //get Location
//            Location location = mEvent.getLocation();
//
//            //convert address to a readable string if possible
//            try {
//                addresses = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }
//
//            return addresses;
//
//        }
//
//        @Override
//        protected void onPostExecute(List<Address> addresses) {
//            Address address;
//            if (!addresses.isEmpty()) {
//                address = addresses.get(0);
//                mAddress.setText(address.getAddressLine(0));
//            }
//
//            showUI();
//
//        }
//    }

    /**
     * Method to blur in the UI layout using an animation
     * <p>
     * This is called after any relevant data is fetched from the network/local cache
     */
    private void showUI() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mContentView.setAlpha(0f);
        mContentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mContentView.animate()
                .alpha(1f)
                .setDuration(mAnimDuration)
                .setListener(null);
    }

    private void initFavourite() {

        if (mFavourited) {
            mFavourite.setText("{fa-star}");
        } else {
            mFavourite.setText("{fa-star-o}");
        }

        //change look of favourite icon when user presses it
        //filled in means favourited, empty means not favourited
        mFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int duration = Toast.LENGTH_SHORT;

                if (!mFavourited) {
                    ((IconTextView) view).setText("{fa-star}");
                    CharSequence text = getString(R.string.favourited);

                    addFavourite();

                    Toast toast = Toast.makeText(mContext, text, duration);
                    toast.show();
                    mFavourited = true;
//                    ((IconTextView) view).setBackgroundColor(getBaseContext()
//                            .getResources().getColor(R.color.colorPrimary));
                } else {
                    CharSequence text = getString(R.string.un_favourited);

                    removeFavourite();

                    Toast toast = Toast.makeText(mContext, text, duration);
                    toast.show();
                    mFavourited = false;
                    ((IconTextView) view).setText("{fa-star-o}");
//                    ((IconTextView) view).setBackgroundColor(getBaseContext()
//                            .getResources().getColor(R.color.colorPrimary));
                }
            }

        });
    }


//    private boolean initFavourite(){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference users = database.getReference("Users");
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        users.child(userId).child("favourites").child(mEventId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot)
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }
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

