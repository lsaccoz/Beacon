package com.bcn.beacon.beacon.CustomViews;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bcn.beacon.beacon.R;

/**
 * Created by neema on 2016-11-01.
 */
public class SearchRangePreference extends Preference {

    //the seekbar that will be used to update the actual preference value
    SeekBar mRange;

    //the textview displaying the range in km
    TextView mRangeText;

    //default constructor
    public SearchRangePreference(Context context, AttributeSet attrSet){
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

        //set an on change listener so that when the user changes the position of the slider
        //the preference value is updated
        mRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getEditor().putInt(getContext().getString(R.string.pref_range_key), progress).commit();
                mRangeText.setText(progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }


}
