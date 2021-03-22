package com.triskelapps.interactor;

import android.content.Context;
import android.util.Log;

import com.triskelapps.api.BusTimetableApi;
import com.triskelapps.base.BaseInteractor;
import com.triskelapps.base.BaseView;
import com.triskelapps.util.CountlyUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimetableInteractor extends BaseInteractor {


    public TimetableInteractor(Context context, BaseView baseView) {
        super(context, baseView);
    }


    public interface CallbackPostLogin extends CallbackPost {
        void onAccountNotActivated();
    }

    public void getTimetable(int lineId, int codeBusStop, CallbackPost callback) {

        getApi(BusTimetableApi.class)
                .getTimetable(lineId, codeBusStop)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                callback.onSuccess(response.body().string());
                            } catch (IOException e) {
                                callback.onError("Error on string body");
                                Log.e(TAG, "onResponse: Error", e);
                            }
                        } else {
                            callback.onError(parseError(response));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        CountlyUtil.recordHandledException(new Exception("Error loading timetable: " + t.getMessage()));
                        getTimetableAlternative(lineId, codeBusStop, callback);
                    }
                });
    }

    public void getTimetableAlternative(int lineId, int codeBusStop, CallbackPost callback) {

        getApi(BusTimetableApi.class)
                .getTimetableAlternative(lineId, codeBusStop)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                callback.onSuccess(response.body().string());
                            } catch (IOException e) {
                                callback.onError("Error on string body");
                                Log.e(TAG, "onResponse: Error", e);
                            }
                        } else {
                            callback.onError(parseError(response));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        CountlyUtil.recordHandledException(new Exception("Error loading timetable ALTERNATIVE: " + t.getMessage()));
                        callback.onError(t.getMessage());
                    }
                });
    }


}
