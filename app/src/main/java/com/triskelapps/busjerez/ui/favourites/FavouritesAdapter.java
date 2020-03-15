package com.triskelapps.busjerez.ui.favourites;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.databinding.RowBusStopBinding;
import com.triskelapps.busjerez.databinding.RowFavouriteBusStopBinding;
import com.triskelapps.busjerez.model.BusStop;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {


    private List<BusStop> busStops;
    private Context context;
    private OnItemClickListener itemClickListener;


    public FavouritesAdapter(Context context, List<BusStop> busStops) {
        this.context = context;
        this.busStops = busStops;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View contactView = LayoutInflater.from(context).inflate(R.layout.row_favourite_bus_stop, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final BusStop busStop = getItemAtPosition(position);

        holder.binding.tvName.setText(context.getString(R.string.favourite_bus_stop_format, busStop.getLineId(), busStop.getName()));

    }

    @Override
    public int getItemCount() {
        return busStops.size();
    }

    public BusStop getItemAtPosition(int position) {
        return busStops.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final RowFavouriteBusStopBinding binding;

        public ViewHolder(View itemView) {

            super(itemView);

            binding = RowFavouriteBusStopBinding.bind(itemView);

            binding.getRoot().setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }

    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}


