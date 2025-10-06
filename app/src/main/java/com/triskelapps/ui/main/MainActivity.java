package com.triskelapps.ui.main;

import static android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import com.triskelapps.App;
import com.triskelapps.BuildConfig;
import com.triskelapps.CityData;
import com.triskelapps.R;
import com.triskelapps.base.BaseActivity;
import com.triskelapps.databinding.ActivityMainBinding;
import com.triskelapps.databinding.ViewTextDialogBinding;
import com.triskelapps.model.BusLine;
import com.triskelapps.model.BusStop;
import com.triskelapps.ui.favourites.FavouritesActivity;
import com.triskelapps.ui.main.address.AddressFragment;
import com.triskelapps.ui.main.bus_stops.BusStopsFragment;
import com.triskelapps.ui.main.filter.FilterBusLinesFragment;
import com.triskelapps.features.news.ui.NewsActivity;
import com.triskelapps.util.ChangelogHelper;
import com.triskelapps.util.CountlyUtil;
import com.triskelapps.util.ShareAppReminderHelper;
import com.triskelapps.util.Util;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, MainView, GoogleMap.OnPolylineClickListener, GoogleMap.OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int REQ_CODE_LOCATION_PERMISSION = 3214;

    private GoogleMap map;
    private MainPresenter presenter;
    private MapDataController mapDataController;
    private ActivityMainBinding binding;
    private Marker markerDestination;
    private FilterBusLinesFragment fragmentFilterBusLines;
    private AddressFragment addressFragment;
    private BusStopsFragment busStopsFragment;
    private MenuItem menuItemAllowLocation;

    private boolean locationPermissionPermanentlyDenied;

    ActivityResultLauncher<Intent> settingsActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> checkLocationPermission());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // https://developer.android.com/develop/ui/views/layout/edge-to-edge#kotlin
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = MainPresenter.newInstance(this, this);
        setBasePresenter(presenter);

        configureToolbar();
        configureDrawer();
        configureToolbarBackArrowBehaviour();

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );
            binding.frameToolbar.setPadding(0, bars.top, 0, 0);
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getDecorView().getWindowInsetsController().setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS);
        } else {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; // Remove light status bar flag
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        binding.navView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.i(TAG, "mapAsync started");

        binding.progressMap.setVisibility(View.GONE);

        fragmentFilterBusLines = (FilterBusLinesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_filter_bus_lines);

        presenter.onCreate(getIntent());

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
                    .setMessage(getString(R.string.welcome_text, getString(R.string.app_name_description)))
                    .setPositiveButton(R.string.watch_video, (dialog, which) -> {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CityData.URL_YOUTUBE_VIDEO)));
                        CountlyUtil.recordEvent("welcome_screen_click_watch_video");
                    })
                    .setNeutralButton(R.string.close, (dialog, which) -> showNotificationsPermissionDialog())
                    .show();

            getPrefs().edit().putBoolean(App.PREF_FIRST_TIME_LAUNCH, false).commit();
        } else {
            showNotificationsPermissionDialog();
        }
    }

    private void showNotificationsPermissionDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            Dexter.withContext(this)
                    .withPermission(Manifest.permission.POST_NOTIFICATIONS)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
