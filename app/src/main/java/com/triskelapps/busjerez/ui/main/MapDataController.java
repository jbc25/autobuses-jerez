package com.triskelapps.busjerez.ui.main;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapDataController {

    private int lineSelected = 0;

    private Map<Integer, Polyline> polylinePathMap = new HashMap<>();
    private Map<Integer, List<Marker>> markersBusStopsMap = new HashMap<>();

    public void selectBusLine(int lineId) {

        if (lineSelected > 0) {
            setMarkersOfLineSelectedVisible(false);
        }

        lineSelected = lineId;

        setMarkersOfLineSelectedVisible(true);
    }

    private void setMarkersOfLineSelectedVisible(boolean visible) {
        setMarkersVisible(lineSelected, visible);
    }

    public void setBusLineVisible(int lineId, boolean visible) {
        polylinePathMap.get(lineId).setVisible(visible);

        if (!visible) {
            setMarkersVisible(lineId, false);
        }
    }

    public void setMarkersVisible(int lineId, boolean visible) {
        for (Marker marker : markersBusStopsMap.get(lineId)) {
            marker.setVisible(visible);
        }
    }

    public LatLngBounds getLineBounds(int lineId) {

        LatLngBounds.Builder latLngBuilder = LatLngBounds.builder();
        for (LatLng point : polylinePathMap.get(lineId).getPoints()) {
            latLngBuilder.include(point);
        }
        return latLngBuilder.build();
    }

    public void addLineData(int lineId, Polyline polylinePath, List<Marker> markersBusStopsLine) {
        polylinePathMap.put(lineId, polylinePath);
        markersBusStopsMap.put(lineId, markersBusStopsLine);
    }

    public boolean hasBusLineSelected() {
        return lineSelected > 0;
    }

    public void unselectBusLine() {
        setMarkersOfLineSelectedVisible(false);
        lineSelected = 0;
    }
}
