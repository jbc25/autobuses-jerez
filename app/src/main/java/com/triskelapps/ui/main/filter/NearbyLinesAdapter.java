package com.triskelapps.ui.main.filter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.triskelapps.R;
import com.triskelapps.databinding.RowNearbyLineBinding;
import com.triskelapps.model.NearbyLine;

import java.util.List;
import java.util.Locale;

public class NearbyLinesAdapter extends RecyclerView.Adapter<NearbyLinesAdapter.ViewHolder> {


    private List<NearbyLine> nearbyLines;
    private Context context;
    private OnItemClickListener itemClickListener;


    public NearbyLinesAdapter(Context context, List<NearbyLine> nearbyLines) {
        this.context = context;
        this.nearbyLines = nearbyLines;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View contactView = LayoutInflater.from(context).inflate(R.layout.row_nearby_line, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final NearbyLine nearbyLine = getItemAtPosition(position);

        holder.binding.tvLineId.setText(String.valueOf(nearbyLine.getBusLine().getId()));
        holder.binding.tvLineId.getBackground().setColorFilter(
                nearbyLine.getBusLine().getColor(), PorterDuff.Mode.SRC_ATOP);
        holder.binding.tvNearbyDistance.setText(
                String.format(Locale.getDefault(), "%dm", (int) nearbyLine.getDistance()));

    }

    @Override
    public int getItemCount() {
        return nearbyLines.size();
    }

    public NearbyLine getItemAtPosition(int position) {
        return nearbyLines.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final RowNearbyLineBinding binding;

        public ViewHolder(View itemView) {

            super(itemView);

            binding = RowNearbyLineBinding.bind(itemView);

            binding.getRoot().setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(nearbyLines.get(getAdapterPosition()));
                }
            });
        }

    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(NearbyLine nearbyLine);
    }
}


