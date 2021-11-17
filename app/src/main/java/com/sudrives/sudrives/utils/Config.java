package com.sudrives.sudrives.utils;

/**
 * Created by admin on 14/11/17.
 */

public class Config {


    public static String CALL_API = "http://14.97.59.83:8081/SEATECHCCIVR/CallFromApp?";
    //public static String SAVE_CARD_DETAIL = BASE_URL_NEW + "savecardetails";
    public static String DOMAIN_URL = "http://staging-admin.sudrives.com/";//"http://load.sudrives.com/";

    public static final String BASE_URL_NEW = DOMAIN_URL + "new_api/public/api/";

    public static String DEVICE_TYPE = "android";
    public static String VERSION = "android";
    public static String TYPE_USER_VAl = "3";
    public static String TIMEZONE = "IST";
    public static String CANCELORDERLIST = "cancel_order";
    public static String REPORTISSUE = "report_issue";
    public static String ContactUs = "contact-us";
    public static String TOF = "term-and-condition-user";
    public static String PRIVACYPOLICY = "privacy-policies-user";
    public static String AboutUs = "about-us";
    public static String SELECTED_LANG = "";
    public static String AUTO_VEHICLE = "Auto";
    public static String DAILY = "daily";
    public static String OUTSTATION = "outstation";
    public static String RENTAL = "rental";
    public static String OTP_HASH = "GTYN7liCV1i";

    /*
     * device token means device notification id
     * */

    public static String DEVICE_TOKEN = "";

    /*
     * true mean show logcat
     * */
    public static final boolean isLog = true;

    /*
     * device id
     * */
    public static String deviceId = "9AF240F8F11312391E5D9321083BD042";


    public static String SERVICE_URL = "";
    public static String BASE_URL_SOCKET = "";
    public static String PAYMENT_URL = "";
    public static String VERSION_CODE = "1";

    /**
     * Application mode development-staging or production-prod
     */


    public static String BASE_URL_SOCKET_PRODUCTION = "http://103.56.36.39:3000/";//"http://129.151.46.196:3000/";//"http://152.67.23.162:3000/"; //"http://103.56.36.39:3000/";/*"http://152.67.31.61:3000/";*//*"http://13.235.134.181:3000/";*/
    public static String APIURLPRODUCTION = DOMAIN_URL + "api/";

    public static String PAYMENT_BASEURL_PRODUCTION = DOMAIN_URL + "PaytmAppLive/";

    public static String CHANGE_PAYMENT_MODE = APIURLPRODUCTION + "socketApi/change_trip_payment_type";

    public static String CANCEL_RIDE_REQUEST = APIURLPRODUCTION + "socketApi/trip_cancel";

    // live app
    public static String VERSION_CODE_PRODUCTION = "6";     // TODO (last updated date  13-09-2019)

    // Socket EMIT Events
    public static String EMIT_GET_VEHICHLES = "get_vehicle_types";
    public static String EMIT_GET_TRIPS = "get_incomplete_trip";
    public static String EMIT_GET_BOOKING_STATUS = "get_pending_booking";
    public static String EMIT_GET_VEHICHLES_RATE = "get_vehicle_types";
    public static String EMIT_GET_DRIVERS = "get_search_driver";
    public static String EMIT_GET_CREATE_BOOKING = "get_create_booking";
    public static String EMIT_GET_LIVE_TRIPS = "get_user_live_trip";
    public static String EMIT_GET_TRIP_DETAILS = "get_trip_detail";
    public static String EMIT_ROOM = "room";
    public static String EMIT_DISCONNECT = "disconnect";
    public static String EMIT_GET_TRIP_CANCEL = "get_trip_cancel";
    public static String EMIT_GET_DISTANCE_MATRIX_SERVICE = "get_distance_matrix_service";
    public static String EMIT_GET_GOOGLE_HAS_KEY = "get_google_has_key";
    public static String EMIT_GET_PAYMENT = "get_update_payment_data";
    public static String EMIT_LOGOUT = "get_logout";
    public static String EMIT_EMERGENCY_CONTACT = "get_update_emergency_contact";
    public static String EMIT_EMERGENCY_SMS = "get_send_emergency_sms";
    public static String EMIT_EMERGENCY_CONTACT_SUPPORT = "get_emergency_contact_of_support";
    public static String EMIT_EMERGENCY_GET_PROFILE = "get_profile";
    public static String EMIT_EMERGENCY_GET_CANCELLED_TRIP = "get_last_cancel_trip_status";
    public static String EMIT_GET_RENTAL_PACKAGES = "get_rental_packages";
    public static String EMIT_GET_RENTAL_CABS = "get_vehicle_price_according_to_package";
    public static String EMIT_GET_OUTSTATION_CABS = "get_vehicle_price_according_to_oustation_destination";
    public static String EMIT_GET_CHANGE_DESTINATION_ADDRESS = "get_change_destination_address";


