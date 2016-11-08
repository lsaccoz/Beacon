package com.bcn.beacon.beacon.CustomViews;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bcn.beacon.beacon.R;

/**
 * Created by neema on 2016-11-02.
 */
public class NotificationsPreference extends Preference implements View.OnClickListener {

    CheckBox mCheckbox;
    TextView mSummary;

    public NotificationsPreference(Context context, AttributeSet attrSet){
        super(context, attrSet, 0);

    }

    @Override
    protected View onCreateView(ViewGroup parent){
        super.onCreateView(parent);

        //inflate custom view for this preference
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.notifications_pref_layout, parent, false);

        SharedPreferences preferences = getSharedPreferences();
        boolean checked = preferences.getBoolean(getContext().getString(R.string.pref_notifications_key), true);

        mSummary = (TextView) view.findViewById(R.id.summary);

        mCheckbox = (CheckBox) view.findViewById(R.id.checkbox);
        mCheckbox.setChecked(checked);


        mCheckbox.setOnClickListener(this);

        return view;

    }


    @Override
    public void onClick(View view){

        boolean checked = mCheckbox.isChecked();
        if (checked){
            getEditor().putBoolean(getContext().getString(R.string.pref_notifications_key), true ).commit();
        }else{
            getEditor().putBoolean(getContext().getString(R.string.pref_notifications_key), false).commit();
        }

    }

//    private void setSummary(boolean checked){
//        if(checked){
//            mSummary.setText(getContext().getString(R.string.pref_notifications_summary_on));
//        }else{
//            mSummary.setText(getContext().getString(R.string.pref_notifications_summary_off));
//        }
//    }
}
