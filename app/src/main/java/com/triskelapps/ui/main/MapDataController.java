package com.triskelapps.ui.main;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.triskelapps.model.BusStop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapDataController {

    private static final String TAG = "MapDataController";
    private int lineSelected = 0;

    private Map<Integer, Polyline> polylinePathMap = new HashMap<>();
    private Map<Integer, List<Marker>> markersBusStopsMap = new HashMap<>();
    private Map<Integer, List<Marker>> markersArrowsMap = new HashMap<>();

    public void selectBusLine(int lineId) {

        if (lineSelected > 0) {
            unselectBusLine();
        }

        lineSelected = lineId;

        polylinePathMap.get(lineId).setZIndex(1);

        setMarkersOfLineSelectedVisible(true);
    }


    public void unselectBusLine() {
        setMarkersOfLineSelectedVisible(false);
        polylinePathMap.get(lineSelected).setZIndex(0);
        lineSelected = 0;
    }

    private void setMarkersOfLineSelectedVisible(boolean visible) {
        setMarkersVisible(lineSelected, visible);
    }

    public void setBusLineVisible(int lineId, boolean visible) {
        polylinePathMap.get(lineId).setVisible(visible);
        polylinePathMap.get(lineId).setClickable(visible);

        if (!visible) {
            setMarkersVisible(lineId, false);
        }
    }

    public void setMarkersVisible(int lineId, boolean visible) {
        for (Marker marker : markersBusStopsMap.get(lineId)) {
            marker.setVisible(visible);
        }

        for (Marker marker : markersArrowsMap.get(lineId)) {
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

    public void addLineData(int lineId, Polyline polylinePath, List<Marker> markersBusStopsLine, List<Marker> markersArrowsLine) {
        polylinePathMap.put(lineId, polylinePath);
        markersBusStopsMap.put(lineId, markersBusStopsLine);
        markersArrowsMap.put(lineId, markersArrowsLine);
    }

    public boolean hasBusLineSelected() {
        return lineSelected > 0;
    }

    public Marker getMarker(int busStopCode) {
        List<Marker> markers = markersBusStopsMap.get(lineSelected);
        return markers.stream().filter(marker -> {
            BusStop markerBusStop = (BusStop) marker.getTag();
            return markerBusStop.getCode() == busStopCode;
        }).findFirst().get();
    }
}
