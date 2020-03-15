package com.triskelapps.busjerez.ui.favourites;

import android.os.Bundle;
import android.view.View;

import com.triskelapps.busjerez.App;
import com.triskelapps.busjerez.base.BaseActivity;
import com.triskelapps.busjerez.databinding.ActivityFavouritesBinding;
import com.triskelapps.busjerez.model.BusStop;
import com.triskelapps.busjerez.ui.timetable.TimetableDialog;

import java.util.List;

public class FavouritesActivity extends BaseActivity implements FavouritesAdapter.OnItemClickListener {

    private ActivityFavouritesBinding binding;
    private List<BusStop> favourites;
    private FavouritesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureSecondLevelActivity();

        favourites = App.getDB().busStopDao().getAll();
        adapter = new FavouritesAdapter(this, favourites);
        adapter.setOnItemClickListener(this);
        binding.recyclerFavs.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {

        TimetableDialog.createDialog(favourites.get(position)).show(getSupportFragmentManager(), null);

    }
}
