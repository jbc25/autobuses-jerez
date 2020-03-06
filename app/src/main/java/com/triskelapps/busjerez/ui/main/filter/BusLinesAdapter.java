package com.triskelapps.busjerez.ui.main.filter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.databinding.RowBusLineBinding;
import com.triskelapps.busjerez.model.BusLine;

import java.util.List;

public class BusLinesAdapter extends RecyclerView.Adapter<BusLinesAdapter.ViewHolder> {


    private List<BusLine> busLines;
    private Context context;
    private OnItemClickListener itemClickListener;


    public BusLinesAdapter(Context context, List<BusLine> busLines) {
        this.context = context;
        this.busLines = busLines;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_bus_line, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final BusLine busLine = getItemAtPosition(position);

        holder.binding.checkBusLine.setText(busLine.getName());
        holder.binding.checkBusLine.setTextColor(busLine.getColor());

    }

    @Override
    public int getItemCount() {
        return busLines.size();
    }

    public BusLine getItemAtPosition(int position) {
        return busLines.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public RowBusLineBinding binding;

        public ViewHolder(View itemView) {

            super(itemView);

            binding = RowBusLineBinding.bind(itemView);

            binding.checkBusLine.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (itemClickListener != null) {
                    itemClickListener.onBusLineCheckedChanged(getAdapterPosition(), isChecked);
                }
            });

        }

    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onBusLineCheckedChanged(int position, boolean checked);
    }
}


