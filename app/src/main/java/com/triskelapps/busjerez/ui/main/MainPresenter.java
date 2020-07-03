package com.triskelapps.busjerez.ui.main;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.libraries.places.api.model.Place;
import com.triskelapps.busjerez.App;
import com.triskelapps.busjerez.base.BasePresenter;
import com.triskelapps.busjerez.model.BusLine;
import com.triskelapps.busjerez.model.BusStop;
import com.triskelapps.busjerez.model.db.BusLineVisible;
import com.triskelapps.busjerez.util.DataProcessUtil;

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

    public void onBusLinePathClick(int lineId) {
        for (BusLine busLine : busLines) {
            if (busLine.getId() == lineId) {
                view.showBusLineInfo(busLine);
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
