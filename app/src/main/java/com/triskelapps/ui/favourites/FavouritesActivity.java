package com.triskelapps.ui.favourites;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;

import com.triskelapps.App;
import com.triskelapps.base.BaseActivity;
import com.triskelapps.busjerez.databinding.ActivityFavouritesBinding;
import com.triskelapps.model.BusStop;
import com.triskelapps.ui.timetable.TimetableDialog;
import com.triskelapps.util.CountlyUtil;

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

        binding.recyclerFavs.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        favourites = App.getDB().busStopDao().getAll();
        adapter = new FavouritesAdapter(this, favourites);
        adapter.setOnItemClickListener(this);
        binding.recyclerFavs.setAdapter(adapter);

        binding.viewEmptyList.setVisibility(favourites.isEmpty() ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onItemClick(View view, int position) {

        BusStop busStop = favourites.get(position);
        TimetableDialog.createDialog(busStop).show(getSupportFragmentManager(), null);
        CountlyUtil.seeTimetableFavourite(busStop);

    }

    @Override
    public void onItemRemoveClick(int position) {
        App.getDB().busStopDao().delete(favourites.get(position));
        favourites.remove(position);
        adapter.notifyDataSetChanged();
    }
}
