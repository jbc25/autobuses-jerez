package com.triskelapps;

import android.content.Context;

import com.triskelapps.model.BusLine;
import com.triskelapps.model.BusStop;

import java.util.List;

public class CityManager {

    public static void processBusLinesData(List<BusLine> busLines) {

    }

    public static boolean hasBusStopExtraInfo() {
        return true;
    }

    public static String getBusStopExtraInfo(Context context, BusStop busStop) {
        return context.getString(
                R.string.bus_stop_direction_transfer_format, busStop.getDirection(), busStop.getTransfer());
    }
}
