package com.triskelapps.busjerez.ui.timetable;

import android.content.Context;

import com.triskelapps.busjerez.R;

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

        festiveDaysMap.put("01/01/2020", context.getString(R.string.festive_1_new_year)); // nacional
        festiveDaysMap.put("06/01/2020", context.getString(R.string.festive_2_magic_kings)); // nacional
        festiveDaysMap.put("28/02/2020", context.getString(R.string.festive_3_andalucia_day)); // regional
        festiveDaysMap.put("09/04/2020", context.getString(R.string.festive_4_jueves_santo)); // regional
        festiveDaysMap.put("10/04/2020", context.getString(R.string.festive_5_viernes_santo)); // nacional
        festiveDaysMap.put("01/05/2020", context.getString(R.string.festive_6_worker_day)); // nacional
        festiveDaysMap.put("11/05/2020", context.getString(R.string.festive_7_feria_caballo)); // local
        festiveDaysMap.put("15/08/2020", context.getString(R.string.festive_8_asuncion_virgen)); // regional
        festiveDaysMap.put("24/09/2020", context.getString(R.string.festive_9_virgen_merced)); // local
        festiveDaysMap.put("12/10/2020", context.getString(R.string.festive_10_nacional_day)); // nacional
        festiveDaysMap.put("02/11/2020", context.getString(R.string.festive_11_all_saints) + " " + context.getString(R.string.moved)); // nacional
        festiveDaysMap.put("07/12/2020", context.getString(R.string.festive_12_constitution_day) + " " + context.getString(R.string.moved)); // nacional
        festiveDaysMap.put("08/12/2020", context.getString(R.string.festive_13_inmaculada_day)); // nacional
        festiveDaysMap.put("25/12/2020", context.getString(R.string.festive_14_christmas)); // nacional


        festiveDaysMap.put("01/01/2021", context.getString(R.string.festive_1_new_year)); // nacional
        festiveDaysMap.put("06/01/2021", context.getString(R.string.festive_2_magic_kings)); // nacional
        festiveDaysMap.put("01/03/2021", context.getString(R.string.festive_3_andalucia_day) + " " + context.getString(R.string.moved)); // regional
        festiveDaysMap.put("01/04/2021", context.getString(R.string.festive_4_jueves_santo)); // regional
        festiveDaysMap.put("02/04/2021", context.getString(R.string.festive_5_viernes_santo)); // nacional
        festiveDaysMap.put("01/05/2021", context.getString(R.string.festive_6_worker_day)); // nacional
        festiveDaysMap.put("11/05/2021", context.getString(R.string.festive_7_feria_caballo)); // local
        festiveDaysMap.put("16/08/2021", context.getString(R.string.festive_8_asuncion_virgen) + " " + context.getString(R.string.moved)); // regional
        festiveDaysMap.put("24/09/2021", context.getString(R.string.festive_9_virgen_merced)); // local
        festiveDaysMap.put("12/10/2021", context.getString(R.string.festive_10_nacional_day)); // nacional
        festiveDaysMap.put("01/11/2021", context.getString(R.string.festive_11_all_saints)); // nacional
        festiveDaysMap.put("06/12/2021", context.getString(R.string.festive_12_constitution_day)); // nacional
        festiveDaysMap.put("08/12/2021", context.getString(R.string.festive_13_inmaculada_day)); // nacional
        festiveDaysMap.put("25/12/2021", context.getString(R.string.festive_14_christmas)); // nacional

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