    // Socket LISTENER EVENT

    public static String LISTENER_GET_VEHICHLE_RATE = "responce_vehicle_types";
    public static String LISTENER_GET_VEHICHLE = "responce_vehicle_types";
    public static String LISTENER_GET_BOOKING_STATUS = "responce_pending_booking";
    public static String LISTENER_GET_DRIVER = "responce_search_driver";
    public static String LISTENER_GET_CREATE_BOOKING = "responce_create_booking";
    public static String LISTENER_GET_LIVE_TRIPS = "responce_user_live_trip";
    public static String LISTENER_GET_TRIPS_DETAIL = "responce_trip_detail";
    public static String LISTENER_GET_TRIP_CANCEL = "responce_trip_cancel";
    public static String LISTENER_GET_RESPONSE_DRIVER_CURRENT_LOCATION_UPDATE = "responce_driver_current_location_update";
    public static String LISTENER_GET_RESPONSE_DISTANCE_MATRIX_SERVICE = "responce_distance_matrix_service";
    public static String LISTENER_GET_RESPONSE_COMPLETE_TRIP = "responce_incomplete_trip";
    public static String LISTENER_GET_RESPONSE_COMPLETE_START = "responce_trip_start";
    public static String LISTNER_GET_RESPONSE_END_TRIP = "responce_trip_end";
    public static String LISTNER_GET_RESPONSE_CANCELLED_TRIP = "responce_trip_cancel";
    public static String LISTNER_GET_GOOGLE_HAS_KEY = "responce_google_has_key";
    public static String LISTNER_GET_BOOKING_ACCEPT = "responce_booking_accept";
    public static String LISTNER_GET_PAYMENT = "responce_update_payment_data";
    public static String LISTNER_GET_BOOKING_AUTO_CANCEL = "responce_trip_auto_cancel";
    public static String LISTNER_GET_RESPONSE_ARRIVED_TRIP = "responce_booking_arrived_at_pickup";

    public static String LISTNER_GET_LOGOUT = "responce_logout";
    public static String LISTNER_GET_EMERGENCY_CONTACT = "responce_update_emergency_contact";
    public static String LISTENER_GET_EMERGENCY_SMS = "responce_send_emergency_sms";
    public static String LISTENER_GET_CONTACT_SUPPORT = "responce_emergency_contact_of_support";
    public static String LISTENER_GET_PROFILE = "responce_profile";
    public static String LISTENER_GET_CANCELLED_TRIP = "responce_last_cancel_trip_status";
    public static String LISTENER_GET_RENTAL_PACKAGES = "responce_rental_packages";
    public static String LISTENER_GET_RENTAL_CAB_TYPE = "responce_vehicle_price_according_to_package";
    public static String LISTENER_GET_OUTSTATION_CAB_TYPE = "responce_vehicle_price_according_to_oustation_destination";
    public static String LISTENER_GET_CHANGE_DESTINATION_ADDRESS = "responce_change_destination_address";
    public static String LISTENER_GET_DRIVER_PROJECTION = "responce_driver_angle_update";
    public static String AMOUNT_SAVE = APIURLPRODUCTION + "socketApi/savePaymentAmount";

    public static String GENERATE_ORDER_ID = APIURLPRODUCTION + "driver/generaterazorpayorder";

    public static String FETCH_RECENT_BOOKING = APIURLPRODUCTION + "socketApi/fetchrecentbookingaddress";

    public static String CHAT_NOTIFICATION = APIURLPRODUCTION + "socketApi/sendmessagenotification";

    public static String GET_PROMOAPPLIED = APIURLPRODUCTION + "socketApi/fetchcoupondiscountamount";


    static {
        SERVICE_URL = APIURLPRODUCTION;
        BASE_URL_SOCKET = BASE_URL_SOCKET_PRODUCTION;
        PAYMENT_URL = PAYMENT_BASEURL_PRODUCTION;
        VERSION_CODE = VERSION_CODE_PRODUCTION;
    }


    public static boolean PATH_TRACKING = false;


}
