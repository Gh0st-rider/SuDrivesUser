package com.sudrives.sudrives.utils.server;

import com.google.gson.JsonObject;

import java.util.HashMap;

/**
 * Created by hemanth on 12/22/2016.
 */

public class JsonElementUtil {

    public static JsonObject getJsonObject(String... nameValuePair) {
        JsonObject HashMap = null;

        if (null != nameValuePair && nameValuePair.length % 2 == 0) {

            HashMap = new JsonObject();

            int i = 0;

            while (i < nameValuePair.length) {
                HashMap.addProperty(nameValuePair[i], nameValuePair[i + 1]);
                i += 2;
            }

        }

        return HashMap;
    }


    public static JsonObject getJsonObjectForImageArray(HashMap<String, String> hashMap ) {
        JsonObject HashMap = null;

        if (null != hashMap && hashMap.size()>0) {

            HashMap = new JsonObject();

            int i = 0;

            for ( String key : hashMap.keySet() ) {



                HashMap.addProperty(key,hashMap.get(key));

            }

        }

        return HashMap;
    }

}
