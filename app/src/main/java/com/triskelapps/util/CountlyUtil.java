package com.triskelapps.util;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.triskelapps.DebugHelper;
import com.triskelapps.R;
import com.triskelapps.model.BusStop;
import com.triskelapps.features.news.data.News;

import java.util.HashMap;
import java.util.Map;

import ly.count.android.sdk.Countly;
import ly.count.android.sdk.CountlyConfig;

public class CountlyUtil {

    private static final String TAG = "CountlyUtil";

    // For fist time execution (Countly keys are not fetched yet and first onStart() is not called)
    private static Activity pendingActivityTrackStart;

    private static boolean isAnalyticsEnabled() {
        return DebugHelper.SWITCH_RECORD_ANALYTICS && Countly.sharedInstance().isInitialized();
    }

    // SCREEN VIEWS

    public static void onStart(Activity activity) {
        if (isAnalyticsEnabled()) {
            Countly.sharedInstance().onStart(activity);
        } else {
            pendingActivityTrackStart = activity;
        }
    }

    public static void onStop() {
        if (isAnalyticsEnabled()) {
            Countly.sharedInstance().onStop();
        }
    }

    // EVENTS

    public static void recordEvent(String name) {
        if (isAnalyticsEnabled()) {
            Countly.sharedInstance().events().recordEvent(name);
        }

    }

    public static void selectBusLine(int lineId, String from) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", lineId);
            segmentation.put("from", from);
            Countly.sharedInstance().events().recordEvent("select_bus_line", segmentation);
        }
    }

    public static void showBusLine(int lineId) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", lineId);
            Countly.sharedInstance().events().recordEvent("show_bus_line", segmentation);
        }
    }

    public static void hideBusLine(int lineId) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", lineId);
            Countly.sharedInstance().events().recordEvent("hide_bus_line", segmentation);
        }
    }

    public static void selectBusStop(BusStop busStop) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %d - %s",
                    busStop.getLineId(), busStop.getCode(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("select_bus_stop", segmentation);
        }
    }

    public static void addFavouriteBusStop(BusStop busStop) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %d - %s",
                    busStop.getLineId(), busStop.getCode(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("add_favourite_bus_stop_ok", segmentation);
        }
    }

    public static void removeFavouriteBusStop(BusStop busStop) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %d - %s",
                    busStop.getLineId(), busStop.getCode(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("remove_favourite_bus_stop_ok", segmentation);
        }
    }

    public static void seeTimetable(BusStop busStop) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %d - %s",
                    busStop.getLineId(), busStop.getCode(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("see_timetable", segmentation);
        }
    }

    public static void seeTimetableFavourite(BusStop busStop) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %d - %s",
                    busStop.getLineId(), busStop.getCode(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("see_timetable_favourite", segmentation);
        }
    }

    public static void newsClick(News news) {

        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("title", news.getTitle());
            segmentation.put("link", news.getUrl());
            Countly.sharedInstance().events().recordEvent("news_click", segmentation);
        }
    }

    public static void busStopNotFound(int lineId, String name, String from) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", lineId);
            segmentation.put("bus_stop_info", String.format("L%d - %s", lineId, name));
            segmentation.put("from", from);
            Countly.sharedInstance().events().recordEvent("bus_stop_not_found", segmentation);
        }
    }

    public static void selectPlace(String name) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("name", name);
            Countly.sharedInstance().events().recordEvent("select_place", segmentation);
        }
    }

    public static void selectPlaceError(int statusCode, String statusMessage) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("error", String.format("Code %d - %s", statusCode, statusMessage));
            Countly.sharedInstance().events().recordEvent("select_place_error", segmentation);
        }
    }

    public static void bannerMessageClick(String link) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("link", link);
            Countly.sharedInstance().events().recordEvent("banner_message_click", segmentation);
        }
    }

    public static void appShareReminderDialogShown(int appStartCount) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("app_start_count", appStartCount);
            Countly.sharedInstance().events().recordEvent("app_share_reminder_dialog_shown", segmentation);
        }
    }


    public static void timetableError(BusStop busStop) {
        if (isAnalyticsEnabled()) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %s", busStop.getLineId(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("timetable_error", segmentation);
        }
    }




    // CONFIGURATIONS


    public static void configureCountly(Application app) {

        if (areKeysFetched()) {
            initCountly(app);
        } else {
            fetchRemoteKeysThenInit(app);
        }
    }

    private static boolean areKeysFetched() {
        String appKey = FirebaseRemoteConfig.getInstance().getString("countly_app_key");
        String serverUrl = FirebaseRemoteConfig.getInstance().getString("countly_server_url");

        return !TextUtils.isEmpty(appKey) && !TextUtils.isEmpty(serverUrl);
    }

    private static void initCountly(Application app) {

        String appKey = app.getString(R.string.countly_app_key);
        String serverUrl = app.getString(R.string.countly_server_url);

        CountlyConfig config = new CountlyConfig(app, appKey, serverUrl);
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            config.enableCrashReporting();
            config.setViewTracking(true);
            config.setAutoTrackingUseShortName(true);
        }
        Countly.sharedInstance().init(config);

        if (pendingActivityTrackStart != null) {
            onStart(pendingActivityTrackStart);
            pendingActivityTrackStart = null;
        }
    }

    private static void fetchRemoteKeysThenInit(Application app) {

        FirebaseRemoteConfig.getInstance().fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                initCountly(app);
            } else {
                Log.e(TAG, "remoteConfig error: ", task.getException());
            }
        });
    }

    public static void recordHandledException(Exception e) {
        if (isAnalyticsEnabled()) {
            Countly.sharedInstance().crashes().recordHandledException(e);
        }
    }

}
