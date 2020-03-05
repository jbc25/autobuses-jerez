package com.triskelapps.busjerez.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.base.BaseActivity;
import com.triskelapps.busjerez.model.BusLine;
import com.triskelapps.busjerez.model.BusStop;
import com.triskelapps.busjerez.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    private final LatLng JEREZ_NORTH_EAST = new LatLng(36.707457, -6.093387);
    private final LatLng JEREZ_SOUTH_WEST = new LatLng(36.663924, -6.160751);

    private final LatLng JEREZ_CENTER = new LatLng(36.682757, -6.136800);

    private GoogleMap map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        configureToolbar();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void testGeoJson() {

        String jsonStr = Util.getStringFromAssets(this, "test.geojson"); // A string containing GeoJSON

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("features");

            BusLine busLine = new BusLine();
            List<BusStop> busStops = new ArrayList<>();

            boolean lineStringProcessed = false;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonFeature = jsonArray.getJSONObject(i);
                JSONObject jsonGeometry = jsonFeature.getJSONObject("geometry");
                String type = jsonGeometry.getString("type");
                switch (type) {
                    case "Point":

                        BusStop busStop = new BusStop();

                        String nameLine1 = jsonFeature.getJSONObject("properties").getString("name");
                        busStop.setName(nameLine1);

                        JSONArray coords = jsonGeometry.getJSONArray("coordinates");

                        List<Double> coordinates = new ArrayList<>();
                        coordinates.add(coords.getDouble(1));
                        coordinates.add(coords.getDouble(0));

                        busStop.setCoordinates(coordinates);
                        busStops.add(busStop);

                        busLine.setBusStops(busStops);
                        break;

                    case "LineString":

                        if (lineStringProcessed) {
                            throw new IllegalStateException("More than one LineString");
                        }

                        String nameLine = jsonFeature.getJSONObject("properties").getString("name");
                        busLine.setName(nameLine);

                        JSONArray coordinatesList = jsonGeometry.getJSONArray("coordinates");
                        List<List<Double>> path = new ArrayList<>();

                        for (int j = 0; j < coordinatesList.length(); j++) {
                            JSONArray latLngCoords = coordinatesList.getJSONArray(j);
                            path.add(new ArrayList<Double>() {{
                                add(latLngCoords.getDouble(1));
                                add(latLngCoords.getDouble(0));
                            }});

                        }

                        busLine.setPath(path);

                        lineStringProcessed = true;

                        break;
                }
            }

            busLine.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setBuildingsEnabled(false);
        map.setIndoorEnabled(false);

        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(JEREZ_CENTER, 14));

        map.setOnMapLoadedCallback(() -> {
            LatLngBounds latLngBoundsJerez = LatLngBounds.builder().include(JEREZ_NORTH_EAST).include(JEREZ_SOUTH_WEST).build();
            int padding = getResources().getDimensionPixelSize(R.dimen.padding_map);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsJerez, padding));


            testGeoJson();
        });

        checkLocationPermission();

    }


    private void checkLocationPermission() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        configureSelfLocationMap();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toasty.error(MainActivity.this, getString(R.string.permission_denied)).show();
                        finish();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.permission_needed)
                                .setMessage(R.string.location_permission_message)
                                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        token.continuePermissionRequest();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        token.cancelPermissionRequest();
                                    }
                                })
                                .show();
                    }
                }).check();
    }

    private void configureSelfLocationMap() {
        map.setMyLocationEnabled(true);
    }
}
