package com.triskelapps.busjerez.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.SphericalUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.triskelapps.busjerez.App;
import com.triskelapps.busjerez.BuildConfig;
import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.base.BaseActivity;
import com.triskelapps.busjerez.databinding.ActivityMainBinding;
import com.triskelapps.busjerez.databinding.ViewTextDialogBinding;
import com.triskelapps.busjerez.model.BusLine;
import com.triskelapps.busjerez.model.BusStop;
import com.triskelapps.busjerez.ui.favourites.FavouritesActivity;
import com.triskelapps.busjerez.ui.main.address.AddressFragment;
import com.triskelapps.busjerez.ui.main.bus_stops.BusStopsFragment;
import com.triskelapps.busjerez.ui.main.filter.FilterBusLinesFragment;
import com.triskelapps.busjerez.ui.news.NewsActivity;
import com.triskelapps.busjerez.util.ChangelogHelper;
import com.triskelapps.busjerez.util.CountlyUtil;
import com.triskelapps.busjerez.util.ShareAppReminderHelper;
import com.triskelapps.busjerez.util.Util;
import com.triskelapps.busjerez.views.TextViewHTML;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, MainView, GoogleMap.OnPolylineClickListener, GoogleMap.OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener {

    public final LatLng JEREZ_NORTH_EAST = new LatLng(36.707457, -6.093387);
    public final LatLng JEREZ_SOUTH_WEST = new LatLng(36.663924, -6.160751);

    private final LatLng JEREZ_CENTER = new LatLng(36.843724859578046, -2.450765243421731);

    private GoogleMap map;
    private MainPresenter presenter;
    private MapDataController mapDataController;
    private ActivityMainBinding binding;
    private Marker markerDestination;
    private FilterBusLinesFragment fragmentFilterBusLines;
    private AddressFragment addressFragment;
    private BusStopsFragment busStopsFragment;


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

        binding.navView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.i(TAG, "mapAsync started");

        binding.progressMap.setVisibility(View.GONE);

        fragmentFilterBusLines = (FilterBusLinesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_filter_bus_lines);

        presenter.onCreate();

        binding.tvAppVersion.setText(getString(R.string.version_format, BuildConfig.VERSION_NAME));

        addressFragment = (AddressFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_address);

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        ChangelogHelper.with(this).check();
        ShareAppReminderHelper.with(this).check();
        checkWelcomeMessage();

    }

    private void checkWelcomeMessage() {
        if (getPrefs().getBoolean(App.PREF_FIRST_TIME_LAUNCH, true)) {
            CountlyUtil.recordEvent("welcome_screen_show_dialog");
            new AlertDialog.Builder(this)
                    .setTitle(R.string.welcome)
                    .setMessage(R.string.welcome_text)
                    .setPositiveButton(R.string.watch_video, (dialog, which) -> {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(App.URL_YOUTUBE_VIDEO)));
                        CountlyUtil.recordEvent("welcome_screen_click_watch_video");
                    })
                    .setNeutralButton(R.string.close, null)
                    .show();

            getPrefs().edit().putBoolean(App.PREF_FIRST_TIME_LAUNCH, false).commit();
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
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

        Log.i(TAG, "mapAsync ready");

        map = googleMap;

        mapDataController = new MapDataController();

        map.setBuildingsEnabled(false);
        map.setIndoorEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);

        map.setOnPolylineClickListener(this);
        map.setOnMarkerClickListener(this);

        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(JEREZ_CENTER, 12.5f));

        presenter.onMapReady();

