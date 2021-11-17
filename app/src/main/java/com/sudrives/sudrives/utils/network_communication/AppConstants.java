package com.sudrives.sudrives.utils.network_communication;


import com.sudrives.sudrives.utils.Config;

/**
 * Created by krishnapal on 9/25/17.
 */

public class AppConstants {

    // Sign Up URL
    public static final String PROFILE_UPDATE_URL = Config.SERVICE_URL + "api/profile_update";
    public static final String EVENT_PROFILE_UPDATE = "eventProfileUpdate";

    // Report Issue URL
    public static final String REPORT_ISSUE_URL = Config.SERVICE_URL + "driver/report_issue";
    public static final String EVENT_REPORT_ISSUE = "eventReportIssue";


    public static final String KEY_TOKEN = "token";
    public static final String KEY_LANG = "lang";
    public static final String KEY_TIME_ZONE = "timeZone";
    public static final String KEY_DEVICE_TOKEN = "fcmtoken";
    public static final String KEY_DEVICE_TYPE = "devicetype";

    public static final String KEY_STATUS = "status";
    public static final String KEY_MESSAGE = "message";

    public static final String KEY_USER_ID = "userid";
    public static final String KEY_VERSION = "version";

    // Static final Variables
    public static final int STATUS_SUCCESS_VALUE = 1;














}
