package com.triskelapps.ui.favourites;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.triskelapps.App;
import com.triskelapps.R;
import com.triskelapps.base.BaseActivity;
import com.triskelapps.databinding.ActivityFavouritesBinding;
import com.triskelapps.ui.timetable.TimetableDialog;
import com.triskelapps.util.CountlyUtil;
import com.triskelapps.model.BusStop;
import com.triskelapps.util.Util;
import com.triskelapps.util.WindowUtils;

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

        if (!Util.isConnected(this)) {
            toast(R.string.no_connection);
            return;
        }

        BusStop busStop = favourites.get(position);
        TimetableDialog.createDialog(busStop).show(getSupportFragmentManager(), null);
        CountlyUtil.seeTimetableFavourite(busStop);

    }

    @Override
    public void onItemEditClick(int position) {

        final BusStop busStop = favourites.get(position);

        View layout = View.inflate(this, R.layout.view_dialog_edittext, null);

        final EditText editText = layout.findViewById(R.id.edit_text);
        editText.setText(busStop.getName());

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton(R.string.accept, (dialogInterface, which) -> {

                    busStop.setName(editText.getText().toString());

                    App.getDB().busStopDao().update(busStop);
                    favourites.set(position, busStop);
                    adapter.notifyDataSetChanged();

                })
                .setNeutralButton(R.string.cancel, null)
                .create();

        dialog.setOnDismissListener(dialogInterface -> WindowUtils.hideSoftKeyboard(this));

        dialog.show();


    }

    @Override
    public void onItemRemoveClick(int position) {
        App.getDB().busStopDao().delete(favourites.get(position));
        favourites.remove(position);
        adapter.notifyDataSetChanged();
    }
}
