package com.bcn.beacon.beacon.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.net.Uri;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import com.android.camera.CropImageIntentBuilder;
import com.bcn.beacon.beacon.BeaconApplication;
import com.bcn.beacon.beacon.Data.Models.Date;
import com.bcn.beacon.beacon.Data.Models.Event;
import com.bcn.beacon.beacon.Data.Models.Location;
import com.bcn.beacon.beacon.R;
import com.bcn.beacon.beacon.Utility.ImageUtil;
import com.bcn.beacon.beacon.Utility.LocationUtil;
import com.bcn.beacon.beacon.Utility.UI_Util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateEventActivity extends AuthBaseActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    protected LocationUtil localUtil = new LocationUtil();

    protected EditText eTime;
    protected EditText eDate;
    protected Date date = new Date();
    protected EditText eName;
    protected EditText eDescription;
    protected EditText eAddress;
    protected RelativeLayout mContentView;

    private ArrayList photos = new ArrayList<Bitmap>();

    private int from;


    protected Location location = new Location();
    protected ImageButton eAddImage;
    protected Uri picUri;
    protected FloatingActionButton myFab;
    protected Spinner categorySpinner;
    protected ImageUtil imgUtil = new ImageUtil();

    private File tempfile;

    protected double currentLat, currentLng;
    protected double userLat, userLng;

    final int LOCATION_SELECTED = 3;
    final int PIC_CROP = 2;
    final int REQUEST_IMAGE_CAPTURE = 1;
    final int PIC_SAVE = 0;
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        final ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        Window window = this.getWindow();
        //set the status bar color if the API version is high enough
        //UI_Util.setStatusBarColor(window, Color.TRANSPARENT);

        actionBar.setTitle("Create Event");

        mContentView = (RelativeLayout) findViewById(R.id.create_event);
        //set on global layout listener so that we can add appropriate padding to the top of the content view
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mContentView.setPadding(0, UI_Util.getStatusBarHeight(getApplicationContext()) + actionBar.getHeight(), 0, 0);
            }
        });

        eTime = (EditText) findViewById(R.id.event_time);
        eDate = (EditText) findViewById(R.id.event_date);
        eName = (EditText) findViewById(R.id.input_name);
        eDescription = (EditText) findViewById(R.id.input_description);
        eAddImage = (ImageButton) findViewById(R.id.addImageButton);
        categorySpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        myFab = (FloatingActionButton) findViewById(R.id.fab);
        eAddress = (EditText) findViewById(R.id.input_address);

        eDate.setOnClickListener(this);
        eTime.setOnClickListener(this);
        eAddImage.setOnClickListener(this);
        eAddress.setOnClickListener(this);
        categorySpinner.setOnItemSelectedListener(this);
        myFab.setOnClickListener(this);

        initialzieDateandTime();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userLat = extras.getDouble("userlat");
            userLng = extras.getDouble("userlng");
            currentLat = userLat;
            currentLng = userLng;
            from = extras.getInt("from");
            location.setLatitude(userLat);
            location.setLongitude(userLng);

            //since editEventActivity calls this method we don't want the activity to hang if the user
            //is offline so do nothing here if so
            if(BeaconApplication.isNetworkAvailable(this)) {
                eAddress.setText(localUtil.getLocationName(userLat, userLng, getApplicationContext()));
                location.setAddress(eAddress.getText().toString());
            }

            //The key argument here must match that used in the other activity
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.event_time): {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateEventActivity.this, R.style.PickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        boolean isPM = (selectedHour >= 12);
                        date.setHour(selectedHour);
                        date.setMinute(selectedMinute);
                        eTime.setText(String.format(Locale.US, "%02d:%02d %s",
                                (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute,
                                isPM ? "PM" : "AM"));
                    }
                }, hour, minute, false);//No to 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                break;
            }
            case (R.id.event_date): {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(CreateEventActivity.this, R.style.PickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        selectedmonth = selectedmonth + 1;
                        eDate.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                        date.setDay(selectedday);
                        date.setMonth(selectedmonth);
                        date.setYear(selectedyear);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");
                mDatePicker.getDatePicker().setMaxDate(mcurrentDate.getTimeInMillis() + DateUtils.YEAR_IN_MILLIS);
                mDatePicker.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
                mDatePicker.show();
                break;
            }

            case (R.id.addImageButton): {
                selectImage();
                break;
            }
            case (R.id.fab): {
                if (eName.getText().toString().trim().equals("")) {
                    eName.setError("Your event needs a name!");
                } else {
                    upload();
                }

                break;
            }
            case (R.id.input_address): {
                if(BeaconApplication.isNetworkAvailable(this)) {
                    Intent intent = new Intent(this, SelectLocationActivity.class);
                    intent.putExtra("userlat", userLat);
                    intent.putExtra("userlng", userLng);
                    intent.putExtra("curlat", currentLat);
                    intent.putExtra("curlng", currentLng);
                    startActivityForResult(intent, LOCATION_SELECTED);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            new ContextThemeWrapper(this, R.style.DialogTheme));

                    builder.setMessage(getString(R.string.no_internet_feature))
                            .setTitle(getString(R.string.no_internet_title))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog signInDialog = builder.create();
                    signInDialog.setCanceledOnTouchOutside(true);

                    signInDialog.show();

                    UI_Util.setDialogStyle(signInDialog, this);

                }

                break;
            }
        }

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkExternalMemoryPermissions();
                    }
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    tempfile = getTempFile();
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempfile));
                        takePictureIntent.putExtra("return-data", true);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else if (items[item].equals("Choose from Library")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkExternalMemoryPermissions();
                    }
                    Intent saveImageIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(saveImageIntent, PIC_SAVE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.create().show();
    }

    private File getTempFile() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
            try {
                file.createNewFile();
            } catch (IOException e) {
            }

            return file;
        } else {

            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        File croppedImageFile = new File(getFilesDir(), "test.jpg");

        if (resultCode == RESULT_OK) {
            if (requestCode == PIC_SAVE) {

                Uri croppedImage = Uri.fromFile(croppedImageFile);

                CropImageIntentBuilder cropImage = new CropImageIntentBuilder(3, 2, 540, 360, croppedImage);
                cropImage.setOutlineColor(0xFF03A9F4);
                cropImage.setSourceImage(data.getData());

                startActivityForResult(cropImage.getIntent(getApplicationContext()), PIC_CROP);
            } else if (requestCode == PIC_CROP) {
                eAddImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Bitmap photo = BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath());
                photos.add(photo);
                eAddImage.setImageBitmap(photo);

            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Uri croppedImage = Uri.fromFile(croppedImageFile);

                try {
                    imgUtil.handleSamplingAndRotationBitmap(this, Uri.fromFile(croppedImageFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                CropImageIntentBuilder cropImage = new CropImageIntentBuilder(3, 2, 540, 360, croppedImage);

                cropImage.setOutlineColor(0xFF03A9F4);
                cropImage.setSourceImage(Uri.fromFile(tempfile));
                startActivityForResult(cropImage.getIntent(getApplicationContext()), PIC_CROP);

            } else if (requestCode == LOCATION_SELECTED) {
                if (data.getExtras() != null) {
                    currentLat = data.getExtras().getDouble("lat");
                    currentLng = data.getExtras().getDouble("lng");
                    String name = data.getExtras().getString("name");
                    eAddress.setText(name);
                    location.setLatitude(currentLat);
                    location.setLongitude(currentLng);
                    location.setAddress(eAddress.getText().toString());
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkExternalMemoryPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            return;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        //parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    protected void upload() {

        if (location == null) {
            location.setLatitude(49.2765);
            location.setLongitude(-123.2177);
            location.setAddress("");
        }
        Event toUpload = new Event();


        toUpload.setName(eName.getText().toString());
        toUpload.setDescription(eDescription.getText().toString());
        toUpload.setDate(date);
        toUpload.setLocation(location);
        toUpload.addPhotos(photos);
        toUpload.upload();

        // for temporary fix on back pressed
        MainActivity.setEventPageClickedFrom(from);

        //display toast to user to confirm that an event was uploaded
        Toast toast = Toast.makeText(this,
                getString(R.string.upload_event_confirmation),
                Toast.LENGTH_SHORT);

        toast.show();
        kill_activity();

    }

    private void kill_activity() {
        finish();
    }


    private void initialzieDateandTime() {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);

        int mYear = mCurrentTime.get(Calendar.YEAR);
        int mMonth = mCurrentTime.get(Calendar.MONTH);
        int mDay = mCurrentTime.get(Calendar.DAY_OF_MONTH);

        boolean isPM = (hour >= 12);

        mMonth = mMonth + 1;
        eDate.setText("" + mDay + "/" + mMonth + "/" + mYear);
        date.setDay(mDay);
        date.setMonth(mMonth);
        date.setYear(mYear);

        date.setHour(hour);
        date.setMinute(minute);

        eTime.setText(String.format(Locale.US, "%02d:%02d %s",
                (hour == 12 || hour == 0) ? 12 : hour % 12, minute,
                isPM ? "PM" : "AM"));
    }

    @Override
    public void onBackPressed() {
        // for temporary fix on back pressed
        MainActivity.setEventPageClickedFrom(from);
        super.onBackPressed();
    }
}
