package com.triskelapps.ui.news;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.triskelapps.base.BaseActivity;
import com.triskelapps.databinding.ActivityNewsBinding;

public class NewsActivity  extends BaseActivity {

    private ActivityNewsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());


    }
}
