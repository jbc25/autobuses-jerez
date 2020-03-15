package com.triskelapps.busjerez.ui.timetable;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.databinding.DialogTimetableBinding;
import com.triskelapps.busjerez.model.BusStop;
import com.triskelapps.busjerez.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimetableDialog extends DialogFragment implements WebView.FindListener {

    private static final String ARG_BUS_STOP = "arg_bus_stop";
    private static final String ARG_INFO_HTML = "arg_info_html";
    private DialogTimetableBinding binding;
    private BusStop busStop;
    private String infoHtml;

    private Map<String, String> festiveDaysMap = new HashMap<>();

    public static TimetableDialog createDialog(BusStop busStop, String infoHtml) {
        TimetableDialog timetableDialog = new TimetableDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BUS_STOP, busStop);
        args.putString(ARG_INFO_HTML, infoHtml);
        timetableDialog.setArguments(args);
        return timetableDialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busStop = (BusStop) getArguments().getSerializable(ARG_BUS_STOP);
        infoHtml = getArguments().getString(ARG_INFO_HTML);

        binding = DialogTimetableBinding.inflate(getActivity().getLayoutInflater());

        initializeFestiveDays();

        binding.webviewTimetable.setFindListener(this);
    }

    private void initializeFestiveDays() {

        festiveDaysMap.put("01/01", getString(R.string.festive_1_new_year));
        festiveDaysMap.put("06/01", getString(R.string.festive_2_magic_kings));
        festiveDaysMap.put("28/02", getString(R.string.festive_3_andalucia_day));
        festiveDaysMap.put("09/04", getString(R.string.festive_4_jueves_santo));
        festiveDaysMap.put("10/04", getString(R.string.festive_5_viernes_santo));
        festiveDaysMap.put("01/05", getString(R.string.festive_6_worker_day));
        festiveDaysMap.put("11/05", getString(R.string.festive_7_feria_caballo));
        festiveDaysMap.put("15/08", getString(R.string.festive_8_asuncion_virgen));

        festiveDaysMap.put("24/09", getString(R.string.festive_9_virgen_merced));
        festiveDaysMap.put("12/10", getString(R.string.festive_10_nacional_day));
        festiveDaysMap.put("02/11", getString(R.string.festive_11_all_saints));
        festiveDaysMap.put("07/12", getString(R.string.festive_12_constitution_day));
        festiveDaysMap.put("08/12", getString(R.string.festive_13_inmaculada_day));
        festiveDaysMap.put("25/12", getString(R.string.festive_14_christmas));

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInfoText();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        refreshInfoText();
        binding.webviewTimetable.loadDataWithBaseURL(null, infoHtml, "text/html", "utf-8", null);

        return new AlertDialog.Builder(getActivity())
                .setView(binding.getRoot())
                .setNegativeButton(R.string.back, (dialog, which) -> dismiss())
                .create();
    }

    private void refreshInfoText() {

        String time = DateUtils.formatTime.format(new Date());
        String dayType = getDayType();
        String infoText = getString(R.string.info_timetable_format, busStop.getLineId(), busStop.getName(), dayType, time);
        binding.tvTimetableInfo.setText(infoText);

        String dayTypeWebName = convertDayType(dayType);
        binding.webviewTimetable.findAllAsync(dayTypeWebName);
    }

    private String convertDayType(String dayType) {
        if (TextUtils.equals(dayType, getString(R.string.sunday)) || dayType.contains(getString(R.string.festive))) {
            return "DOMINGOS Y FESTIVOS";
        } else if (TextUtils.equals(dayType, getString(R.string.saturday))) {
            return "SÁBADOS";
        } else {
            return "LABORABLES";
        }
    }

    private String getDayType() {

        String dayMonth = new SimpleDateFormat("dd/MM").format(new Date());
        int dayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (festiveDaysMap.containsKey(dayMonth)) {
            return getString(R.string.festive) + ". " + festiveDaysMap.get(dayMonth);
        } else if (dayWeek == Calendar.SUNDAY) {
            return getString(R.string.sunday);
        } else if (dayWeek == Calendar.SATURDAY) {
            return getString(R.string.saturday);
        } else {
            return getString(R.string.laboral);
        }
    }



    @Override
    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        if (isDoneCounting) {
            binding.webviewTimetable.findNext(true);
        }

    }

}
