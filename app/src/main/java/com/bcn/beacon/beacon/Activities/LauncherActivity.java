package com.bcn.beacon.beacon.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.*;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
