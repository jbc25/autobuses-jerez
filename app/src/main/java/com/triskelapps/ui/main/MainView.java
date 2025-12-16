package com.triskelapps.ui.main;

import com.triskelapps.base.BaseView;
import com.triskelapps.model.BusLine;
import com.triskelapps.model.BusStop;
import com.triskelapps.ui.main.address.LocationResult;

import java.util.List;

public interface MainView extends BaseView {
    void loadBusLines(List<BusLine> busLines);

    void showBusLineInfo(BusLine busLine, BusStop busStop, boolean animateToBounds);

    void setDestinationMarker(LocationResult locationResult);

    void removeDestinationMarker();

    void setBusLineVisible(int lineId, boolean visible);
}
