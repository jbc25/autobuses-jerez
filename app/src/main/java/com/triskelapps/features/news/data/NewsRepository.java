package com.triskelapps.features.news.data;

import com.triskelapps.base.BaseInteractor;

public class NewsRepository {

    private final NewsRemoteDataSource newsRemoteDataSource;

    public NewsRepository(NewsRemoteDataSource newsRemoteDataSource) {
        this.newsRemoteDataSource = newsRemoteDataSource;
    }

    public void getNews(final BaseInteractor.CallbackGetList<News> callback) {
        newsRemoteDataSource.getNews(callback);
    }

}
