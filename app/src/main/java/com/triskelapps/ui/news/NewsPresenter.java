package com.triskelapps.ui.news;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Patterns;

import com.triskelapps.App;
import com.triskelapps.R;
import com.triskelapps.base.BaseInteractor;
import com.triskelapps.base.BasePresenter;
import com.triskelapps.interactor.NewsInteractor;
import com.triskelapps.model.News;

import java.util.ArrayList;
import java.util.List;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class NewsPresenter extends BasePresenter {

    private final NewsView view;
    private final NewsInteractor newsInteractor;
    private List<News> newsList = new ArrayList<>();
    private News newsParsed;

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
        this.newsInteractor = new NewsInteractor(context, view);

    }

    public void onCreate() {
        refreshData();
    }


    public void refreshData() {


        newsInteractor.getNews(new BaseInteractor.CallbackGetList<News>() {
            @Override
            public void onListReceived(List<News> list) {
                newsList.clear();
                newsList.addAll(list);
                view.showNewsList(newsList);
            }

            @Override
            public void onError(String error) {
                view.toast(error);
            }
        });

    }

    public void onPasteUrlBtnClick() {

        String link = checkAndGetClipboardLink();
        if (link != null) {
            getNewsDetails(link);
        }
    }

    private void getNewsDetails(String link) {
        view.showProgressDialog(context.getString(R.string.receiving_news_data));
        newsInteractor.parseLinkDetails(link, new BaseInteractor.CallbackGetEntity<News>() {
            @Override
            public void onEntityReceived(News news) {
                view.hideProgressDialog();
                newsParsed = news;
                view.showNewsParsed(newsParsed);
            }

            @Override
            public void onError(String error) {
                view.hideProgressDialog();
                view.toast(error);
            }
        });
    }


    private String checkAndGetClipboardLink() {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        // If it does contain data, decide if you can handle the data.
        if (!(clipboard.hasPrimaryClip())) {
            view.toast(context.getString(R.string.no_link_copied));
        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
            // since the clipboard has data but it is not plain text
            view.toast(context.getString(R.string.invalid_link_copied));
        } else {

            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            // Gets the clipboard as text.
            //since the clipboard contains plain text.

            String pasteData = item.getText().toString();

            if (Patterns.WEB_URL.matcher(pasteData).matches()) {
                return pasteData;
            } else {
                view.toast(context.getString(R.string.invalid_link_copied));
            }
        }

        return null;
    }


    public void sendNews(News news) {

//        if (BuildConfig.DEBUG) {
//            showMessagePushDialog(App.TOPIC_NEWS, getString(R.string.new_news_push_title), news.getTitle());
//            return;
//        }

        view.showProgressDialog(getString(R.string.sending));

        newsInteractor.sendNews(news, new BaseInteractor.CallbackPost() {
            @Override
            public void onSuccess(String id) {
                if (id != null) {
                    view.toast(id);
                } else {
                    view.toast(R.string.news_sent_success);
                }

                refreshData();
//                showMessagePushDialog(App.TOPIC_NEWS, getString(R.string.new_news_push_title), news.getTitle());
            }

            @Override
            public void onError(String error) {
                view.toast("Fallo al publicar noticia. " + error);
            }
        });
    }

}
