package com.triskelapps.ui.news;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;
import com.triskelapps.R;
import com.triskelapps.model.News;
import com.triskelapps.util.CountlyUtil;
import com.triskelapps.util.Util;
import com.triskelapps.util.WebUtils;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {


    private List<News> newsList;
    private Context context;
    private OnItemClickListener itemClickListener;


    public NewsAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View contactView = LayoutInflater.from(context).inflate(R.layout.row_news, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position2) {

        final News news = getItemAtPosition(holder.getAdapterPosition());

        holder.tvNewsTitle.setText(news.getTitle());
        holder.tvNewsSubtitle.setText(news.getSubtitle());
        holder.tvNewsDate.setText(news.getDateHumanFormat());
        holder.tvNewsMedia.setText(news.getMedia());

        Picasso.get()
                .load(news.getImage())
//                .placeholder(R.mipmap.img_default_grid)
//                        .error(R.mipmap.ic_mes_v2_144)
//                        .resizeDimen(R.dimen.width_image_small, R.dimen.height_image_small)
                .into(holder.imgNews);


    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public News getItemAtPosition(int position) {
        return newsList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public View rootView;
        private AppCompatImageView imgNews;
        private TextView tvNewsTitle;
        private TextView tvNewsSubtitle;
        private TextView tvNewsDate;
        private TextView tvNewsMedia;

        public ViewHolder(View itemView) {

            super(itemView);

            imgNews = (AppCompatImageView) itemView.findViewById(R.id.img_news);
            tvNewsTitle = (TextView) itemView.findViewById(R.id.tv_news_title);
            tvNewsSubtitle = (TextView) itemView.findViewById(R.id.tv_news_subtitle);
            tvNewsDate = (TextView) itemView.findViewById(R.id.tv_news_date);
            tvNewsMedia = (TextView) itemView.findViewById(R.id.tv_news_media);

            rootView = itemView;

            rootView.setOnClickListener(v -> {
                News news = getItemAtPosition(getAdapterPosition());
                WebUtils.openCustomTab(context, news.getUrl());
                logEvent(news);
            });
        }

    }

    private void logEvent(News news) {

        CountlyUtil.newsClick(news);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}


