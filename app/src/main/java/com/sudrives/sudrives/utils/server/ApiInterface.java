package com.sudrives.sudrives.utils.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 *
 */

public interface ApiInterface {

    @POST
    Call<JsonElement> postSimpleJson(@Url String url, @Body JsonObject body);

    @POST
    Call<JsonElement> postJson(@Url String url, @Body JsonObject body, @Header("userid") String userid,
                               @Header("version") String version, @Header("token") String token, @Header("timeZone") String timeZone, @Header("lang") String lang, @Header("fcmtoken") String fcm_token, @Header("devicetype") String device_type);
}
