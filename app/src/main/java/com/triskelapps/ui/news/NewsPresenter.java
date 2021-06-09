package com.triskelapps.ui.news;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.triskelapps.base.BasePresenter;

public class NewsPresenter extends BasePresenter {

    private final NewsView view;

    public static void launchNewsActivity(Context context) {

        Intent intent = new Intent(context, NewsActivity.class);

        context.startActivity(intent);
    }

    public static NewsPresenter newInstance(NewsView view, Context context) {

        return new NewsPresenter(view, context);

    }

    private NewsPresenter(NewsView view, Context context) {
        super(context, view);

        this.view = view;

    }

    public void onCreate() {

    }

    public void onResume() {

        refreshData();
    }

    public void refreshData() {


    }

}
