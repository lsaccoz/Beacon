package com.bcn.beacon.beacon.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Range;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import com.bcn.beacon.beacon.Activities.MainActivity;
import com.bcn.beacon.beacon.CustomViews.SearchRangePreference;
import com.bcn.beacon.beacon.R;
import com.bcn.beacon.beacon.Utility.UI_Util;

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

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                        getActivity(), R.style.DialogTheme));

                //set message, title and listeners for new alert dialog
                builder.setMessage(getString(R.string.sign_out_confirmation))
                        .setTitle(getString(R.string.sign_out))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) getActivity()).signOut();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();

                //dialog will go away if user presses outside the dialog window
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                UI_Util.setDialogStyle(dialog, getActivity());



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