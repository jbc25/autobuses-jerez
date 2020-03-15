package com.triskelapps.busjerez.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.triskelapps.busjerez.R;

public class TimetableActivity extends AppCompatActivity {


    private static final String EXTRA_HTML_BODY = "extra_html_body";

    public static void launchTimetableActivity(Context context, String htmlBody) {
        Intent intent = new Intent(context, TimetableActivity.class);
        intent.putExtra(EXTRA_HTML_BODY, htmlBody);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timetable);

        String body = getIntent().getStringExtra(EXTRA_HTML_BODY);
        WebView webView = findViewById(R.id.webview_timetable);
        webView.loadData(body, "text/html", "utf-8");
    }
}
