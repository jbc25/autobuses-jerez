package com.triskelapps.ui.news;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;

import com.squareup.picasso.Picasso;
import com.triskelapps.BuildConfig;
import com.triskelapps.R;
import com.triskelapps.base.BaseActivity;
import com.triskelapps.databinding.ActivityNewsBinding;
import com.triskelapps.databinding.RowNewsBinding;
import com.triskelapps.model.News;
import com.triskelapps.views.EditTextDialog;

import java.util.List;

public class NewsActivity  extends BaseActivity implements NewsView {

    private ActivityNewsBinding binding;
    private NewsPresenter presenter;
    private NewsAdapter adapter;

    View.OnClickListener editTextClickListener = v -> {
        if (v instanceof TextView) {
            EditTextDialog.with((TextView) v).show();
            return;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        presenter = NewsPresenter.newInstance(this, this);

        configureSecondLevelActivity();

        binding.recyclerNews.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.btnSendNews.setOnClickListener(v -> presenter.onPasteUrlBtnClick());
        binding.btnSendNews.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);

        presenter.onCreate();

    }

    @Override
    public void showNewsList(List<News> newsList) {

        binding.progressNews.setVisibility(View.GONE);
        binding.recyclerNews.setVisibility(View.VISIBLE);

        if (adapter == null) {
            adapter = new NewsAdapter(this, newsList);
            binding.recyclerNews.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showNewsParsed(News news) {
        RowNewsBinding rowNewsBinding = RowNewsBinding.inflate(LayoutInflater.from(this));

        rowNewsBinding.tvNewsTitle.setText(news.getTitle());
        rowNewsBinding.tvNewsSubtitle.setText(news.getSubtitle());
        rowNewsBinding.tvNewsDate.setText(news.getDate());
        rowNewsBinding.tvNewsMedia.setText(news.getMedia());

        rowNewsBinding.tvNewsTitle.setOnClickListener(editTextClickListener);
        rowNewsBinding.tvNewsSubtitle.setOnClickListener(editTextClickListener);
        rowNewsBinding.tvNewsDate.setOnClickListener(editTextClickListener);
        rowNewsBinding.tvNewsMedia.setOnClickListener(editTextClickListener);

        Picasso.get()
                .load(news.getImage())
//                .placeholder(R.mipmap.img_default_grid)
//                        .error(R.mipmap.ic_mes_v2_144)
//                        .resizeDimen(R.dimen.width_image_small, R.dimen.height_image_small)
                .into(rowNewsBinding.imgNews);

        rowNewsBinding.imgNews.setTag(news.getImage());
        rowNewsBinding.imgOpenUrl.setTag(news.getUrl());

        new AlertDialog.Builder(this)
                .setView(rowNewsBinding.getRoot())
                .setPositiveButton(R.string.send, (dialog, which) -> {
                    News news2 = new News();
                    news2.setTitle(rowNewsBinding.tvNewsTitle.getText().toString());
                    news2.setSubtitle(rowNewsBinding.tvNewsSubtitle.getText().toString());
                    news2.setDate(rowNewsBinding.tvNewsDate.getText().toString());
                    news2.setMedia(rowNewsBinding.tvNewsMedia.getText().toString());
                    news2.setImage(rowNewsBinding.imgNews.getTag().toString());
                    news2.setUrl(rowNewsBinding.imgOpenUrl.getTag().toString());
                    presenter.sendNews(news2);
                })
                .setNeutralButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void showProgress() {
        binding.progressNews.setVisibility(View.VISIBLE);
        binding.recyclerNews.setVisibility(View.GONE);
    }

}
