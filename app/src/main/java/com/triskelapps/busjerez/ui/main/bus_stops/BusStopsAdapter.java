package com.triskelapps.busjerez.ui.main.bus_stops;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.databinding.RowBusStopBinding;
import com.triskelapps.busjerez.model.BusStop;

import java.util.List;

public class BusStopsAdapter extends RecyclerView.Adapter<BusStopsAdapter.ViewHolder> {


    private final int busLineColor;
    private int selectedPosition = -1;
    private List<BusStop> busStops;
    private Context context;
    private OnItemClickListener itemClickListener;


    public BusStopsAdapter(Context context, List<BusStop> busStops, int busLineColor) {
        this.context = context;
        this.busStops = busStops;
        this.busLineColor = busLineColor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View contactView = LayoutInflater.from(context).inflate(R.layout.row_bus_stop, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final BusStop busStop = getItemAtPosition(position);

        holder.binding.tvName.setText(busStop.getName());
        holder.binding.imgBusStop.setColorFilter(busLineColor);
        holder.binding.viewPointBusStop.setColor(busLineColor);
        holder.binding.viewPointBusStop.configureStartEndPoint(position, busStops.size());
        holder.binding.getRoot().setSelected(selectedPosition == position);

    }

    @Override
    public int getItemCount() {
        return busStops.size();
    }

    public BusStop getItemAtPosition(int position) {
        return busStops.get(position);
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final RowBusStopBinding binding;

        public ViewHolder(View itemView) {

            super(itemView);

            binding = RowBusStopBinding.bind(itemView);

            binding.getRoot().setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                    selectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
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


