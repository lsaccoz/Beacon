package com.bcn.beacon.beacon.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.R;

public class EditEventActivity extends CreateEventActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userLat = extras.getDouble("lat");
            userLng = extras.getDouble("lng");
            currentLat = userLat;
            currentLng = userLng;
            location.setLatitude(userLat);
            location.setLongitude(userLng);
            eAddress.setText(localUtil.getLocationName(userLat, userLng, getApplicationContext()));

            eName.setText(extras.getString("name"));
            eDescription.setText(extras.getString("description"));
            eTime.setText(extras.getString("time"));
            eDate.setText(extras.getString("date"));


            //The key argument here must match that used in the other activity
        }
    }

    @Override
    protected void upload(){
        Intent data = new Intent();

        data.putExtra("lat", currentLat);
        data.putExtra("lng", currentLng);
        data.putExtra("name", eName.getText().toString());
        data.putExtra("description", eDescription.getText().toString());
        data.putExtra("minute", date.getMinute());
        data.putExtra("hour", date.getHour());
        data.putExtra("day", date.getDay());
        data.putExtra("month", date.getMonth());
        data.putExtra("year", date.getYear());
        data.putExtra("address", eAddress.getText().toString());
        data.putExtra("time", eTime.getText().toString());


        setResult(RESULT_OK, data);

        //show confirmation that the event was updated
        Toast toast = Toast.makeText(this,
                getString(R.string.event_update_confirmation),
                Toast.LENGTH_SHORT);

        toast.show();

        finish();
        return;
    }


}
