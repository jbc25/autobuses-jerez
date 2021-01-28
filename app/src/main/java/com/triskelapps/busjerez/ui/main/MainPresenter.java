package com.triskelapps.busjerez.ui.main;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.model.Place;
import com.triskelapps.busjerez.App;
import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.base.BasePresenter;
import com.triskelapps.busjerez.model.BusLine;
import com.triskelapps.busjerez.model.BusStop;
import com.triskelapps.busjerez.model.db.BusLineVisible;

import org.json.JSONException;

import java.util.List;

public class MainPresenter extends BasePresenter {

    private static final String URL_BUS_DATA_VERSION_FILE = "https://triskelapps.es/apps/autobuses-jerez/bus_data_version.json";
    private static final String URL_BUS_DATA_FILE = "https://triskelapps.es/apps/autobuses-jerez/bus_data.json";
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
                (Request.Method.GET, URL_BUS_DATA_VERSION_FILE, null,
                        response -> {
                            try {
                                int serverVersion = response.getInt("version");
                                int savedVersion = getPrefs().getInt(App.PREF_BUS_DATA_SAVED_VERSION, 1);
                                if (savedVersion < serverVersion) {
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
                (Request.Method.GET, URL_BUS_DATA_FILE,
                        response -> {
                            view.hideProgressDialog();
                            getPrefs().edit()
                                    .putString(App.PREF_BUS_DATA, response)
                                    .putInt(App.PREF_BUS_DATA_SAVED_VERSION, serverVersion)
                                    .commit();
                            loadData();
                        },
                        error -> {
                            view.hideProgressDialog();
                            error.printStackTrace();
                        });


        jsonObjectRequest.setShouldCache(false);

        queue.add(jsonObjectRequest);
    }

    public void onMapReady() {
        loadData();
    }

    public void onMapLoaded() {

        loadData(); // Method time: 8ms
    }


    private void loadData() {

        busLines = App.getBusLinesData(context);

        for (BusLine busLine : busLines) {
            for (int i = 0; i < busLine.getBusStops().size(); i++) {
                BusStop busStop = busLine.getBusStops().get(i);

                for (int j = 0; j < busLine.getBusStops().size(); j++) {
                    BusStop busStop1 = busLine.getBusStops().get(j);

                    if (i != j && busStop.getCode() == busStop1.getCode()) {
                        Log.i(TAG, "bus stop code repeated: " + busStop1.getCode() + ". Línea " + busLine.getId());
                    }
                }
            }
        }

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
