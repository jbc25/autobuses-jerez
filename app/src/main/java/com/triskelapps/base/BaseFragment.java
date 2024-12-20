package com.triskelapps.base;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.fragment.app.Fragment;

/**
 * Created by julio on 27/01/16.
 */
public abstract class BaseFragment extends Fragment implements BaseView {


    public final String TAG = this.getClass().getSimpleName();
    protected BaseActivity baseActivity;
    private BasePresenter basePresenter;
    private Handler handlerDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.baseActivity = (BaseActivity) context;
        } catch (ClassCastException e) {
            throw new IllegalStateException("The activity "
                    + "hosting this fragment does not extend BaseActivity");
        }

    }

    public BasePresenter getBasePresenter() {
        return basePresenter;
    }

    public void setBasePresenter(BasePresenter presenter) {
        this.basePresenter = presenter;
    }

    protected SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }


    @Override
    public void showProgressDialog(String message) {

        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showProgressDialog(message);
        }
    }

    @Override
    public void hideProgressDialog() {

        if (getActivity() != null) {
            ((BaseActivity) getActivity()).hideProgressDialog();
        }
    }


    @Override
    public void setRefreshing(boolean refresing) {

        if (getActivity() != null) {
            ((BaseActivity) getActivity()).setRefreshing(refresing);
        }
    }


    @Override
    public void toast(int stringResId) {

        if (getActivity() != null) {
            ((BaseActivity) getActivity()).toast(stringResId);
        }
    }

    @Override
    public void toast(String mensaje) {

        if (getActivity() != null) {
            ((BaseActivity) getActivity()).toast(mensaje);
        }
    }

    @Override
    public void alert(String title, String message) {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).alert(title, message);
        }
    }

    @Override
    public void alert(String message) {

        if (getActivity() != null) {
            ((BaseActivity) getActivity()).alert(message);
        }
    }

    @Override
    public void onInvalidToken() {

    }
}
