package com.triskelapps.ui.timetable;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.triskelapps.busjerez.R;
import com.triskelapps.base.BaseActivity;
import com.triskelapps.base.BaseInteractor;
import com.triskelapps.busjerez.databinding.DialogTimetableBinding;
import com.triskelapps.interactor.TimetableInteractor;
import com.triskelapps.model.BusStop;
import com.triskelapps.util.CountlyUtil;
import com.triskelapps.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimetableDialog extends DialogFragment implements WebView.FindListener {

    private static final String ARG_BUS_STOP = "arg_bus_stop";
    private static final String TAG = "TimetableDialog";
    private DialogTimetableBinding binding;
    private BusStop busStop;
    private String infoHtml;
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

        binding.webviewTimetable.setFindListener(this);

        showProgressBar();
        new TimetableInteractor(getActivity(), null)
                .getTimetable(busStop.getLineId(), busStop.getCode(), new BaseInteractor.CallbackPost() {
                    @Override
                    public void onSuccess(String body) {
                        if (getActivity() == null) {
                            return;
                        }
                        infoHtml = body;
                        loadTimetableHtml();
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() == null) {
                            return;
                        }
                        hideProgressBar();
                        ((BaseActivity) getActivity()).toast(getString(R.string.error_loading_timetable));

                        CountlyUtil.timetableError(busStop);
                    }
                });
    }

    private void showProgressBar() {
        binding.progressbarWebview.setVisibility(View.VISIBLE);
        binding.webviewTimetable.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        binding.progressbarWebview.setVisibility(View.GONE);
        binding.webviewTimetable.setVisibility(View.VISIBLE);
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

    private void loadTimetableHtml() {

        if (getContext() != null && infoHtml != null) {

            binding.webviewTimetable.loadDataWithBaseURL(null, infoHtml, "text/html", "utf-8", null);

            binding.webviewTimetable.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    if (getActivity() == null) {
                        CountlyUtil.recordHandledException(new Exception("Error timetable No Context"));
                        return;
                    }

                    hideProgressBar();

                    // Workaround to make this work in Android 9
                    new Handler().postDelayed(() -> {

                        if (getActivity() == null) {
                            CountlyUtil.recordHandledException(new Exception("Error timetable No Context"));
                            return;
                        }

                        if (dayType != null) {
                            String dayTypeWebName = convertDayType(dayType);
                            binding.webviewTimetable.findAllAsync(dayTypeWebName);
                        }
                    }, 50);

                }


            });
        }
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


    @Override
    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        if (isDoneCounting && numberOfMatches > 0) {
            binding.webviewTimetable.findNext(true);
            binding.webviewTimetable.clearMatches();
        }

    }

}
