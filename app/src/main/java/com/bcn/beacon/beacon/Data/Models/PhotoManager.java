package com.bcn.beacon.beacon.Data.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bcn.beacon.beacon.Adapters.EventImageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class PhotoManager {
    private static PhotoManager instance = null;
    private StorageReference storageRef;
    private static HashMap photos = new HashMap<String, Drawable>();
    private static HashMap thumbnails = new HashMap<String, Bitmap>();

    private static final long MEGABYTE = 1024 * 1024;

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
        if (photos.size() == 0)
            return;

        ByteArrayOutputStream stream;
        byte[] data;
        int i = 0;

        stream = new ByteArrayOutputStream();
        Bitmap square = Bitmap.createBitmap(photos.get(0), 90, 0, 360, 360);
        Bitmap thumb = Bitmap.createScaledBitmap(square, 90, 90, true);
        thumb.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        data = stream.toByteArray();

        StorageReference smallRef = storageRef.child(eventId + "/thumb.jpg");
        smallRef.putBytes(data);

        for (Bitmap full : photos) {
            stream = new ByteArrayOutputStream();
            full.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            data = stream.toByteArray();

            StorageReference fullRef = storageRef.child(eventId + "/photos/" + i++ + ".jpg");
            fullRef.putBytes(data);
        }

    }

    public Bitmap getThumb(String eventId) {
        if (thumbnails.containsKey(eventId))
            return (Bitmap) thumbnails.get(eventId);
        else
            return null;
    }

    public void downloadThumbs(final String eventId) {
        if (!thumbnails.containsKey(eventId)) {
            StorageReference smallRef = storageRef.child(eventId + "/thumb.jpg");

            smallRef.getBytes(MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap downloaded = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    thumbnails.put(eventId, downloaded);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //leave default image
                }
            });
        }
    }

    public void setEventPhotos(String eventId, final EventImageAdapter adapter) {
        setSinglePhoto(eventId, adapter, 0);
    }

    public void setSinglePhoto(final String eventId, final EventImageAdapter adapter, final int i) {
        final String key = eventId + String.valueOf(i);
        if (photos.containsKey(key)) {
            adapter.addPhoto((Drawable) photos.get(key));
            setSinglePhoto(eventId, adapter, i + 1);
            return;
        }

        StorageReference eventPhotosRef = storageRef.child(eventId + "/photos/" + i + ".jpg");

        eventPhotosRef.getBytes(MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap downloaded = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Drawable photo = new BitmapDrawable(downloaded);
                adapter.addPhoto(photo);
                photos.put(key, photo);
                setSinglePhoto(eventId, adapter, i + 1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //do nothing when no photos left
            }
        });
    }
}
