package com.sudrives.sudrives.utils.server;


/**
 * Created by hemanth on 10/4/2016.
 */

public interface CallBackResponse {

    public void getResponse(Object response);
    public void getError(Object response, String error);

}
