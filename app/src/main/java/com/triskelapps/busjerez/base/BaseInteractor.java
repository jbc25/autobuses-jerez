package com.triskelapps.busjerez.base;

import android.content.Context;

import com.triskelapps.busjerez.R;
import com.triskelapps.busjerez.api.ApiClient;
import com.triskelapps.busjerez.util.Util;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;


/**
 * Created by julio on 14/02/16.
 */
public class BaseInteractor {

    public final String TAG = this.getClass().getSimpleName();

    public Context context;
    public BaseView baseView;

    public BaseInteractor(Context context, BaseView baseView) {
        this.context = context;
        this.baseView = baseView;
    }

    public <T> T getApi(Class<T> service) {
        return ApiClient.getInstance().create(service);
    }

    public String parseError(Response<?> response) {

        String errorMsg = "";
        try {
            errorMsg = response.errorBody().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Error " + response.code() + ". " + errorMsg;
    }

    public interface CallbackGetList<T> {
        void onListReceived(List<T> list);

        void onError(String error);
    }

    public interface CallbackGetEntity<T> {
        void onEntityReceived(T entity);

        void onError(String error);
    }

    public interface CallbackPost {
        void onSuccess(String body);
        void onError(String error);
    }


    public boolean isConnected() {

        boolean connected = Util.isConnected(context);

        if (!connected) {
            if (baseView != null) {
                baseView.hideProgressDialog();
                baseView.toast(R.string.no_connection);
            }
        }

        return connected;
    }

}
