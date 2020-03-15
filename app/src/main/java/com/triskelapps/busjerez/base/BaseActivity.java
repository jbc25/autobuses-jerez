package com.triskelapps.busjerez.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.triskelapps.busjerez.App;
import com.triskelapps.busjerez.R;


public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    public final String TAG = this.getClass().getSimpleName();
    private Activity activity;
    private ProgressDialog progressDialog;
    private String[] dataEntitiesSubscribed;
    private ProgressBar progressBar;
//    private TextView tvTitleToolbar;
    public Toolbar toolbar;

    // I need another result code custom
    public static final int REQ_CODE_EDIT = 1;
    public static final int RESULT_DELETED = 1234;
    private BasePresenter basePresenter;
    private Handler handlerDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getBasePresenter() != null) {
            getBasePresenter().setBaseView(this);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setRefreshing(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void setBasePresenter(BasePresenter basePresenter) {
        this.basePresenter = basePresenter;
    }

    public BasePresenter getBasePresenter() {
        return basePresenter;
    }

    public App getApp() {
        return (App) getApplicationContext();
    }

    // ------------ UI NOTIFICATIONS -----------

    public void refreshData() {
    }

    @Override
    public void onInvalidToken() {

        // Maybe an alertdialog?
//        toast(R.string.should_login_again);
        finish();
//        startActivity(LoginPresenter.newLoginActivity(this));
    }

    @Override
    public void toast(int stringResId) {
        toast(getString(stringResId));
    }

    @Override
    public void toast(final String mensaje) {

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

    }

    @Override
    public void alert(final String title, final String message) {

        AlertDialog.Builder ab = new AlertDialog.Builder(this);

        if (title != null) {
            ab.setTitle(title);
        }

        ab.setMessage(message);
        ab.setNegativeButton(R.string.back, null);
        ab.show();


    }

    @Override
    public void alert(final String message) {

        alert(getString(R.string.attention), message);
    }


    @Override
    public void showProgressDialog(String message) {

//        if (true) {
//            return;
//        }

        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.show();
        } catch (Exception e) {
            // Nothing to do. Most probably activity was destroyed
        }
    }


    @Override
    public void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void setRefreshing(boolean refresing) {

        if (progressBar != null) {
            progressBar.setVisibility(refresing ? View.VISIBLE : View.INVISIBLE);
//            progressBar.setIndeterminate(refresing);
        }
    }

    // ----CONFIGURATIONS --

    public void configureToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar == null) {
            return;
        }

        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
//        tvTitleToolbar = (TextView) findViewById(R.id.tv_title_toolbar);
//        tvTitleToolbar.setVisibility(View.GONE);
//        tvTitleToolbar.setText(getSupportActionBar().getTitle());

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    protected void configureSecondLevelActivity() {

        configureToolbar();

        if (toolbar == null) {
            throw new IllegalStateException("No Toolbar in this second level activity");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationIcon(R.mipmap.ic_back);
//
//        View imgAtomHome = findViewById(R.id.img_atom_home);
//        imgAtomHome.setVisibility(View.VISIBLE);
//        imgAtomHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(BaseActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });

        Drawable upArrow = AppCompatResources.getDrawable(this, R.mipmap.ic_arrow_back_white);
//        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back);
        DrawableCompat.setTint(upArrow, ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationIcon(upArrow);
    }

    public void hideAtomHomeButton() {

//        View imgAtomHome = findViewById(R.id.img_atom_home);
//        imgAtomHome.setVisibility(View.GONE);
    }

//    public void setToolbarTitle(String title) {
//        tvTitleToolbar.setText(title);
//    }
//
//    public void setToolbarTitle(int stringId) {
//        tvTitleToolbar.setText(stringId);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_CODE_EDIT) {
            if (resultCode == RESULT_DELETED) {
                finish();
                return;

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
