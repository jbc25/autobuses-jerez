package com.triskelapps;

import ly.count.android.sdk.Countly;

public class DebugHelper {

    //    private static Class SHORTCUT_ACTIVITY = CategoriesActivity.class;
    private static Class SHORTCUT_ACTIVITY = null;

    // === SWITCHES DEBUG ===

    // Trues in production apk (automatic)
    private static final boolean SYNC_AT_BEGIN = true;


    // Falses
    private static final boolean MOCK_DATA = true;
    public static final boolean DEBUG_MESSAGES = false;
    public static final boolean DISABLE_CRASHLYTICS = true;
    public static final boolean USE_TEST_STRIPE_KEY = false;
    private static final boolean RECORD_ANALYTICS = false;



    // ---------- NOT USED ------------

    //Trues
    private static final boolean PROD_ENVIRONMENT = false;


    //Falses
    private static final boolean SKIP_SPLASH = false;
    private static final boolean COMPLETE_EDIT_TEXTS = true;
    private static final boolean DEBUG_TOKEN = false;

    private static final boolean FORZE_MASTER = false;
    private static final boolean FORZE_ALARM = true;

    private static final boolean FORZE_BETA_ENV_APK = false;

    public static final boolean BILLING_TESTING = false;


    // ----------------------------------------------

    public static final boolean SWITCH_SKIP_SPLASH = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? SKIP_SPLASH : false;

    public static final boolean SWITCH_COMPLETE_EDIT_TEXTS = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? COMPLETE_EDIT_TEXTS : false;

    public static final boolean SWITCH_PROD_ENVIRONMENT = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? PROD_ENVIRONMENT : true;

    public static final boolean SWITCH_DEBUG_MESSAGES = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? DEBUG_MESSAGES : false;

    public static final boolean SWITCH_FORZE_MASTER = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? FORZE_MASTER : false;

    public static final boolean SWITCH_FORZE_ALARM = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? FORZE_ALARM : false;

    public static final boolean SWITCH_DEBUG_TOKEN = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? DEBUG_TOKEN : false;

    public static final boolean SWITCH_MOCK_DATA = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? MOCK_DATA : false;

    public static final boolean SWITCH_SYNC_AT_BEGIN = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? SYNC_AT_BEGIN : true;

    public static final boolean SWITCH_DISABLE_CRASHLYTICS = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? DISABLE_CRASHLYTICS : false;

    public static final boolean SWITCH_USE_TEST_STRIPE_KEY = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? USE_TEST_STRIPE_KEY : false;

    public static final Class SWITCH_SHORTCUT_ACTIVITY = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? SHORTCUT_ACTIVITY : null;

    public static final boolean SWITCH_RECORD_ANALYTICS = BuildConfig.DEBUG
            || FORZE_BETA_ENV_APK ? RECORD_ANALYTICS : true;


}
