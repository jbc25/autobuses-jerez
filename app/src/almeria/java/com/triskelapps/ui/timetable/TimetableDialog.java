package com.triskelapps.ui.timetable;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.triskelapps.R;
import com.triskelapps.databinding.DialogTimetableBinding;
import com.triskelapps.model.BusStop;
import com.triskelapps.util.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class TimetableDialog extends DialogFragment {

    private static final String ARG_BUS_STOP = "arg_bus_stop";
    private static final String TAG = "TimetableDialog";

    private static final String URL_WAIT_TIME = "https://www.surbusalmeria.es/API/GetWaitTime?l=%s&bs=%s";

    private DialogTimetableBinding binding;
    private BusStop busStop;
    private String dayType;

    public static TimetableDialog createDialog(BusStop busStop) {
        TimetableDialog timetableDialog = new TimetableDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BUS_STOP, busStop);
        timetableDialog.setArguments(args);
        return timetableDialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busStop = (BusStop) getArguments().getSerializable(ARG_BUS_STOP);

        binding = DialogTimetableBinding.inflate(getActivity().getLayoutInflater());

        showProgressBar();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        String lineWaitTimeCode = busStop.getWaitTimeCodeLine();
        String busStopWaitTimeCode = busStop.getWaitTimeCode();

        String url = String.format(URL_WAIT_TIME, lineWaitTimeCode, busStopWaitTimeCode);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        response -> {
                            if (getActivity() == null) {
                                return;
                            }
                            hideProgressBar();
                            processResponseJson(response);
                        },
                        error -> {
                            if (getActivity() == null) {
                                return;
                            }
                            hideProgressBar();
//                            CountlyUtil.recordHandledException(error);
                            Toasty.error(getActivity(), R.string.error_loading_timetable).show();
                            error.printStackTrace();
                        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Referer", "https://www.surbusalmeria.es/tiempos-de-espera/");
                headers.put("Host", "www.surbusalmeria.es");
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Cookie", "ASP.NET_SessionId=z1ejax22qbz2banelw0jyp5b");
                return headers;
            }
        };

        jsonObjectRequest.setShouldCache(false);

        queue.add(jsonObjectRequest);

    }

    private void processResponseJson(JSONObject response) {

        String waitTime1 = null;
        String waitTime2 = null;
        try {
            waitTime1 = response.getString("waitTimeString");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObjDebugInfo0 = response.getJSONArray("debugInfo").getJSONObject(0);
            int seconds = jsonObjDebugInfo0.getJSONObject("vh_second").getInt("seconds");

            waitTime2 = getString(R.string.minutes_x, TimeUnit.SECONDS.toMinutes(seconds));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showWaitTimes(waitTime1, waitTime2);
    }

    private void showWaitTimes(String waitTime1, String waitTime2) {
        binding.tvWaitTime1.setText(waitTime1);
        binding.tvWaitTime2.setText(waitTime2);
    }

    private void showProgressBar() {
        binding.progressbar.setVisibility(View.VISIBLE);
        binding.viewWaitTime.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        binding.progressbar.setVisibility(View.GONE);
        binding.viewWaitTime.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
        showHeaderInfo();
    }

    private void showHeaderInfo() {

        String time = DateUtils.formatTime.format(new Date());
        dayType = DayTypeUtil.with(getContext()).getDayType();
        String infoText = getString(R.string.info_timetable_format, busStop.getLineId(), busStop.getName(), dayType, time);
        binding.tvTimetableInfo.setText(infoText);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setView(binding.getRoot())
                .setNegativeButton(R.string.back, (dialog, which) -> dismiss())
                .create();
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


}
