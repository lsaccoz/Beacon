package com.bcn.beacon.beacon.Utility;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcn.beacon.beacon.R;

/**
 * Created by neema on 2016-11-08.
 */
public class UI_Util {

    /**
     * Utility method to set the color of the status bar to the app's
     * primary color theme
     *
     * @param window
     * @param color
     */
    @TargetApi(21)
    public static void setStatusBarColor(Window window, int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(color);
        }

    }

    /**
     * Utility method to hide the list view divider on certain screens
     *
     * @param listView listView object, could be null
     */
    public static void hideListViewDivider(@Nullable ListView listView) {
        if (listView != null) {
            listView.setDivider(null);
        }
    }


    /**
     * Utility method to truncate the length of a textview and add ellipsize
     * to the end
     */
    public static void truncateText(@Nullable TextView textView, int maxLength) {
        if (textView != null) {
            if (textView.length() > maxLength) {
                String text = (String) textView.getText();
                String newText = text.substring(0, 31) + "...";
                textView.setText(newText);
            }
        }
    }

    // Set title divider color and text color
    public static void setDialogStyle(Dialog dialog, Context context) {
        int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));


    }

    // A method to find height of the status bar
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // A method to fix the toolbar layout due to status bar changes
    public static void styleToolBar(Toolbar toolbar, Context context, View layout){

        if(layout instanceof LinearLayout) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
            params.setMargins(0, getStatusBarHeight(context), 0, 0);
            toolbar.setLayoutParams(params);

        }else if(layout instanceof RelativeLayout){

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
            params.setMargins(0, getStatusBarHeight(context), 0, 0);
            toolbar.setLayoutParams(params);
        }

    }
}
