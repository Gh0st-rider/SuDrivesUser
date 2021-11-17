
package com.sudrives.sudrives.utils.server;


/**
 * Created
 */

public class ApiBuilderSingleton {

    private static ApiInterface mMethodBuilder;

    public static synchronized ApiInterface getInstance() {
        if (mMethodBuilder == null) {
            mMethodBuilder = ApiClientBuilder.getClient().create(ApiInterface.class);
        }
        return mMethodBuilder;
    }

    private ApiBuilderSingleton() {
    }


}
