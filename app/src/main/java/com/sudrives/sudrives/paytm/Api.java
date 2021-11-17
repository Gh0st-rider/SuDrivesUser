package com.sudrives.sudrives.paytm;

import com.sudrives.sudrives.utils.Config;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Nikhat on 1/10/2018.
 */

public interface Api {

    //this is the URL of the paytm folder that we added in the server
    //make sure you are using your ip else it will not work

     String BASE_URL = Config.PAYMENT_URL;



    @FormUrlEncoded
    @POST("generateChecksum.php")
    Call<Checksum> getChecksum(
            @FieldMap Map<String, String> params, @Header("Content-Type") String contentType, @Header("userid") String userid,
            @Header("version") String version, @Header("token") String token, @Header("timeZone") String timeZone,
            @Header("lang") String lang, @Header("fcmtoken") String fcm_token, @Header("devicetype") String device_type

    );

}