//        map.setOnMapLoadedCallback(() -> {
//
//            binding.progressMap.setVisibility(View.GONE);
//
//            LatLngBounds latLngBoundsJerez = LatLngBounds.builder().include(JEREZ_NORTH_EAST).include(JEREZ_SOUTH_WEST).build();
//            animateMapToBounds(latLngBoundsJerez);
//
//            presenter.onMapLoaded();
//        });

        checkLocationPermission();


    }

    private void animateMapToBounds(LatLngBounds bounds) {
        try {
            int padding = getResources().getDimensionPixelSize(R.dimen.padding_map);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        } catch (Exception e) {
            //ignore. a line was selected before maps is loaded. not important
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


    @SuppressLint("MissingPermission")
    private void configureSelfLocationMap() {
        map.setMyLocationEnabled(true);
    }


    // INTERACTIONS


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

            case R.id.menu_favourites:
                startActivity(new Intent(this, FavouritesActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            if (hasDrawerPanelOpened()) {
                closeDrawerPanels();
            } else if (busStopsFragment != null && busStopsFragment.hasBusStopSelected()) {
                busStopsFragment.clearBusStopSelection();
                map.getUiSettings().setMapToolbarEnabled(false);
            } else if (mapDataController.hasBusLineSelected()) {
                mapDataController.unselectBusLine();
                getSupportFragmentManager().popBackStack();
                LatLngBounds latLngBoundsJerez = LatLngBounds.builder().include(JEREZ_NORTH_EAST).include(JEREZ_SOUTH_WEST).build();
//            animateMapToBounds(latLngBoundsJerez);
            } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else if (addressFragment != null && markerDestination != null) {
                addressFragment.clearAddress();
                presenter.onClearPlaceAutocompleteText();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            super.onBackPressed();
        }
    }

    private void closeDrawerPanels() {

        if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            binding.drawerLayout.closeDrawer(Gravity.LEFT);
        } else if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            binding.drawerLayout.closeDrawer(Gravity.RIGHT);
        }
    }

    private boolean hasDrawerPanelOpened() {
        return binding.drawerLayout.isDrawerOpen(Gravity.LEFT) || binding.drawerLayout.isDrawerOpen(Gravity.RIGHT);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_news:
                startActivity(new Intent(this, NewsActivity.class));
                break;

            case R.id.nav_covid_rules:
                showCovidRulesDialog();
                CountlyUtil.recordEvent("click_menu_covid_rules");
                break;

            case R.id.nav_buses_contact:
                showDialogAssetsHtml("info/buses_contact.html");
                CountlyUtil.recordEvent("click_menu_buses_contact");
                break;

            case R.id.nav_info:
                showAboutDialog();
                CountlyUtil.recordEvent("click_menu_app_info");
                break;

            case R.id.nav_developer:
                showDialogAssetsHtml("info/developer_info.html");
                CountlyUtil.recordEvent("click_menu_developer");
                break;

            case R.id.nav_free_software:
                showDialogAssetsHtml("info/free_software.html");
                CountlyUtil.recordEvent("click_menu_free_software");
                break;

            case R.id.nav_watch_video:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(App.URL_YOUTUBE_VIDEO)));
                CountlyUtil.recordEvent("click_menu_watch_video");
                break;

            case R.id.nav_share:
                Util.shareText(this, getString(R.string.share_text), getString(R.string.share_app));
                CountlyUtil.recordEvent("click_menu_share");
                break;
        }
        closeDrawerPanels();
        return false;
    }

    private void showDialogAssetsHtml(String assetFile) {

        String htmlContactText = Util.getStringFromAssets(this, assetFile);
        ViewTextDialogBinding textDialogBinding = ViewTextDialogBinding.inflate(getLayoutInflater());
        Util.setHtmlLinkableText(textDialogBinding.tvTextDialog, htmlContactText);

        new AlertDialog.Builder(this)
                .setView(textDialogBinding.getRoot())
                .setNegativeButton(R.string.back, null)
                .show();
    }

    private void showCovidRulesDialog() {

        View layout = View.inflate(this, R.layout.view_covid_rules, null);

        new AlertDialog.Builder(this)
//                .setTitle(R.string.covid_rules)
                .setView(layout)
                .setNegativeButton(R.string.back, null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_info)
                .setMessage(R.string.app_info_text)
                .setPositiveButton(R.string.contact, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","julio@triskelapps.com", null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_email_subject));
//                    intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
                    startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
                })
                .setNegativeButton(R.string.back, null)
                .show();
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        int lineId = (int) polyline.getTag();
        presenter.onBusLinePathClick(lineId, false);
        CountlyUtil.selectBusLine(lineId, "polyline_click");

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        map.getUiSettings().setMapToolbarEnabled(true);
        if (busStopsFragment != null && marker.getTag() != null && marker.getTag() instanceof BusStop) {
            busStopsFragment.selectBusStop((BusStop) marker.getTag());
            return false;
        }
        return true;
    }

    public void selectBusStopMarker(int position) {
        if (mapDataController.hasBusLineSelected() && position != -1) {
            map.getUiSettings().setMapToolbarEnabled(false);
            Marker marker = mapDataController.getMarker(position);
            marker.showInfoWindow();
            map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }
    }

    public void unselectBusStopMarker(int position) {
        if (mapDataController.hasBusLineSelected() && position != -1) {
            Marker marker = mapDataController.getMarker(position);
            marker.hideInfoWindow();
        }
    }

    // PRESENTER CALLBACKS

    @Override
    public void loadBusLines(List<BusLine> busLines) {


        map.clear();

        Log.i(TAG, "loadBusLines: start");

        for (BusLine busLine : busLines) {

            Polyline polylinePath = map.addPolyline(new PolylineOptions()
                    .visible(busLine.isVisible())
                    .clickable(busLine.isVisible())
                    .color(busLine.getColor())
                    .width(getResources().getDimensionPixelSize(R.dimen.width_polyline))
                    .addAll(getPathLatLng(busLine)));

            polylinePath.setTag(busLine.getId());

            List<Marker> markersBusStopsLine = new ArrayList<>();

            for (BusStop busStop : busLine.getBusStops()) {

                LatLng position = new LatLng(busStop.getCoordinates().get(0), busStop.getCoordinates().get(1));
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(position)
                        .visible(true)
                        .icon(getBusMarkerIcon(busLine.getColor()))
                        .title(busStop.getName()));

                marker.setTag(busStop);

                markersBusStopsLine.add(marker);

            }

            List<Marker> markersArrowsLine = new ArrayList<>();
//            int PATH_STEP = 7;
//            BitmapDescriptor bitmapDescriptorLine = BitmapDescriptorFactory.fromBitmap(tintArrow(App.getColorForLine(this, busLine.getId())));
//            for (int pathIndex = 0; pathIndex < busLine.getPath().size(); pathIndex++) {
//                if (pathIndex % PATH_STEP == 0 && pathIndex + 1 < busLine.getPath().size()) {
//                    List<Double> currentCoords = busLine.getPath().get(pathIndex);
//                    List<Double> nextCoords = busLine.getPath().get(pathIndex+1);
//                    LatLng currentLatLng = new LatLng(currentCoords.get(0), currentCoords.get(1));
//                    LatLng nextLatLng = new LatLng(nextCoords.get(0), nextCoords.get(1));
//                    double rotation = SphericalUtil.computeHeading(currentLatLng, nextLatLng);
//
//                    double middleLat = (currentCoords.get(0) + nextCoords.get(0)) / 2;
//                    double middleLng = (currentCoords.get(1) + nextCoords.get(1)) / 2;
//                    LatLng position = new LatLng(middleLat, middleLng);
//                    Marker marker = map.addMarker(new MarkerOptions()
//                            .position(position)
//                            .visible(false)
//                            .rotation((float) rotation)
//                            .icon(bitmapDescriptorLine));
//
//                    markersArrowsLine.add(marker);
//                }
//
//            }

            mapDataController.addLineData(busLine.getId(), polylinePath, markersBusStopsLine, markersArrowsLine);

        }

        Log.i(TAG, "loadBusLines: finish");


    }

    private Bitmap tintArrow(int colorId) {

        int color = ContextCompat.getColor(this, colorId);

        Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_arrow_busline);
        Bitmap resultBitmap = Bitmap.createBitmap(markerBitmap.getWidth(), markerBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);

        Paint markerPaint = new Paint();
        markerPaint.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(markerBitmap, 0, 0, markerPaint);
        return resultBitmap;
    }


    private BitmapDescriptor getBusMarkerIcon(int color) {

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_bus_marker_2);
        Bitmap iconTinted = changeBitmapColor(icon, color);

        return BitmapDescriptorFactory.fromBitmap(iconTinted);
    }

    private Bitmap changeBitmapColor(Bitmap sourceBitmap, int color) {

        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }


    private List<LatLng> getPathLatLng(BusLine busLine) {
        List<LatLng> pathLatLng = new ArrayList<>();

        for (List<Double> coords : busLine.getPath()) {
            pathLatLng.add(new LatLng(coords.get(0), coords.get(1)));
        }

        return pathLatLng;
    }

    @Override
    public void showBusLineInfo(BusLine busLine, boolean animateToBounds) {


        if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            binding.drawerLayout.closeDrawer(Gravity.RIGHT);
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            getSupportFragmentManager().popBackStack();
        }

        busStopsFragment = BusStopsFragment.newInstance(busLine);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_bottom, busStopsFragment)
                .addToBackStack(null)
                .commit();

        mapDataController.selectBusLine(busLine.getId());
        LatLngBounds lineBounds = mapDataController.getLineBounds(busLine.getId());
        if (animateToBounds) {
            animateMapToBounds(lineBounds);
        }
    }

    @Override
    public void setDestinationMarker(Place place) {

        removeDestinationMarker();

        markerDestination = map.addMarker(new MarkerOptions()
                .position(place.getLatLng())
                .title(place.getName()));

        markerDestination.showInfoWindow();

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
