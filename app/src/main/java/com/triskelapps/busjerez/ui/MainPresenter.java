package com.triskelapps.busjerez.ui;


import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.triskelapps.busjerez.base.BasePresenter;
import com.triskelapps.busjerez.model.BusLine;
import com.triskelapps.busjerez.util.DataProcessUtil;
import com.triskelapps.busjerez.util.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter extends BasePresenter {

    private static final String FILE_BUS_LINES_DATA = "bus_lines_data.json";
    private final MainView view;

    public static void launchMainActivity(Context context) {

        Intent intent = new Intent(context, MainActivity.class);

        context.startActivity(intent);
    }

    public static MainPresenter newInstance(MainView view, Context context) {

        return new MainPresenter(view, context);

    }

    private MainPresenter(MainView view, Context context) {
        super(context, view);

        this.view = view;

    }

    public void onCreate() {

            DataProcessUtil.with(context).processData();

    }

    public void onResume() {

        refreshData();
    }

    public void refreshData() {


    }

    public void onMapLoaded() {

        loadData(); // Method time: 8ms
    }


    private void loadData() {

        String jsonDataStr = Util.getStringFromAssets(context, FILE_BUS_LINES_DATA);
        Type listType = new TypeToken<ArrayList<BusLine>>(){}.getType();
        List<BusLine> busLines = new Gson().fromJson(jsonDataStr, listType);

        for (BusLine busLine : busLines) {
            int colorId = context.getResources().getIdentifier("line" + busLine.getId(), "color", context.getPackageName());
            busLine.setColor(ContextCompat.getColor(context, colorId));
        }

        view.loadBusLines(busLines);
    }

}
