package com.triskelapps.busjerez;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

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
import com.triskelapps.busjerez.util.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.CountlyConfig;


public class App extends Application {

    private static final String TAG = "App";

    public static final int BUS_LINES_COUNT = 18;

    public static final String URL_GOOGLE_PLAY_APP = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    public static final String URL_DIRECT_GOOGLE_PLAY_APP = "market://details?id=" + BuildConfig.APPLICATION_ID;

    public static final String PREFIX = BuildConfig.APPLICATION_ID + ".";

    public static final String PREF_FIRST_TIME_DATA_POPULATED = PREFIX + "pref_first_time_data_populated";

    private static final String FILE_BUS_LINES_DATA = "bus_lines_data.json";

    private static MyDatabase db;
    //    public static final String SHARED_TOKEN = PREFIX + "shared_token";

    public static final String DB_NAME = "app_db.sqlite";


    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        CountlyConfig config = new CountlyConfig(this, getString(R.string.countly_app_key), getString(R.string.countly_server_url));
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            config.enableCrashReporting();
            config.setViewTracking(true);
            config.setAutoTrackingUseShortName(true);
        }
        Countly.sharedInstance().init(config);

        String apiKey = BuildConfig.MAPS_API_KEY;
        Places.initialize(getApplicationContext(), apiKey);

        db = Room.databaseBuilder(getApplicationContext(),
                MyDatabase.class, DB_NAME)
//                .addMigrations(MyDatabase.MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        populateDataFirstTime();

//        NotificationHelper.with(this).initializeOreoChannelsNotification();


    }

    private void populateDataFirstTime() {
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

        String jsonDataStr = Util.getStringFromAssets(context, FILE_BUS_LINES_DATA);
        Type listType = new TypeToken<ArrayList<BusLine>>() {
        }.getType();
        List<BusLine> busLines = new Gson().fromJson(jsonDataStr, listType);

        for (BusLine busLine : busLines) {
            int colorId = getColorForLine(context, busLine.getId());
            busLine.setColor(ContextCompat.getColor(context, colorId));

//            if (busLine.getId() == 1) {
//                busLine.set
//            }

            BusLineVisible busLineVisible = db.busLineVisibleDao().getBusLineVisible(busLine.getId());
            busLine.setVisible(busLineVisible.isVisible());
        }

        return busLines;
    }

    public static int getColorForLine(Context context, int lineId) {
        int colorId = context.getResources().getIdentifier("line" + lineId, "color", context.getPackageName());
        return colorId;
    }

}
