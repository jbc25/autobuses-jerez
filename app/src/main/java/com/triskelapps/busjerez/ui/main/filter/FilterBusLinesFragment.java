package com.triskelapps.busjerez.ui.main.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.triskelapps.busjerez.App;
import com.triskelapps.busjerez.base.BaseMainFragment;
import com.triskelapps.busjerez.databinding.FragmentFilterBusLinesBinding;
import com.triskelapps.busjerez.model.BusLine;

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
        getMainPresenter().onBusLineCheckedChanged(position, checked);
    }

    @Override
    public void onBusStopButtonClick(int position) {
        getMainPresenter().onBusLinePathClick(busLines.get(position).getId());
    }
}
