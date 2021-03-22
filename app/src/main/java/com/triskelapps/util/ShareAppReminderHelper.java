package com.triskelapps.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.triskelapps.BuildConfig;
import com.triskelapps.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShareAppReminderHelper {

    private static final int APP_START_PERIOD = 10;

    private final Context context;

    private static final String PREFIX = BuildConfig.APPLICATION_ID + ".";
    private static final String PREF_APP_START_COUNT = PREFIX + "pref_app_start_count";
    private static final String PREF_LAST_DATE_DIALOG_SHOWN = PREFIX + "pref_last_date_dialog_shown";
    private static final String PREF_DONT_SHOW_AGAIN = PREFIX + "pref_dont_show_again_share_app_reminder";

    private ShareAppReminderHelper(Context context) {
        this.context = context;
    }

    public static ShareAppReminderHelper with(Context context) {
        return new ShareAppReminderHelper(context);
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void check() {
        if (getPrefs().getBoolean(PREF_DONT_SHOW_AGAIN, false)) {
            return;
        }

        incrementAppStartCount();

        if (shouldRemind()) {
            showDialog();
        }
    }

    private void incrementAppStartCount() {
        int appStartCount = getPrefs().getInt(PREF_APP_START_COUNT, 0);
        getPrefs().edit().putInt(PREF_APP_START_COUNT, appStartCount + 1).commit();
    }

    private boolean shouldRemind() {
        int appStartCount = getPrefs().getInt(PREF_APP_START_COUNT, 0);
        String lastDateDialogShown = getPrefs().getString(PREF_LAST_DATE_DIALOG_SHOWN, "2020-01-01");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        return appStartCount % APP_START_PERIOD == 0 && !TextUtils.equals(lastDateDialogShown, currentDate);
    }

    private void showDialog() {

        int appStartCount = getPrefs().getInt(PREF_APP_START_COUNT, 0);
        CountlyUtil.appShareReminderDialogShown(appStartCount);

        new AlertDialog.Builder(context)
                .setTitle(R.string.share_app)
                .setMessage(R.string.share_app_reminder_text)
                .setPositiveButton(R.string.share, (dialog, which) -> {
                    Util.shareText(context, context.getString(R.string.share_text), context.getString(R.string.share_app));
                    getPrefs().edit().putBoolean(PREF_DONT_SHOW_AGAIN, true).commit();
                    CountlyUtil.recordEvent("app_share_reminder_click_sharing");
                })
                .setNegativeButton(R.string.later, (dialog, which) ->
                        CountlyUtil.recordEvent("app_share_reminder_click_later"))
                .setNeutralButton(R.string.dont_show_again, (dialog, which) -> {
                    getPrefs().edit().putBoolean(PREF_DONT_SHOW_AGAIN, true).commit();
                    CountlyUtil.recordEvent("app_share_reminder_click_never");
                })
                .show();

        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        getPrefs().edit().putString(PREF_LAST_DATE_DIALOG_SHOWN, currentDate).commit();
    }
}
