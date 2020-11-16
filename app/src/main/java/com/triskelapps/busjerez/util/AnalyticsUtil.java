package com.triskelapps.busjerez.util;

import android.provider.Settings;

import com.triskelapps.busjerez.DebugHelper;
import com.triskelapps.busjerez.model.BusStop;

import java.util.HashMap;
import java.util.Map;

import ly.count.android.sdk.BuildConfig;
import ly.count.android.sdk.Countly;

public class AnalyticsUtil {

    public static void recordEvent(String name) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Countly.sharedInstance().events().recordEvent(name);
        }

    }

    public static void selectBusLine(int lineId, String from) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", lineId);
            segmentation.put("from", from);
            Countly.sharedInstance().events().recordEvent("select_bus_line", segmentation);
        }
    }

    public static void showBusLine(int lineId) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", lineId);
            Countly.sharedInstance().events().recordEvent("show_bus_line", segmentation);
        }
    }

    public static void hideBusLine(int lineId) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", lineId);
            Countly.sharedInstance().events().recordEvent("hide_bus_line", segmentation);
        }
    }

    public static void selectBusStop(BusStop busStop) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %d - %s",
                    busStop.getLineId(), busStop.getCode(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("select_bus_stop", segmentation);
        }
    }

    public static void addFavouriteBusStop(BusStop busStop) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %d - %s",
                    busStop.getLineId(), busStop.getCode(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("add_favourite_bus_stop", segmentation);
        }
    }

    public static void removeFavouriteBusStop(BusStop busStop) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %d - %s",
                    busStop.getLineId(), busStop.getCode(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("remove_favourite_bus_stop", segmentation);
        }
    }

    public static void seeTimetable(BusStop busStop) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %d - %s",
                    busStop.getLineId(), busStop.getCode(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("see_timetable", segmentation);
        }
    }

    public static void seeTimetableFavourite(BusStop busStop) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", busStop.getLineId());
            segmentation.put("bus_stop_info", String.format("L%d - %d - %s",
                    busStop.getLineId(), busStop.getCode(), busStop.getName()));
            Countly.sharedInstance().events().recordEvent("see_timetable_favourite", segmentation);
        }
    }

    public static void busStopNotFound(int lineId, String name, String from) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("line_id", lineId);
            segmentation.put("bus_stop_info", String.format("L%d - %s", lineId, name));
            segmentation.put("from", from);
            Countly.sharedInstance().events().recordEvent("bus_stop_not_found", segmentation);
        }
    }

    public static void selectPlace(String name) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("name", name);
            Countly.sharedInstance().events().recordEvent("select_place", segmentation);
        }
    }

    public static void selectPlaceError(int statusCode, String statusMessage) {
        if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
            Map<String, Object> segmentation = new HashMap<>();
            segmentation.put("error", String.format("Code %d - %s", statusCode, statusMessage));
            Countly.sharedInstance().events().recordEvent("select_place_error", segmentation);
        }
    }
}
