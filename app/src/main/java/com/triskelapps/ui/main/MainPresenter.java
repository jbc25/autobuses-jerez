package com.triskelapps.ui.main;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.model.Place;
import com.triskelapps.App;
import com.triskelapps.CityData;
import com.triskelapps.R;
import com.triskelapps.base.BasePresenter;
import com.triskelapps.model.BusLine;
import com.triskelapps.model.db.BusLineVisible;

import org.json.JSONException;

import java.util.List;

public class MainPresenter extends BasePresenter {

    private final MainView view;
    List<BusLine> busLines;

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

//        DataProcessUtil.with(context).processData();

        checkDataVersion();

    }

    private void checkDataVersion() {

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, CityData.URL_BUS_DATA_VERSION_FILE, null,
                        response -> {
                            try {
                                int serverVersion = response.getInt("version");
                                int savedVersion = getPrefs().getInt(App.PREF_BUS_DATA_SAVED_VERSION, 1);
//                                if (savedVersion < serverVersion) {
                                if(true) {
                                    syncData(serverVersion);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> error.printStackTrace());


        jsonObjectRequest.setShouldCache(false);

        queue.add(jsonObjectRequest);
    }

    private void syncData(int serverVersion) {

        Log.i(TAG, "syncData: serverVersion: " + serverVersion);

        view.showProgressDialog(getString(R.string.updating_data));
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest jsonObjectRequest = new StringRequest
                (Request.Method.GET, CityData.URL_BUS_DATA_FILE,
                        response -> {
                            view.hideProgressDialog();
                            getPrefs().edit()
                                    .putString(App.PREF_BUS_DATA, response)
                                    .putInt(App.PREF_BUS_DATA_SAVED_VERSION, serverVersion)
                                    .commit();
                            loadData(true);
                        },
                        error -> {
                            view.hideProgressDialog();
                            error.printStackTrace();
                        });


        jsonObjectRequest.setShouldCache(false);

        queue.add(jsonObjectRequest);
    }

    public void onMapReady() {
        loadData(false);
    }

    public void onMapLoaded() {

        loadData(false); // Method time: 8ms
    }


    private void loadData(boolean forzeReload) {

        busLines = App.getBusLinesData(context, forzeReload);

        view.loadBusLines(busLines);
    }

    public void onBusLinePathClick(int lineId, boolean animateToBounds) {
        for (BusLine busLine : busLines) {
            if (busLine.getId() == lineId) {
                view.showBusLineInfo(busLine, animateToBounds);
                return;
            }
        }

        throw new IllegalStateException("lineId not valid: " + lineId);
    }

    public void onPlaceSelected(Place place) {

        view.setDestinationMarker(place);

    }

    public void onClearPlaceAutocompleteText() {
        view.removeDestinationMarker();
    }

    public void onBusLineCheckedChanged(int position, boolean checked) {
        int lineId = busLines.get(position).getId();
        view.setBusLineVisible(lineId, checked);

        App.getDB().busLineVisibleDao().update(new BusLineVisible(lineId, checked));
    }

}
