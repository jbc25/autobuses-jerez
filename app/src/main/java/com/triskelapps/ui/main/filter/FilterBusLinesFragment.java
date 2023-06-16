package com.triskelapps.ui.main.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.triskelapps.App;
import com.triskelapps.base.BaseMainFragment;
import com.triskelapps.databinding.FragmentFilterBusLinesBinding;
import com.triskelapps.model.BusLine;
import com.triskelapps.model.NearbyLine;
import com.triskelapps.util.CountlyUtil;

import java.util.ArrayList;
import java.util.List;


public class FilterBusLinesFragment extends BaseMainFragment implements FilterBusLinesAdapter.OnItemClickListener {

    private FragmentFilterBusLinesBinding binding;
    private List<BusLine> busLines;
    private FilterBusLinesAdapter adapter;


    public FilterBusLinesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFilterBusLinesBinding.inflate(getLayoutInflater(), container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        busLines = App.getBusLinesData(getActivity());
        adapter = new FilterBusLinesAdapter(getActivity(), busLines);
        adapter.setOnItemClickListener(this);
        binding.recyclerBusLines.setAdapter(adapter);

    }

    @Override
    public void onBusLineCheckedChanged(int position, boolean checked) {
        busLines.get(position).setVisible(checked);
        getMainPresenter().onBusLineCheckedChanged(position, checked);
        if (checked) {
            CountlyUtil.showBusLine(busLines.get(position).getId());
        } else {
            CountlyUtil.hideBusLine(busLines.get(position).getId());
        }
    }

    @Override
    public void onBusStopButtonClick(int position) {
        BusLine busLine = busLines.get(position);
        getMainPresenter().onBusLinePathClick(busLine.getId(), true);
        CountlyUtil.selectBusLine(busLine.getId(), "filter_panel");
    }
}
