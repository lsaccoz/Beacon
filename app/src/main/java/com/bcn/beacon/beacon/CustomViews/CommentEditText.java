package com.bcn.beacon.beacon.CustomViews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.bcn.beacon.beacon.Activities.EventPageActivity;
import com.bcn.beacon.beacon.R;

/**
 * Created by Emre on 18/11/2016.
 */

public class CommentEditText extends EditText {
    private EventPageActivity activity;

    // Constructors
    public CommentEditText(Context context) {
        super(context);
        this.activity = (EventPageActivity) context;
    }
    public CommentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.activity = (EventPageActivity) context;
    }
    public CommentEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.activity = (EventPageActivity) context;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // TODO: We might add an "are you sure" prompt for discarding changes for comment
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {
            activity.showDiscardAlert();
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

}
