package com.bcn.beacon.beacon.Utility;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by andytertzakian on 2016-11-28.
 */

public class DatabaseUtil {

    private static final DatabaseUtil mDatabaseUtil = new DatabaseUtil();

    //Constructor
    private DatabaseUtil(){

    }

    public static DatabaseUtil getInstance() {
        return mDatabaseUtil;
    }

    private static DatabaseReference getDatabaseReference(String ref){
        DatabaseReference databaseReference = null;
        return databaseReference;
    }

    private static boolean setValue(String ref){
        return false;
    }

    private int getIntValue(String ref){
        return -1;
    }

    private double getDoubleValue(String ref){
        return -1;
    }

    private String getStringValue(String ref){
        return null;
    }
}
