package com.triskelapps.busjerez.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.triskelapps.busjerez.BuildConfig;
import com.triskelapps.busjerez.DebugHelper;
import com.triskelapps.busjerez.R;

import ly.count.android.sdk.Countly;


public class UpdateAppView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "UpdateAppView";

    private TextView btnUpdateApp;
    private AppCompatImageView btnCloseUpdateAppView;
    private AppUpdateManager appUpdateManager;
    private AppUpdateInfo appUpdateInfo;

    public UpdateAppView(Context context) {
        super(context);
        init();
    }


    public UpdateAppView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UpdateAppView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void findViews(View layout) {
        btnUpdateApp = (TextView) layout.findViewById(R.id.btn_update_app);
        btnCloseUpdateAppView = (AppCompatImageView) layout.findViewById(R.id.btn_close_update_app_view);

        btnUpdateApp.setOnClickListener(this);
        btnCloseUpdateAppView.setOnClickListener(this);
    }

    private void init() {
        View layout = View.inflate(getContext(), R.layout.view_update_app, null);
        findViews(layout);

        addView(layout);

        setVisibility(GONE);

        configure();
    }

    private void configure() {

        appUpdateManager = AppUpdateManagerFactory.create(getContext());

        if (getContext() instanceof Activity) {
            checkUpdateAvailable();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_close_update_app_view:
                setVisibility(GONE);
                break;

            case R.id.btn_update_app:
                onUpdateVersionClick();
                break;
        }

    }


    public void onUpdateVersionClick() {

        AppUpdateOptions options = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                .setAllowAssetPackDeletion(true).build();

        appUpdateManager.startUpdateFlow(appUpdateInfo, (Activity) getContext(), options);

    }

    private void checkUpdateAvailable() {

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask
                .addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                        this.appUpdateInfo = appUpdateInfo;
                        setVisibility(VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    if (DebugHelper.SWITCH_RECORD_ANALYTICS) {
                        Countly.sharedInstance().crashes().recordHandledException(e);
                    }
                    e.printStackTrace();
                });

    }
}
