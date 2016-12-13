package com.bcn.beacon.beacon.CustomViews;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by neema on 2016-12-12.
 */
public class CustomCardView extends CardView {

    //is active if showing search widget, otherwise false
    private boolean isActive = false;

    public CustomCardView(Context context, AttributeSet attrSet){
        super(context, attrSet );
    }


    //override children on touch events
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        return !isActive;
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }

}
