package com.triskelapps.busjerez.ui.main.bus_stops;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.base.BaseInteractor;
import com.triskelapps.busjerez.base.BaseMainFragment;
import com.triskelapps.busjerez.databinding.FragmentBusStopsBinding;
import com.triskelapps.busjerez.interactor.TimetableInteractor;
import com.triskelapps.busjerez.model.BusLine;
import com.triskelapps.busjerez.model.BusStop;
import com.triskelapps.busjerez.ui.main.MainActivity;
import com.triskelapps.busjerez.ui.timetable.TimetableDialog;

import java.util.List;

public class BusStopsFragment extends BaseMainFragment implements BusStopsAdapter.OnItemClickListener, View.OnClickListener {
    private static final String ARG_BUS_LINE = "arg_bus_line";

    private BusLine busLine;
    private FragmentBusStopsBinding binding;
    private List<BusStop> busStops;
    private BusStopsAdapter adapter;
    private BusStop busStopSelected;

    public BusStopsFragment() {
        // Required empty public constructor
    }

    public static BusStopsFragment newInstance(BusLine busLine) {
        BusStopsFragment fragment = new BusStopsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BUS_LINE, busLine);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null || !getArguments().containsKey(ARG_BUS_LINE)) {
            throw new IllegalArgumentException("This fragment must receive a Bus Line argument");
        }

        busLine = (BusLine) getArguments().getSerializable(ARG_BUS_LINE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBusStopsBinding.inflate(getLayoutInflater(), container, false);

        binding.tvLineInfo.setText(getString(R.string.line_info_format, busLine.getId(), busLine.getDescription()));

        busStops = busLine.getBusStops();
        adapter = new BusStopsAdapter(getActivity(), busStops, busLine.getColor());
        adapter.setOnItemClickListener(this);
        binding.recyclerBusStops.setAdapter(adapter);

        binding.btnTimetable.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onItemClick(View view, int position) {

        ((MainActivity) getActivity()).selectBusStopMarker(position);

        showTimetableView(busStops.get(position));

    }


    public void selectBusStop(BusStop busStop) {
        int position = -1;
        for (int i = 0; i < busStops.size(); i++) {
            BusStop busStopItem = busStops.get(i);

            // TODO this should be replaced with bus stop code (peding review data)
            if (TextUtils.equals(busStopItem.getName(), busStop.getName())) {
                position = i;
                break;
            }
        }

        adapter.setSelectedPosition(position);

        binding.recyclerBusStops.smoothScrollToPosition(position);

        showTimetableView(busStop);
    }


    private void showTimetableView(BusStop busStop) {
        binding.viewTimetable.setVisibility(View.VISIBLE);
        binding.tvDirectionTransfer.setText(getString(
                R.string.bus_stop_direction_transfer_format, busStop.getDirection(), busStop.getTransfer()));
        busStopSelected = busStop;
    }

    public boolean hasBusStopSelected() {
        return adapter.getSelectedPosition() > -1;
    }

    public void clearBusStopSelection() {
        ((MainActivity) getActivity()).unselectBusStopMarker(adapter.getSelectedPosition());
        adapter.setSelectedPosition(-1);
        binding.viewTimetable.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_timetable:
                requestTimetable();
                break;
        }
    }

    private void requestTimetable() {

        if (busStopSelected.getCode() == -1) {
            toast(R.string.error_code_bus_stop);
        } else {
            showProgressDialog(getString(R.string.loading));
            new TimetableInteractor(getActivity(), this)
                    .getTimetable(busLine.getId(), busStopSelected.getCode(), new BaseInteractor.CallbackPost() {
                        @Override
                        public void onSuccess(String body) {
                            hideProgressDialog();
                            showDialogTimetable(body);
                        }

                        @Override
                        public void onError(String error) {
                            hideProgressDialog();
                            toast(error);
                        }
                    });

        }
    }

    private void showDialogTimetable(String body) {

        TimetableDialog.createDialog(busStopSelected, body).show(getChildFragmentManager(), null);

    }
}
