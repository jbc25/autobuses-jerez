package com.triskelapps.util;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;

import com.triskelapps.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public final class Util {

    public static final String EXTRA_TEXT = "ExtraText";
    public static final String EXTRA_ID = "ExtraId";
    public static final String EXTRA_INT = "ExtraInt";
    public static final String EXTRA_BOOLEAN = "ExtraBoolean";
    public static final String EXTRA_DOUBLE = "ExtraDouble";
    public static final String EXTRA_LONG = "ExtraLong";
    public static final String EXTRA_TEXT_ARRAY = "ExtraTextArray";
    public static final String EXTRA_INT_ARRAY = "ExtraIntArray";
    public static final String EXTRA_TYPE = "ExtraType";
    public static final String EXTRA_OBJECT = "ExtraObject";


    public static final DecimalFormat decimalFormat = new DecimalFormat(
            "#,##0", new DecimalFormatSymbols(Locale.getDefault()));

    public static String getCurrentDateTime() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }


    public static String getCurrentDate() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date());
    }


    public static int getPixels(Context contexto, int dipValue) {
        Resources r = contexto.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue, r.getDisplayMetrics());
        return px;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null) {
            return network.isAvailable();
        }
        return false;

    }

    @SuppressLint("NewApi")
    public static int[] getDisplayDimensions(Context context) {

        // Fuente: http://stackoverflow.com/q/14341041/1365440

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Point size = new Point();
            try {
                WindowManager wm = (WindowManager) context
                        .getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getSize(size);

                // O tambien:
                // ((Activity)context).getWindowManager().getDefaultDisplay().getSize(size);

                int width = size.x;
                int height = size.y;

                return new int[]{width, height};

            } catch (NoSuchMethodError e) {
                Log.i("error", "it can't work");
            }

        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

            return new int[]{width, height};
        }

        return new int[]{0, 0};

    }

    public static String getStringFromAssets(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        InputStream input;
        String text = null;

        try {
            input = assetManager.open(fileName);

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // byte buffer into a string
            text = new String(buffer);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return text;
    }

    public static String normalizeText(String text) {
        return Normalizer
                .normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    public static String getDeviceId(Context context) {

        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }


    public static void setHtmlLinkableText(TextView textView, String htmlText) {

        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(htmlText);
        }
        textView.setText(result);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void shareText(Context context, String shareBody, String chooserText) {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        if (chooserText != null) {
            context.startActivity(Intent.createChooser(sharingIntent, chooserText));
        } else {
            context.startActivity(sharingIntent);
        }
    }

    public static String getStringFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        for (String line; (line = r.readLine()) != null; ) {
            stringBuilder.append(line).append('\n');
        }
        return stringBuilder.toString();
    }

    public static boolean isValidLink(String link) {
        return link != null && Patterns.WEB_URL.matcher(link).matches();
    }

    public static void openLink(Context context, String link) {

        if (!isValidLink(link)) {
            Toasty.error(context, context.getString(R.string.invalid_link)).show();
            return;
        }

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (ActivityNotFoundException e) {
            Toasty.error(context, context.getString(R.string.no_app_to_open_link)).show();
        }
    }
}
