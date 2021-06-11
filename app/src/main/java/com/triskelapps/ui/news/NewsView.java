package com.triskelapps.ui.news;

import com.triskelapps.base.BaseView;
import com.triskelapps.model.News;

import java.util.List;

public interface NewsView  extends BaseView {
    void showNewsList(List<News> newsList);

    void showNewsParsed(News newsParsed);

    void showProgress();
}
