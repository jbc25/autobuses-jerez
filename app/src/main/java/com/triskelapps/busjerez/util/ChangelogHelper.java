package com.triskelapps.busjerez.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.triskelapps.busjerez.App;
import com.triskelapps.busjerez.BuildConfig;
import com.triskelapps.busjerez.R;

public class ChangelogHelper {

    private final Context context;

    private static final String PREFIX = BuildConfig.APPLICATION_ID + ".";
    private static final String PREF_PREVIOUS_APP_VERSION_CODE = PREFIX + "pref_previous_app_version";

    private ChangelogHelper(Context context) {
        this.context = context;
    }

    public static ChangelogHelper with(Context context) {
        return new ChangelogHelper(context);
    }


    public void check() {

        int previousVersionCode = getPrefs().getInt(PREF_PREVIOUS_APP_VERSION_CODE, 0);
        int currentVersionCode = BuildConfig.VERSION_CODE;

        if (previousVersionCode < currentVersionCode && !isFirstTimeLaunch() && hasText(currentVersionCode)) {
            showText(currentVersionCode);
            getPrefs().edit().putInt(PREF_PREVIOUS_APP_VERSION_CODE, currentVersionCode).commit();
        }

    }

    private boolean isFirstTimeLaunch() {
        return getPrefs().getBoolean(App.PREF_FIRST_TIME_LAUNCH, true);
    }

    private void showText(int currentVersionCode) {
        int stringId = getStringId(currentVersionCode);
        new AlertDialog.Builder(context)
                .setTitle(R.string.version_updates)
                .setMessage(stringId)
                .setNegativeButton(R.string.ok, null)
                .show();
    }

    private boolean hasText(int currentVersionCode) {
        int stringId = getStringId(currentVersionCode);
        return stringId != 0;
    }

    private int getStringId(int currentVersionCode) {
        return context.getResources().getIdentifier("version_" + currentVersionCode, "string", context.getPackageName());
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
