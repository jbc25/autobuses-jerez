package com.triskelapps.ui.timetable;

import android.content.Context;

import com.triskelapps.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DayTypeUtil {


    private static Map<String, String> festiveDaysMap = new HashMap<>();
    private final Context context;

    public DayTypeUtil(Context context) {
        this.context = context;
        initializeFestiveDays();
    }

    public static DayTypeUtil with(Context context) {
        return new DayTypeUtil(context);
    }

    public void initializeFestiveDays() {
        festiveDaysMap = FestiveDays.get(context);
    }

    public String getDayType() {

        String dayMonth = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        int dayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (festiveDaysMap.containsKey(dayMonth)) {
            return context.getString(R.string.festive) + ". " + festiveDaysMap.get(dayMonth);
        } else if (dayWeek == Calendar.SUNDAY) {
            return context.getString(R.string.sunday);
        } else if (dayWeek == Calendar.SATURDAY) {
            return context.getString(R.string.saturday);
        } else {
            return context.getString(R.string.laboral);
        }
    }
}
