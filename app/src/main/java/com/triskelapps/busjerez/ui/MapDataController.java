package com.triskelapps.busjerez.ui;

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
            setMarkersVisible(false);
        }

        lineSelected = lineId;

        setMarkersVisible(true);
    }

    public void setDataVisible(boolean visible) {
        setMarkersVisible(visible);
        polylinePathMap.get(lineSelected).setVisible(visible);
    }

    public void setMarkersVisible(boolean visible) {
        for (Marker marker : markersBusStopsMap.get(lineSelected)) {
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
        setMarkersVisible(false);
        lineSelected = 0;
    }
}
