package com.triskelapps.busjerez.ui.main;

import com.google.android.libraries.places.api.model.Place;
import com.triskelapps.busjerez.base.BaseView;
import com.triskelapps.busjerez.model.BusLine;

import java.util.List;

public interface MainView extends BaseView {
    void loadBusLines(List<BusLine> busLines);

    void showBusLineInfo(BusLine busLine);

    void setDestinationMarker(Place place);

    void removeDestinationMarker();

    void setBusLineVisible(int lineId, boolean visible);
}
