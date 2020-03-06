package com.triskelapps.busjerez.ui.main;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.base.BaseActivity;
import com.triskelapps.busjerez.databinding.ActivityMainBinding;
import com.triskelapps.busjerez.model.BusLine;
import com.triskelapps.busjerez.model.BusStop;
import com.triskelapps.busjerez.ui.main.address.AddressFragment;
import com.triskelapps.busjerez.ui.main.filter.BusLinesFragment;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, MainView, GoogleMap.OnPolylineClickListener {

    private final LatLng JEREZ_NORTH_EAST = new LatLng(36.707457, -6.093387);
    private final LatLng JEREZ_SOUTH_WEST = new LatLng(36.663924, -6.160751);

    private final LatLng JEREZ_CENTER = new LatLng(36.682757, -6.136800);

    private GoogleMap map;
    private MainPresenter presenter;
    private MapDataController mapDataController;
    private ActivityMainBinding binding;
    private Marker markerDestination;
    private BusLinesFragment fragmentBusLines;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = MainPresenter.newInstance(this, this);
        setBasePresenter(presenter);

        configureToolbar();
        configureDrawer();
        configureToolbarBackArrowBehaviour();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fragmentBusLines = (BusLinesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_bus_lines);

        presenter.onCreate();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_bottom, new AddressFragment())
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void configureDrawer() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureToolbarBackArrowBehaviour() {

        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> {

            if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                binding.drawerLayout.closeDrawer(Gravity.LEFT);
            } else {

                if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    binding.drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    binding.drawerLayout.openDrawer(Gravity.LEFT);
                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        mapDataController = new MapDataController();

        map.setBuildingsEnabled(false);
        map.setIndoorEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);

        map.setOnPolylineClickListener(this);

        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(JEREZ_CENTER, 14));

        map.setOnMapLoadedCallback(() -> {

            LatLngBounds latLngBoundsJerez = LatLngBounds.builder().include(JEREZ_NORTH_EAST).include(JEREZ_SOUTH_WEST).build();
            animateMapToBounds(latLngBoundsJerez);

            presenter.onMapLoaded();
        });

        checkLocationPermission();


    }

    private void animateMapToBounds(LatLngBounds bounds) {
        int padding = getResources().getDimensionPixelSize(R.dimen.padding_map);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_bus_lines:
                if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    binding.drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {

                    if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                        binding.drawerLayout.closeDrawer(Gravity.LEFT);
                    }

                    binding.drawerLayout.openDrawer(Gravity.RIGHT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mapDataController.hasBusLineSelected()) {
            mapDataController.unselectBusLine();
            getSupportFragmentManager().popBackStack();
            LatLngBounds latLngBoundsJerez = LatLngBounds.builder().include(JEREZ_NORTH_EAST).include(JEREZ_SOUTH_WEST).build();
            animateMapToBounds(latLngBoundsJerez);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
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
                                .setPositiveButton(R.string.accept, (dialog, which) -> token.continuePermissionRequest())
                                .setNegativeButton(R.string.cancel, (dialog, which) -> token.cancelPermissionRequest())
                                .show();
                    }
                }).check();
    }

    private void configureSelfLocationMap() {
        map.setMyLocationEnabled(true);
    }


    // INTERACTIONS

    @Override
    public void onPolylineClick(Polyline polyline) {

        int lineId = (int) polyline.getTag();
        mapDataController.selectBusLine(lineId);
        LatLngBounds lineBounds = mapDataController.getLineBounds(lineId);
        animateMapToBounds(lineBounds);

        presenter.onBusLinePathClick(lineId);

    }


    // PRESENTER CALLBACKS

    @Override
    public void loadBusLines(List<BusLine> busLines) {

        map.clear();

        for (BusLine busLine : busLines) {

            Polyline polylinePath = map.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .visible(busLine.isVisible())
                    .color(busLine.getColor())
                    .addAll(getPathLatLng(busLine)));

            polylinePath.setTag(busLine.getId());

            List<Marker> markersBusStopsLine = new ArrayList<>();

            for (BusStop busStop : busLine.getBusStops()) {

                LatLng position = new LatLng(busStop.getCoordinates().get(0), busStop.getCoordinates().get(1));
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(position)
                        .visible(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(getBusMarkerBitmapTinted(busLine.getColor())))
                        .title(busStop.getName()));

                marker.setTag(busStop);

                markersBusStopsLine.add(marker);

            }

            mapDataController.addLineData(busLine.getId(), polylinePath, markersBusStopsLine);

        }

    }

    private Bitmap getBusMarkerBitmapTinted(int color) {
        return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_bus_marker_2);
    }

    private List<LatLng> getPathLatLng(BusLine busLine) {
        List<LatLng> pathLatLng = new ArrayList<>();

        for (List<Double> coords : busLine.getPath()) {
            pathLatLng.add(new LatLng(coords.get(0), coords.get(1)));
        }

        return pathLatLng;
    }

    @Override
    public void showBusLineInfo(BusLine busLine) {
//        binding.viewBottomInfo.setVisibility(View.VISIBLE);
//        binding.tvLineInfo.setText(busLine.getName() + " - " + busLine.getDescription());
//        binding.tvLineInfo.setTextColor(busLine.getColor());
    }

    @Override
    public void setDestinationMarker(Place place) {
        markerDestination = map.addMarker(new MarkerOptions()
                .position(place.getLatLng())
                .title(place.getName()));

        map.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

    }

    @Override
    public void removeDestinationMarker() {
        if (markerDestination != null) {
            markerDestination.remove();
            markerDestination = null;
        }
    }

    @Override
    public void setBusLineVisible(int lineId, boolean visible) {
        mapDataController.setBusLineVisible(lineId, visible);
    }
}
