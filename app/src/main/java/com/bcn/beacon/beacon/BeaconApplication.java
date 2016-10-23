package com.bcn.beacon.beacon;

import android.app.Application;

import com.bcn.beacon.beacon.Data.Models.DaoMaster;
import com.bcn.beacon.beacon.Data.Models.DaoSession;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.greenrobot.greendao.database.Database;

/**
 * Created by neema on 2016-10-09.
 */
public class BeaconApplication extends Application {

    public static Database db;
    public static DaoSession daoSession;

    @Override
    public void onCreate(){
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "Beacon-db");
        db = helper.getWritableDb();

        Iconify.
                with(new FontAwesomeModule());
    }


    public static DaoSession getDaoSession(){
        if(daoSession != null){
            return daoSession;
        }else{
            daoSession = new DaoMaster(db).newSession();
            return daoSession;
        }

    }
}
