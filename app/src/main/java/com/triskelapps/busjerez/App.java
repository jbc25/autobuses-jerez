package com.triskelapps.busjerez;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.triskelapps.busjerez.database.MyDatabase;


public class App extends Application {

    private static final String TAG = "App";

    public static final String URL_GOOGLE_PLAY_APP = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    public static final String URL_DIRECT_GOOGLE_PLAY_APP = "market://details?id=" + BuildConfig.APPLICATION_ID;

    public static final String PREFIX = BuildConfig.APPLICATION_ID + ".";


    private static MyDatabase db;
    //    public static final String SHARED_TOKEN = PREFIX + "shared_token";

    public static final String DB_NAME = "app_db.sqlite";

    @Override
    public void onCreate() {
        super.onCreate();

//        db = Room.databaseBuilder(getApplicationContext(),
//                MyDatabase.class, DB_NAME)
////                .addMigrations(MyDatabase.MIGRATION_1_2)
////                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .build();

//        NotificationHelper.with(this).initializeOreoChannelsNotification();

    }
    public static MyDatabase getDB() {
        return db;
    }

    public static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


}
