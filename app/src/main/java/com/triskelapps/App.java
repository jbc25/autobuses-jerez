package com.triskelapps;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.google.android.libraries.places.api.Places;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.triskelapps.database.MyDatabase;
import com.triskelapps.model.BusLine;
import com.triskelapps.model.db.BusLineVisible;
import com.triskelapps.simpleappupdate.SimpleAppUpdate;
import com.triskelapps.simpleappupdate.config.NotificationStyle;
import com.triskelapps.simpleappupdate.config.WorkerConfig;
import com.triskelapps.util.CountlyUtil;
import com.triskelapps.util.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class App extends MultiDexApplication {

    private static final String TAG = "App";

    public static final int BUS_LINES_COUNT = 18;

    public static final String PREFIX = BuildConfig.APPLICATION_ID + ".";

    public static final String PREF_FIRST_TIME_LAUNCH = PREFIX + "pref_first_time_launch";
    public static final String PREF_FIRST_TIME_DATA_POPULATED = PREFIX + "pref_first_time_data_populated";
    public static final String PREF_BUS_DATA = PREFIX + "pref_bus_data";
    public static final String PREF_BUS_DATA_SAVED_VERSION = PREFIX + "pref_bus_data_saved_version";
    public static final String PREF_BANNER_IGNORED_ID = PREFIX + "pref_banner_ignored_id_";


    private static final String FILE_BUS_LINES_DATA = "bus_lines_data.json";
    private static final String TOPIC_GENERAL = "general";
    private static final String TOPIC_GENERAL_TEST = "general_test";

    private static MyDatabase db;
    //    public static final String SHARED_TOKEN = PREFIX + "shared_token";

    public static final String DB_NAME = "app_db.sqlite";
    private static List<BusLine> busLines;


    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        CountlyUtil.configureCountly(this);

        String apiKey = getString(R.string.maps_api_key);
        Places.initialize(getApplicationContext(), apiKey);

        db = Room.databaseBuilder(getApplicationContext(),
                MyDatabase.class, DB_NAME)
//                .addMigrations(DBMigrationBase.MIGRATION_5_6)
//                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();


        // Periodic app update configuration
        new SimpleAppUpdate(this).cancelWork("appUpdateCheckWork");
        NotificationStyle notificationStyle = new NotificationStyle(R.mipmap.ic_notification, R.color.colorPrimary);
        WorkerConfig workerConfig = new WorkerConfig(6, TimeUnit.HOURS, 30, TimeUnit.MINUTES);
        SimpleAppUpdate.schedulePeriodicChecks(this, BuildConfig.VERSION_CODE, notificationStyle, workerConfig);

//        clearPersistedData();

        boolean firstTimeLaunch = getDB().busLineVisibleDao().getAll().size() == 0;
        getPrefs(this).edit().putBoolean(PREF_FIRST_TIME_LAUNCH, firstTimeLaunch).commit();

        populateDataFirstTime();

//        NotificationHelper.with(this).initializeOreoChannelsNotification();

        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GENERAL_TEST);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_GENERAL);
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GENERAL);
        }

        loadData(this);

    }

    private void clearPersistedData() {

        getPrefs(this).edit().remove(PREF_BUS_DATA).commit();
        db.busLineVisibleDao().deleteAll();
        db.busStopDao().deleteAll();
    }


    private void populateDataFirstTime() {

        Log.i(TAG, "populateDataFirstTime: 1");

        if (getPrefs(this).getString(PREF_BUS_DATA, null) == null) {
            String jsonDataStrInitial = Util.getStringFromAssets(this, FILE_BUS_LINES_DATA);
            getPrefs(this).edit().putString(PREF_BUS_DATA, jsonDataStrInitial).commit();

            populateBusLineVisibleTable(jsonDataStrInitial);
        }

        Log.i(TAG, "populateDataFirstTime: 2");

    }

    private void populateBusLineVisibleTable(String jsonDataStrInitial) {
        db.busLineVisibleDao().deleteAll();

        Type listType = new TypeToken<ArrayList<BusLine>>() {
        }.getType();
        List<BusLine> busLines = new Gson().fromJson(jsonDataStrInitial, listType);

        for(BusLine busLine : busLines) {
            db.busLineVisibleDao().insert(new BusLineVisible(busLine.getId(), true));
        }
    }

    public static MyDatabase getDB() {
        return db;
    }

    public static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static void loadData(Context context) {

        String jsonDataStr = getPrefs(context).getString(PREF_BUS_DATA, null);
        Type listType = new TypeToken<ArrayList<BusLine>>() {}.getType();
        busLines = new Gson().fromJson(jsonDataStr, listType);

        for (BusLine busLine : busLines) {
            BusLineVisible busLineVisible = db.busLineVisibleDao().getBusLineVisible(busLine.getId());
            busLine.setVisible(busLineVisible != null ? busLineVisible.isVisible() : true);
        }

    }


    public static List<BusLine> getBusLinesData(Context context) {
        return getBusLinesData(context, false);
    }

    public static List<BusLine> getBusLinesData(Context context, boolean forzeReload) {
        if (busLines == null || forzeReload) {
            loadData(context);
        }

        return busLines;
    }

    public static int getColorForLine(Context context, int lineId) {
        if (busLines == null) {
            loadData(context);
        }

        for (BusLine busLine : busLines) {
            if (busLine.getId() == lineId) {
                return busLine.getColor();
            }
        }

        return Color.BLACK;
    }

}
