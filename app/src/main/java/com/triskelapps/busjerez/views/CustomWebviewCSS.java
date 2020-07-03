package com.triskelapps.busjerez.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.triskelapps.busjerez.R;

import java.io.InputStream;

public class CustomWebviewCSS extends FrameLayout {
    private WebView webview;
    private String cssFile;
    private ProgressBar progressBar;

    public CustomWebviewCSS(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomWebviewCSS(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomWebviewCSS(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        View layout = View.inflate(getContext(), R.layout.webview_custom, null);
        webview = layout.findViewById(R.id.webview);
        progressBar = layout.findViewById(R.id.progressbar_webview);

        configureWebview();

        addView(layout);
    }

    public WebView getWebview() {
        return webview;
    }

    private void configureWebview() {

        webview.getSettings().setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(VISIBLE);
                webview.setVisibility(GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {


                // Inject CSS when page is done loading
                injectCSS();
                super.onPageFinished(view, url);

                progressBar.setVisibility(GONE);
                webview.setVisibility(VISIBLE);
            }
        });
    }

    public void loadUrlWithCSS(String url, String cssFile) {

        webview.setVisibility(GONE);
        this.cssFile = cssFile;
        webview.loadUrl(url);

    }

    private void injectCSS() {
        try {
            InputStream inputStream = getContext().getAssets().open(cssFile);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webview.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
