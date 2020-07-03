package com.triskelapps.busjerez.ui.news;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.triskelapps.busjerez.base.BaseActivity;
import com.triskelapps.busjerez.databinding.ActivityNewsBinding;
import com.triskelapps.busjerez.util.Util;

import java.io.InputStream;

public class NewsActivity extends BaseActivity {

    private ActivityNewsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureSecondLevelActivity();

        binding.webviewCss.loadUrlWithCSS("https://www.jerez.es/webs_municipales/autobuses_urbanos/noticias/", "styles/news_styles.css");

    }

    @Override
    public void onBackPressed() {
        if (!checkBackNavigation()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (checkBackNavigation()) {
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkBackNavigation() {

        if (binding.webviewCss.getWebview().canGoBack()) {
            binding.webviewCss.getWebview().goBack();
            return true;
        }

        return false;
    }
}