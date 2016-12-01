package com.bcn.beacon.beacon.Data.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.bcn.beacon.beacon.Adapters.EventListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class PhotoManager {
    private static PhotoManager instance = null;
    private StorageReference storageRef;
    private static HashMap photos = new HashMap<String, Bitmap>();
    private static HashMap thumbs = new HashMap<String, Bitmap>();

    private static final long MEGABYTE = 1024*1024;

    private PhotoManager() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://beacon-b6fd8.appspot.com");
    }

    public static PhotoManager getInstance() {
        if (instance == null) {
            instance = new PhotoManager();
        }
        return instance;
    }

    public void upload(String eventId, ArrayList<Bitmap> photos) {
        if(photos.size() == 0)
            return;

        ByteArrayOutputStream stream;
        byte[] data;
        int i = 0;

        for(Bitmap full : photos) {
            stream = new ByteArrayOutputStream();
            full.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            data = stream.toByteArray();

            StorageReference fullRef = storageRef.child(eventId + "/photos/" + i++ + ".jpg");
            fullRef.putBytes(data);
        }

        stream = new ByteArrayOutputStream();
        Bitmap square = Bitmap.createBitmap(photos.get(0),180,0,720,720);
        Bitmap thumb = Bitmap.createScaledBitmap(square, 90, 90, true);
        thumb.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        data = stream.toByteArray();

        StorageReference smallRef = storageRef.child(eventId + "/thumb.jpg");
        smallRef.putBytes(data);
    }

    public Bitmap getThumb(String eventId){
        if(thumbs.containsKey(eventId))
            return (Bitmap) thumbs.get(eventId);
        else
            return null;
    }

    public void downloadThumbs(final String eventId) {
        if(!thumbs.containsKey(eventId)) {
            StorageReference smallRef = storageRef.child(eventId + "/thumb.jpg");

            smallRef.getBytes(MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap downloaded = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    thumbs.put(eventId, downloaded);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //leave default image
                }
            });
        }
    }
}
