package com.bcn.beacon.beacon.CustomViews;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bcn.beacon.beacon.R;

/**
 * Created by neema on 2016-11-02.
 */
public class NotificationsPreference extends Preference {

    public NotificationsPreference(Context context, AttributeSet attrSet){
        super(context, attrSet, 0);

    }

    @Override
    protected View onCreateView(ViewGroup parent){
        super.onCreateView(parent);

        //inflate custom view for this preference
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.notifications_pref_layout, parent, false);

        return view;

    }
}
