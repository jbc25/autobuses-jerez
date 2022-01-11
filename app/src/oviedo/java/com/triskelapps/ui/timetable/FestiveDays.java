package com.triskelapps.ui.timetable;

import android.content.Context;

import com.triskelapps.R;

import java.util.HashMap;
import java.util.Map;

public class FestiveDays {

    public static Map<String, String> get(Context context) {

        Map<String, String> festiveDaysMap = new HashMap<>();

        festiveDaysMap.put("01/01/2021", context.getString(R.string.festive_1_new_year)); // nacional
        festiveDaysMap.put("06/01/2021", context.getString(R.string.festive_2_magic_kings)); // nacional
        festiveDaysMap.put("01/03/2021", context.getString(R.string.festive_3_andalucia_day) + " " + context.getString(R.string.moved)); // regional
        festiveDaysMap.put("01/04/2021", context.getString(R.string.festive_4_jueves_santo)); // regional
        festiveDaysMap.put("02/04/2021", context.getString(R.string.festive_5_viernes_santo)); // nacional
        festiveDaysMap.put("01/05/2021", context.getString(R.string.festive_6_worker_day)); // nacional

        festiveDaysMap.put("24/06/2021", context.getString(R.string.festive_7_san_juan)); // local

        festiveDaysMap.put("16/08/2021", context.getString(R.string.festive_8_asuncion_virgen) + " " + context.getString(R.string.moved)); // regional

        festiveDaysMap.put("27/08/2021", context.getString(R.string.festive_9_virgen_mar)); // local

        festiveDaysMap.put("12/10/2021", context.getString(R.string.festive_10_nacional_day)); // nacional
        festiveDaysMap.put("01/11/2021", context.getString(R.string.festive_11_all_saints)); // nacional
        festiveDaysMap.put("06/12/2021", context.getString(R.string.festive_12_constitution_day)); // nacional
        festiveDaysMap.put("08/12/2021", context.getString(R.string.festive_13_inmaculada_day)); // nacional
        festiveDaysMap.put("25/12/2021", context.getString(R.string.festive_14_christmas)); // nacional

        return festiveDaysMap;
    }
}
