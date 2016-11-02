package com.bcn.beacon.beacon.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;

import com.bcn.beacon.beacon.CustomViews.SearchRangePreference;
import com.bcn.beacon.beacon.R;

/**
 * Created by neema on 2016-10-28.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.settings_fragment);

        // For certain preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.

        //bindPreferenceSummaryToValue(rangePreference);

    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */

    //TODO currently this method is unused, remove if not used in future

//    private void bindPreferenceSummaryToValue(Preference preference) {
//        // Set the listener to watch for value changes.
//        preference.setOnPreferenceChangeListener(this);
//
//        // Trigger the listener immediately with the preference's
//        // current value.
//        if(preference instanceof CheckBoxPreference){
//            onPreferenceChange(preference,
//                    PreferenceManager
//                            .getDefaultSharedPreferences(preference.getContext())
//                            .getBoolean(preference.getKey(), true));
//
//            //case where the preference is the search range preference
//        }else if(preference instanceof SearchRangePreference){
//            onPreferenceChange(preference,
//                    PreferenceManager
//                            .getDefaultSharedPreferences(preference.getContext())
//                            .getInt(preference.getKey(), 40));
//
//            //default preference case
//        } else{
//            onPreferenceChange(preference,
//                    PreferenceManager
//                            .getDefaultSharedPreferences(preference.getContext())
//                            .getString(preference.getKey(), ""));
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        // For list preferences, look up the correct display value in
        // the preference's 'entries' list (since they have separate labels/values).



//        String stringValue = value.toString();
//        preference.setSummary(stringValue);

        //TODO if there are preferences for which we need to bind their preference summary
        //TODO to their value, do so in this block


        return true;

    }
}
