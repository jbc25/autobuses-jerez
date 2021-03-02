package com.triskelapps.busjerez.api;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BusTimetableApi {


    @POST("index.php?id=listar_b")
    @FormUrlEncoded
    Call<ResponseBody> getTimetable(@Field("valorLinea") int lineId, @Field("valorCaja1") int codeBusStop);


    @GET("https://triskelapps.es/apps/autobuses-jerez/timetables/L{lineId}-P{busStopCode}.html")
    Call<ResponseBody> getTimetableAlternative(@Path("lineId") int lineId, @Path("busStopCode") int codeBusStop);


}
