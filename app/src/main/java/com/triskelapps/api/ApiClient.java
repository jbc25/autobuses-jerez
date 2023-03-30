package com.triskelapps.api;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.triskelapps.DebugHelper;
import com.triskelapps.util.DateUtils;

import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    private static final String TAG = "ApiClient";

    // Tutorial Retrofit 2.0
    // http://inthecheesefactory.com/blog/retrofit-2.0/en

    public static final String BASE_URL_PRODUCTION = "https://www.comujesa.es/";
    public static final String BASE_URL_DEBUG = "https://www.comujesa.es/";


    public static final String BASE_API_URL = DebugHelper.SWITCH_PROD_ENVIRONMENT ? BASE_URL_PRODUCTION : BASE_URL_DEBUG;

    private static Retrofit sharedInstance;

//    private static JsonDeserializer<Date> jsonDateDeserializer = new JsonDeserializer<Date>() {
//        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//
//            try {
//                return DateUtils.formatDateApi.parse(((JsonObject) json).get("initDate").getAsString());
//            } catch (ParseException e) {
//                throw new JsonParseException(e);
//            }
//
//        }
//    };

    public static Retrofit getInstance() {
        if (sharedInstance == null) {

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.VOLATILE, Modifier.TRANSIENT, Modifier.STATIC)
//                    .registerTypeAdapter(Area.class, null)
                    .setPrettyPrinting()
//                    .registerTypeAdapter(Date.class, jsonDateDeserializer)
                    .setDateFormat(DateUtils.formatDateApi.toPattern())
                    .create();


            sharedInstance = new Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getOkHttpClient())
                    .build();

        }

        return sharedInstance;
    }

    private static okhttp3.OkHttpClient getOkHttpClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okhttp3.Interceptor headersInterceptor = chain -> {

            okhttp3.Request original = chain.request();

            okhttp3.Request.Builder requestBuilder = original.newBuilder();
            requestBuilder.header("Content-Type", "application/json");

//            if (AuthLogin.API_KEY != null) {
//                requestBuilder.header("Authorization", AuthLogin.API_KEY);
//            }

            requestBuilder.method(original.method(), original.body());
            okhttp3.Request request = requestBuilder.build();

            okhttp3.Response response = chain.proceed(request);

            // otherwise just pass the original response on
            return response;
        };


        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(headersInterceptor)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
//                .sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> {
                    return true;
//                        return hostname.equals("triskelapps.com");
                })
                .build();

        return client;

    }

}
