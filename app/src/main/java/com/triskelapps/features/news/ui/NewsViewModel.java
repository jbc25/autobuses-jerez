package com.triskelapps.features.news.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.triskelapps.base.BaseInteractor;
import com.triskelapps.features.news.data.News;
import com.triskelapps.features.news.data.NewsRepository;

import java.util.List;

public class NewsViewModel extends ViewModel {


    private final NewsRepository newsRepository;
    private MutableLiveData<NewsUiState> _news = new MutableLiveData<>();
    public LiveData<NewsUiState> news = _news;

    public NewsViewModel(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public void onCreate() {

        _news.setValue(_news.getValue().setLoading(true));

        newsRepository.getNews(new BaseInteractor.CallbackGetList<News>() {
            @Override
            public void onListReceived(List<News> list) {
                _news.setValue(_news.getValue().updateNews(list).setLoading(false));
            }

            @Override
            public void onError(String error) {
                _news.setValue(_news.getValue().setErrorMessage(error));
            }
        });
    }

}
