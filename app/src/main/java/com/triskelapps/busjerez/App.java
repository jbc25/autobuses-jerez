package com.triskelapps.busjerez;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.google.android.libraries.places.api.Places;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.triskelapps.busjerez.database.MyDatabase;
import com.triskelapps.busjerez.model.BusLine;
import com.triskelapps.busjerez.model.db.BusLineVisible;
import com.triskelapps.busjerez.util.CountlyUtil;
import com.triskelapps.busjerez.util.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class App extends Application {

    private static final String TAG = "App";

    public static final int BUS_LINES_COUNT = 18;

    public static final String URL_GOOGLE_PLAY_APP = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    public static final String URL_DIRECT_GOOGLE_PLAY_APP = "market://details?id=" + BuildConfig.APPLICATION_ID;

    public static final String URL_YOUTUBE_VIDEO = "https://youtu.be/S87pQYvdvRQ";

    public static final String PREFIX = BuildConfig.APPLICATION_ID + ".";

    public static final String PREF_FIRST_TIME_LAUNCH = PREFIX + "pref_first_time_launch";
    public static final String PREF_FIRST_TIME_DATA_POPULATED = PREFIX + "pref_first_time_data_populated";
    public static final String PREF_BUS_DATA = PREFIX + "pref_bus_data";
    public static final String PREF_BUS_DATA_SAVED_VERSION = PREFIX + "pref_bus_data_saved_version";
    public static final String PREF_BANNER_IGNORED_ID = PREFIX + "pref_banner_ignored_id_";


    private static final String FILE_BUS_LINES_DATA = "bus_lines_data.json";

    private static MyDatabase db;
    //    public static final String SHARED_TOKEN = PREFIX + "shared_token";

    public static final String DB_NAME = "app_db.sqlite";


    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        CountlyUtil.configureCountly(this);

        String apiKey = BuildConfig.MAPS_API_KEY;
        Places.initialize(getApplicationContext(), apiKey);

        db = Room.databaseBuilder(getApplicationContext(),
                MyDatabase.class, DB_NAME)
//                .addMigrations(MyDatabase.MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();


        boolean firstTimeLaunch = getDB().busLineVisibleDao().getAll().size() == 0;
        getPrefs(this).edit().putBoolean(PREF_FIRST_TIME_LAUNCH, firstTimeLaunch).commit();

        populateDataFirstTime();

//        NotificationHelper.with(this).initializeOreoChannelsNotification();


    }



    private void populateDataFirstTime() {

        Log.i(TAG, "populateDataFirstTime: 1");

        if (getPrefs(this).getString(PREF_BUS_DATA, null) == null) {
            String jsonDataStrInitial = Util.getStringFromAssets(this, FILE_BUS_LINES_DATA);
            getPrefs(this).edit().putString(PREF_BUS_DATA, jsonDataStrInitial).commit();
        }

        Log.i(TAG, "populateDataFirstTime: 2");

        if (getDB().busLineVisibleDao().getAll().size() == 0) {
            populateBusLineVisibleTable();
        }
    }

    private void populateBusLineVisibleTable() {
        for (int lineId = 1; lineId <= BUS_LINES_COUNT; lineId++) {
            db.busLineVisibleDao().insert(new BusLineVisible(lineId, true));
        }
    }

    public static MyDatabase getDB() {
        return db;
    }

    public static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static List<BusLine> getBusLinesData(Context context) {

        String jsonDataStr = getPrefs(context).getString(PREF_BUS_DATA, null);
        Type listType = new TypeToken<ArrayList<BusLine>>() {
        }.getType();
        List<BusLine> busLines = new Gson().fromJson(jsonDataStr, listType);

//        for (BusLine busLine : busLines) {
//            int colorId = getColorForLine(context, busLine.getId());
//            busLine.setColor(ContextCompat.getColor(context, colorId));
//
//            BusLineVisible busLineVisible = db.busLineVisibleDao().getBusLineVisible(busLine.getId());
//            busLine.setVisible(busLineVisible.isVisible());
//        }

        return busLines;
    }

    public static int getColorForLine(Context context, int lineId) {
        int colorId = context.getResources().getIdentifier("line" + lineId, "color", context.getPackageName());
        return colorId;
    }

}
