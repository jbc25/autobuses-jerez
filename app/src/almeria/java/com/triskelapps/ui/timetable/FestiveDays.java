package com.triskelapps.ui.timetable;

import android.content.Context;

import com.triskelapps.R;

import java.util.HashMap;
import java.util.Map;

public class FestiveDays {

    public static Map<String, String> get(Context context) {

        Map<String, String> festiveDaysMap = new HashMap<>();

        festiveDaysMap.put("02/01/2023", context.getString(R.string.festive_1_new_year) + " " + context.getString(R.string.moved)); // nacional
        festiveDaysMap.put("06/01/2023", context.getString(R.string.festive_2_magic_kings)); // nacional
        festiveDaysMap.put("28/02/2023", context.getString(R.string.festive_3_andalucia_day)); // regional
        festiveDaysMap.put("06/04/2023", context.getString(R.string.festive_4_jueves_santo)); // regional
        festiveDaysMap.put("07/04/2023", context.getString(R.string.festive_5_viernes_santo)); // nacional
        festiveDaysMap.put("01/05/2023", context.getString(R.string.festive_6_worker_day)); // nacional

        festiveDaysMap.put("08/05/2023", context.getString(R.string.festive_7_feria_caballo)); // local

        festiveDaysMap.put("15/08/2023", context.getString(R.string.festive_8_asuncion_virgen)); // regional

        festiveDaysMap.put("25/09/2023", context.getString(R.string.festive_9_virgen_merced) + " " + context.getString(R.string.moved)); // local

        festiveDaysMap.put("12/10/2023", context.getString(R.string.festive_10_nacional_day)); // nacional
        festiveDaysMap.put("01/11/2023", context.getString(R.string.festive_11_all_saints)); // nacional
        festiveDaysMap.put("06/12/2023", context.getString(R.string.festive_12_constitution_day)); // nacional
        festiveDaysMap.put("08/12/2023", context.getString(R.string.festive_13_inmaculada_day)); // nacional
        festiveDaysMap.put("25/12/2023", context.getString(R.string.festive_14_christmas)); // nacional

        return festiveDaysMap;
    }
}
