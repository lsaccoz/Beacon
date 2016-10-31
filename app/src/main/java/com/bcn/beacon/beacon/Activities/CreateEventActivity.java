package com.bcn.beacon.beacon.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.net.Uri;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;


import com.bcn.beacon.beacon.Data.Models.Date;
import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.Data.Models.Location;
import com.bcn.beacon.beacon.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joanzapata.iconify.widget.IconTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static android.support.design.R.styleable.View;

public class CreateEventActivity extends AppCompatActivity {

    EditText eTime;
    EditText eDate;
    Date date = new Date();
    EditText eName;
    EditText eDescription;
    Location location = new Location();
    ImageButton eAddImage;
    ImageView eImage;
    Uri picUri;
    Button upload;

    final int PIC_CROP = 2;
    final int PIC_SAVE = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Create Event");

        eTime = (EditText) findViewById(R.id.event_time);
        eDate = (EditText) findViewById(R.id.event_date);
        eName = (EditText) findViewById(R.id.input_name);
        eDescription = (EditText) findViewById(R.id.input_description);
        eAddImage = (ImageButton) findViewById(R.id.addImageButton);
        //eImage = (ImageView) findViewById(R.id.eventImage);
        upload = (Button) findViewById(R.id.upload);


        eDate.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        selectedmonth = selectedmonth + 1;
                        eDate.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                        date.setDay(selectedday);
                        date.setMonth(selectedmonth);
                        date.setYear(selectedyear);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");

                mDatePicker.show();
            }
        });

        eTime.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        eTime.setText(selectedHour + ":" + selectedMinute);
                        date.setHour(selectedHour);
                        date.setMinute(selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        eAddImage.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        upload.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PIC_SAVE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PIC_SAVE) {
                picUri = data.getData();
                performCrop(picUri);
            } else if (requestCode == PIC_CROP) {
                //Bundle extras = data.getExtras();
                //Bitmap thePic = extras.getParcelable("data");
                Bitmap thePic = null;
                try {
                    thePic = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), picUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                eAddImage.setScaleType(ImageView.ScaleType.FIT_XY);
                //eAddImage.setScaleY(400);
                eAddImage.setImageBitmap(thePic);
            }
        }
    }

    private void performCrop(Uri picUri) {
        //call the standard crop action intent (the user device may not support it)
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        //indicate image type and Uri
        cropIntent.setDataAndType(picUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 1080);
        cropIntent.putExtra("outputY", 1080);
        //retrieve data on return
        //cropIntent.putExtra("scale", true);
        //cropIntent.putExtra("return-data", true);
        //start the activity - we handle returning in onActivityResult

        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        startActivityForResult(cropIntent, PIC_CROP);
    }

    private void upload() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle("Yo");
//        builder.setMessage("you pressed a button");
//
//        builder.create().show();

        location.setLatitude(50.50);
        location.setLongitude(90.90);

        Event toUpload = new Event();

        toUpload.setName(eName.getText().toString());
        toUpload.setDescription(eDescription.getText().toString());
        toUpload.setDate(date);
        toUpload.setLocation(location);
        toUpload.upload();
    }
}
