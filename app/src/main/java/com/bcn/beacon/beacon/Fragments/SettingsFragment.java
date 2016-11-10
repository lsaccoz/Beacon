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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import com.bcn.beacon.beacon.Activities.MainActivity;
import com.bcn.beacon.beacon.CustomViews.SearchRangePreference;
import com.bcn.beacon.beacon.R;

/**
 * Created by neema on 2016-10-28.
 */
public class SettingsFragment extends PreferenceFragment {

    private static SettingsFragment settingsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.settings_fragment);

        Preference signOut = findPreference(getActivity().getString(R.string.sign_out));

        signOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((MainActivity) getActivity()).signOut();
                return true;
            }
        });

    }

    public static SettingsFragment getInstance() {

        if (settingsFragment != null) {
            return settingsFragment;
        } else{
            settingsFragment = new SettingsFragment();
            return settingsFragment;
        }
    }

}