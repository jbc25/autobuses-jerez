package com.triskelapps.busjerez.ui.main;


import android.content.Context;
import android.content.Intent;

import com.google.android.libraries.places.api.model.Place;
import com.triskelapps.busjerez.App;
import com.triskelapps.busjerez.base.BasePresenter;
import com.triskelapps.busjerez.model.BusLine;

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

    public void onResume() {

        refreshData();
    }

    public void refreshData() {


    }

    public void onMapLoaded() {

        loadData(); // Method time: 8ms
    }


    private void loadData() {

        busLines = App.getBusLinesData(context);

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
        view.setBusLineVisible(busLines.get(position).getId(), checked);
    }
}
