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

    public void getTimetable(final int lineId, final int codeBusStop, CallbackPost callback) {

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

                            if (response.code() == 404) {
                                CountlyUtil.recordHandledException(new Exception("Error loading timetable ALTERNATIVE: Parada no encontrada. " +
                                        String.format("L: %d - P: %d", lineId, codeBusStop)));
                            } else {
                                CountlyUtil.recordHandledException(new Exception("Error loading timetable ALTERNATIVE: " + parseError(response)));
                            }

                            getTimetableOriginal(lineId, codeBusStop, callback);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        CountlyUtil.recordHandledException(new Exception("Error loading timetable ALTERNATIVE: " + t.getMessage()));
                        getTimetableOriginal(lineId, codeBusStop, callback);
                    }
                });
    }

    private void getTimetableOriginal(int lineId, int codeBusStop, CallbackPost callback) {

        getApi(BusTimetableApi.class)
                .getTimetableOriginal(lineId, codeBusStop)
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

                            if (response.code() == 404) {
                                CountlyUtil.recordHandledException(new Exception("Error loading timetable ORIGINAL: Parada no encontrada. " +
                                        String.format("L: %d - P: %d", lineId, codeBusStop)));
                            } else {
                                CountlyUtil.recordHandledException(new Exception("Error loading timetable ORIGINAL: " + parseError(response)));
                            }

                            callback.onError(parseError(response));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                        CountlyUtil.recordHandledException(new Exception("Error loading timetable ORIGINAL: " + t.getMessage()));
                        callback.onError(t.getMessage());
                    }
                });
    }


}
