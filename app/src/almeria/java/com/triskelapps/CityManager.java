package com.triskelapps;

import android.content.Context;

import com.triskelapps.model.BusLine;
import com.triskelapps.model.BusStop;

import java.util.List;

public class CityManager {

    public static void processBusLinesData(List<BusLine> busLines) {

        for (BusLine busLine : busLines) {
            for (BusStop busStop : busLine.getBusStops()) {
                busStop.addExtra("waitTimeCodeLine", busLine.getExtra("waitTimeCode"));
            }
        }

    }

    public static boolean hasBusStopExtraInfo() {
        return false;
    }

    public static String getBusStopExtraInfo(Context context, BusStop busStop) {
        return null;
    }
}
