package com.sudrives.sudrives.utils.server;


import com.sudrives.sudrives.utils.Config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created
 */

public class ApiClientBuilder {


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    public static String API_BASE_URL = Config.SERVICE_URL;
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                //add timeout time if required
                .readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("Content-Type", "application/json; charset=UTF-8")
//                        .addHeader("session_key", "");
                        .addHeader("Accept", "application/json");
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);  // <-- this is the important line!
        if (retrofit == null) {
          //  Log.v("API_BASE_URL", "API_BASE_URL: " + API_BASE_URL);
            retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build()).build();
        }
        return retrofit;
    }


}