//                            toast(R.string.notifications_permission_granted);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
//                            toast(R.string.notifications_permission_denied);
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(R.string.notifications_permission)
                                    .setMessage(R.string.notifications_permission_rationale_msg)
                                    .setPositiveButton(R.string.accept, (dialog, which) -> token.continuePermissionRequest())
                                    .setNegativeButton(R.string.cancel, (dialog, which) -> token.cancelPermissionRequest())
                                    .show();
                        }
                    }).check();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menuItemAllowLocation = menu.findItem(R.id.menu_allow_location);
        menuItemAllowLocation.setVisible(locationPermissionPermanentlyDenied);
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

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CityData.COORD_CENTER, CityData.INITIAL_ZOOM));

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

        if (SmartLocation.with(this).location().state().isAnyProviderAvailable()) {
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            if (menuItemAllowLocation != null) {
                                menuItemAllowLocation.setVisible(false);
                            }
                            configureSelfLocationMap();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (SmartLocation.with(MainActivity.this).location().state().isAnyProviderAvailable()) {
                                if (menuItemAllowLocation != null) {
                                    menuItemAllowLocation.setVisible(true);
                                }
                            }

                            locationPermissionPermanentlyDenied = response.isPermanentlyDenied();
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
    }


    @SuppressLint("MissingPermission")
    private void configureSelfLocationMap() {
        if (SmartLocation.with(this).location().state().isAnyProviderAvailable()) {
            map.setMyLocationEnabled(true);
        }
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

            case R.id.menu_allow_location:
                if (!locationPermissionPermanentlyDenied) {
                    checkLocationPermission();
                } else {
                    showAllowLocationSettingsDialog();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAllowLocationSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.allow_location)
                .setMessage(R.string.allow_location_message)
                .setPositiveButton(R.string.allow, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    settingsActivityResultLauncher.launch(intent);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
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
//                LatLngBounds latLngBoundsJerez = LatLngBounds.builder().include(JEREZ_NORTH_EAST).include(JEREZ_SOUTH_WEST).build();
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
            Log.e(TAG, "onBackPressed: ", e);
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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CityData.URL_YOUTUBE_VIDEO)));
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

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_info)
                .setMessage(R.string.app_info_text)
                .setPositiveButton(R.string.contact, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "julio@triskelapps.com", null));
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
            marker.setZIndex(2);
            return false;
        }
        return true;
    }

    public void selectBusStopMarker(int busStopCode) {
        if (mapDataController.hasBusLineSelected() && busStopCode != -1) {
            map.getUiSettings().setMapToolbarEnabled(false);
            Marker marker = mapDataController.getMarker(busStopCode);
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

        if (map == null) {
            return;
        }

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

            BitmapDescriptor busStopsIcon = getBusMarkerIcon(busLine.getColor());

            for (BusStop busStop : busLine.getBusStops()) {

                LatLng position = new LatLng(busStop.getCoordinates().get(0), busStop.getCoordinates().get(1));
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(position)
                        .visible(false)
                        .icon(busStopsIcon)
                        .title(busStop.getName() + (BuildConfig.DEBUG ? " - " + busStop.getCode() : "")));

                marker.setTag(busStop);

                markersBusStopsLine.add(marker);

            }

            List<Marker> markersArrowsLine = new ArrayList<>();
            int PATH_STEP = 7;
            BitmapDescriptor bitmapDescriptorLine = BitmapDescriptorFactory.fromBitmap(tintArrow(busLine.getColor()));
            for (int pathIndex = 0; pathIndex < busLine.getPath().size(); pathIndex++) {
                if (pathIndex % PATH_STEP == 0 && pathIndex + 1 < busLine.getPath().size()) {
                    List<Double> currentCoords = busLine.getPath().get(pathIndex);
                    List<Double> nextCoords = busLine.getPath().get(pathIndex + 1);
                    LatLng currentLatLng = new LatLng(currentCoords.get(0), currentCoords.get(1));
                    LatLng nextLatLng = new LatLng(nextCoords.get(0), nextCoords.get(1));
                    double rotation = SphericalUtil.computeHeading(currentLatLng, nextLatLng);

                    double middleLat = (currentCoords.get(0) + nextCoords.get(0)) / 2;
                    double middleLng = (currentCoords.get(1) + nextCoords.get(1)) / 2;
                    LatLng position = new LatLng(middleLat, middleLng);
                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(position)
                            .visible(false)
                            .rotation((float) rotation)
                            .icon(bitmapDescriptorLine));

                    markersArrowsLine.add(marker);
                }

            }

            mapDataController.addLineData(busLine.getId(), polylinePath, markersBusStopsLine, markersArrowsLine);

        }

        Log.i(TAG, "loadBusLines: finish");


    }

    private Bitmap tintArrow(int color) {

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

        Bitmap iconMarkerOutline = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_bus_marker_outline);
        Bitmap iconTinted = Bitmap.createBitmap(iconMarkerOutline.getWidth(), iconMarkerOutline.getHeight(), iconMarkerOutline.getConfig());
        Canvas canvas = new Canvas(iconTinted);
        canvas.drawBitmap(iconMarkerOutline, new Matrix(), null);

        Bitmap iconBg = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_bus_marker_background);
        Bitmap iconBgTinted = changeBitmapColor(iconBg, color);
        canvas.drawBitmap(iconBgTinted, new Matrix(), null);

        int icBusStopMarker = isDark(color) ? R.mipmap.ic_bus_marker_icon_white : R.mipmap.ic_bus_marker_icon_black;
        Bitmap busIcon = BitmapFactory.decodeResource(getResources(), icBusStopMarker);
        canvas.drawBitmap(busIcon, new Matrix(), null);

        return BitmapDescriptorFactory.fromBitmap(iconTinted);
    }

    boolean isDark(int color) {
        return ColorUtils.calculateLuminance(color) < 0.2;
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
    public void showBusLineInfo(BusLine busLine, BusStop busStop, boolean animateToBounds) {


        if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            binding.drawerLayout.closeDrawer(Gravity.RIGHT);
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            getSupportFragmentManager().popBackStack();
        }

        busStopsFragment = BusStopsFragment.newInstance(busLine, busStop);
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
                .position(place.getLocation())
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_green))
                .title(place.getDisplayName()));

        markerDestination.showInfoWindow();
        markerDestination.setZIndex(Integer.MAX_VALUE);

        map.animateCamera(CameraUpdateFactory.newLatLng(place.getLocation()));

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
