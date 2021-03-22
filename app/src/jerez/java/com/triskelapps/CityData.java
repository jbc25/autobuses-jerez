package com.triskelapps;

import com.google.android.gms.maps.model.LatLng;

public class CityData {


    public final static LatLng COORD_CENTER = new LatLng(36.687458, -6.127826);
    public final static float INITIAL_ZOOM = 12.5f;

    public final static LatLng COORD_NORTH_EAST = new LatLng(36.707457, -6.093387);
    public final static LatLng COORD_SOUTH_WEST = new LatLng(36.663924, -6.160751);


    public final static LatLng AUTOCOMPLETE_RESULTS_COORDS_NORTH_EAST = new LatLng(36.765444, -6.017372);
    public final static LatLng AUTOCOMPLETE_RESULTS_COORDS_SOUTH_WEST = new LatLng(36.626432, -6.161407);

    public static final String URL_BUS_DATA_VERSION_FILE = "https://triskelapps.es/apps/autobuses-jerez/bus_data_version.json";
    public static final String URL_BUS_DATA_FILE = "https://triskelapps.es/apps/autobuses-jerez/bus_data.json";
}
