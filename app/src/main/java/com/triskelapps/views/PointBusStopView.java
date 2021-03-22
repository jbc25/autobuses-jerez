package com.triskelapps.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.triskelapps.busjerez.databinding.ViewPointBusStopBinding;


public class PointBusStopView extends FrameLayout {
    private ViewPointBusStopBinding binding;

    public PointBusStopView(Context context) {
        super(context);
        init();
    }

    public PointBusStopView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PointBusStopView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        binding = ViewPointBusStopBinding.inflate(LayoutInflater.from(getContext()));
        addView(binding.getRoot());
    }

    public void setColor(int color) {
        DrawableCompat.setTint(binding.viewBusStopLineLeft.getBackground(), color);
        DrawableCompat.setTint(binding.viewBusStopLineRight.getBackground(), color);
        DrawableCompat.setTint(binding.viewBusStopPoint.getBackground(), color);
    }

    public void configureStartEndPoint(int position, int size) {
        binding.viewBusStopLineLeft.setVisibility(position == 0 ? INVISIBLE : VISIBLE);
        binding.viewBusStopLineRight.setVisibility(position == size - 1 ? INVISIBLE : VISIBLE);
    }
}
