package com.triskelapps.busjerez;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.libraries.places.api.Places;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.triskelapps.busjerez.database.MyDatabase;
import com.triskelapps.busjerez.model.BusLine;
import com.triskelapps.busjerez.util.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class App extends Application {

    private static final String TAG = "App";

    public static final String URL_GOOGLE_PLAY_APP = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    public static final String URL_DIRECT_GOOGLE_PLAY_APP = "market://details?id=" + BuildConfig.APPLICATION_ID;

    public static final String PREFIX = BuildConfig.APPLICATION_ID + ".";

    private static final String FILE_BUS_LINES_DATA = "bus_lines_data.json";

    private static MyDatabase db;
    //    public static final String SHARED_TOKEN = PREFIX + "shared_token";

    public static final String DB_NAME = "app_db.sqlite";


    @Override
    public void onCreate() {
        super.onCreate();

        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);

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

    public static List<BusLine> getBusLinesData(Context context) {

        String jsonDataStr = Util.getStringFromAssets(context, FILE_BUS_LINES_DATA);
        Type listType = new TypeToken<ArrayList<BusLine>>() {
        }.getType();
        List<BusLine> busLines = new Gson().fromJson(jsonDataStr, listType);

        for (BusLine busLine : busLines) {
            int colorId = context.getResources().getIdentifier("line" + busLine.getId(), "color", context.getPackageName());
            busLine.setColor(ContextCompat.getColor(context, colorId));
        }

        return busLines;
    }

}
