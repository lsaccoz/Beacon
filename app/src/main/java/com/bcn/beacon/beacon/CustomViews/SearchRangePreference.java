package com.bcn.beacon.beacon.CustomViews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bcn.beacon.beacon.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by neema on 2016-11-01.
 */
public class SearchRangePreference extends Preference implements SeekBar.OnSeekBarChangeListener {

    //the seekbar that will be used to update the actual preference value
    SeekBar mRange;

    //the textview displaying the range in km
    TextView mRangeText;

    //default constructor
    public SearchRangePreference(Context context, AttributeSet attrSet) {
        super(context, attrSet, 0);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);

        //inflate custom view for this preference
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.search_range_pref_layout, parent, false);

        //find seekbar
        mRange = (SeekBar) view.findViewById(R.id.range);

        //find textview
        mRangeText = (TextView) view.findViewById(R.id.range_text);

        //initialize the progress value to what is stored in the SharedPreferences file
        SharedPreferences preferences = getSharedPreferences();
        int progressVal = preferences.getInt(getContext().getString(R.string.pref_range_key), 40);
        mRange.setProgress(progressVal);
        mRangeText.setText(progressVal + " km");

        if (Build.VERSION.SDK_INT >= 16) {
            customizeThumb(mRange, Color.parseColor("#BA68C8"));
        }
        customizeProgressBar(mRange, Color.rgb(142, 36, 170));

        //set an on change listener so that when the user changes the position of the slider
        //the preference value is updated
        mRange.setOnSeekBarChangeListener(this);


        return view;
    }


    private void customizeProgressBar(SeekBar seekBar, int newColor) {

        seekBar.getProgressDrawable().setColorFilter
                (new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));

    }

    @TargetApi(16)
    private void customizeThumb(SeekBar seekBar, int newColor) {

        seekBar.getThumb().setColorFilter(
                new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));

//        Rect oldBounds = seekBar.getThumb().getBounds();

//        seekBar.getThumb().setBounds(new Rect(oldBounds.left + 20, oldBounds.top + 20,
//                oldBounds.right + 20, oldBounds.bottom + 20));


    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


        //if user picks a value that is not a multiple of 5, scale up or down
        if (seekBar.getProgress() % 5 != 0) {

            if ((seekBar.getProgress() % 5 < 3)) {
                seekBar.setProgress(seekBar.getProgress() - (seekBar.getProgress() % 5));
            } else {
                seekBar.setProgress(seekBar.getProgress() + (5 - seekBar.getProgress() % 5));
            }

        }

        //set minimum search radius to 1 km
        if (seekBar.getProgress() == 0) {
            seekBar.setProgress(1);
        }


            //set the preference value once the user has picked a range value
            getEditor().putInt(getContext().getString(R.string.pref_range_key), seekBar.getProgress()).commit();
            //manually call this preferences on preference change listener
            OnPreferenceChangeListener listener = this.getOnPreferenceChangeListener();
            if (listener != null) {
                listener.onPreferenceChange(this, null);
            }


            mRangeText.setText(seekBar.getProgress() + " km");


            //display a toast to notify user that their search range was updated
            Toast toast = Toast.makeText(getContext(),
                    getContext().getString(R.string.updated_pref_range),
                    Toast.LENGTH_SHORT);

            toast.show();

        }

    }
