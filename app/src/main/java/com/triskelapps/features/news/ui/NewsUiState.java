package com.triskelapps.features.news.ui;

import com.triskelapps.features.news.data.News;

import java.util.ArrayList;
import java.util.List;

public class NewsUiState {
    private List<News> news = new ArrayList<>();
    private boolean loading;
    private String errorMessage;


    public NewsUiState setLoading(boolean loading) {
        this.loading = loading;
        return this;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

    public NewsUiState setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public boolean isLoading() {
        return loading;
    }

    public List<News> getNews() {
        return news;
    }

    public NewsUiState updateNews(List<News> newsUpdated) {
        news.clear();
        news.addAll(newsUpdated);
        return this;
    }
}
