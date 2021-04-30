package com.triskelapps.ui.main.bus_stops;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.triskelapps.App;
import com.triskelapps.R;
import com.triskelapps.base.BaseMainFragment;
import com.triskelapps.databinding.FragmentBusStopsBinding;
import com.triskelapps.model.BusLine;
import com.triskelapps.model.BusStop;
import com.triskelapps.ui.main.MainActivity;
import com.triskelapps.ui.timetable.TimetableDialog;
import com.triskelapps.util.CountlyUtil;

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

        binding.btnTimetable.setOnClickListener(this);
        binding.imgFavourite.setOnClickListener(this);
        
        binding.tvLineInfo.setText(getString(R.string.line_info_format, busLine.getId(), busLine.getDescription()));

        busStops = busLine.getBusStops();
        adapter = new BusStopsAdapter(getActivity(), busStops, busLine.getColor());
        adapter.setOnItemClickListener(this);
        binding.recyclerBusStops.setAdapter(adapter);
        

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (busStopSelected != null) {
            showBusStopInfoView(busStopSelected);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

        ((MainActivity) getActivity()).selectBusStopMarker(position);

        BusStop busStop = busStops.get(position);

        Log.i(TAG, "onBusStopClick: code: " + busStop.getCode() + ". Name: " + busStop.getName());

        CountlyUtil.selectBusStop(busStop);

        showBusStopInfoView(busStops.get(position));

    }


    public void selectBusStop(BusStop busStop) {

        if (!busStop.hasValidCode()) {
            toast(R.string.error_code_bus_stop);
            CountlyUtil.busStopNotFound(busStop.getLineId(), busStop.getName(), "marker_click");
            return;
        }

        CountlyUtil.selectBusStop(busStop);

        Log.i(TAG, "onBusStopClick: code: " + busStop.getCode() + ". Name: " + busStop.getName());

        int position = -1;
        for (int i = 0; i < busStops.size(); i++) {
            BusStop busStopItem = busStops.get(i);

            if (busStop.getCode() == busStopItem.getCode()
                    && TextUtils.equals(busStop.getName(), busStopItem.getName())) {
                position = i;
                break;
            }
        }

        if (position == -1) {
            CountlyUtil.recordHandledException(new IllegalArgumentException(
                    "selectBusStop. not found: " + busStop.getName() + ", code: " + busStop.getCode()));
            return;
        }

        adapter.setSelectedPosition(position);

        binding.recyclerBusStops.smoothScrollToPosition(position);

        showBusStopInfoView(busStop);
    }


    private void showBusStopInfoView(BusStop busStop) {
        binding.viewBusStopInfo.setVisibility(View.VISIBLE);
        binding.imgFavourite.setVisibility(View.VISIBLE);

        binding.tvDirectionTransfer.setVisibility(busStop.hasExtraInfo() ? View.VISIBLE : View.GONE);
        binding.tvDirectionTransfer.setText(busStop.getExtraInfo(getActivity()));

        boolean isFavourite = App.getDB().busStopDao().getBusBusStop(busStop.getCode(), busStop.getLineId()) != null;
        binding.imgFavourite.setSelected(isFavourite);
        
        busStopSelected = busStop;
    }

    public boolean hasBusStopSelected() {
        return adapter.getSelectedPosition() > -1;
    }

    public void clearBusStopSelection() {
        ((MainActivity) getActivity()).unselectBusStopMarker(adapter.getSelectedPosition());
        adapter.setSelectedPosition(-1);
        binding.viewBusStopInfo.setVisibility(View.GONE);
        binding.imgFavourite.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_timetable:
                requestTimetable();
                break;

            case R.id.img_favourite:
                addRemoveFavourite();
                break;
        }
    }

    private void addRemoveFavourite() {

        if (!busStopSelected.hasValidCode()) {
            toast(R.string.error_code_bus_stop);
            CountlyUtil.busStopNotFound(busStopSelected.getLineId(), busStopSelected.getName(), "add_favourite");
        } else {
            if (binding.imgFavourite.isSelected()) {
                App.getDB().busStopDao().delete(busStopSelected);
                binding.imgFavourite.setSelected(false);
                CountlyUtil.removeFavouriteBusStop(busStopSelected);
            } else {
                App.getDB().busStopDao().insert(busStopSelected);
                binding.imgFavourite.setSelected(true);
                CountlyUtil.addFavouriteBusStop(busStopSelected);
            }
        }

    }

    private void requestTimetable() {

        if (!busStopSelected.hasValidCode()) {
            toast(R.string.error_code_bus_stop);
            CountlyUtil.busStopNotFound(busStopSelected.getLineId(), busStopSelected.getName(), "request_timetable");
        } else {
            TimetableDialog.createDialog(busStopSelected).show(getChildFragmentManager(), null);
            CountlyUtil.seeTimetable(busStopSelected);
        }
    }

}
