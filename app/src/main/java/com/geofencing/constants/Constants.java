package com.geofencing.constants;

public final class Constants {
    //Network URLs

    //public static final String GEOFENCE_URL = "http://155.246.114.74:80/safechild/get_child_geofences.php";
    /*
    public static final String LAST_KNOWN_LOCATION_URL = "http://192.168.1.153:80/safechild/save_last_known_location.php";
    public static String SIGN_IN_URL = "http://192.168.1.153:80/safechild/child_login.php";
    //public static String GET_CHILD_DATA_URL = "http://192.168.1.153:80/safechild/get_child_data.php";

     */

    public static String SIGN_IN_URL = "http://155.246.218.43:3000/authenticateChild";
    public static String GET_CHILD_DATA_URL = "http://155.246.218.43:3000/childData";
    public static String LAST_KNOWN_LOCATION_URL = "http://155.246.218.43:3000/childLocationUpdate";
    public static String CHILD_FCM_TOKEN_UPDATE_URL = "http://155.246.218.43:3000/childFCMTokenUpdate";
    public static final String GEOFENCE_URL = "http://155.246.218.43:3000/childData";
    public static final String PUSH_NOTIFICATION_TO_PARENT_URL = "http://155.246.218.43:3000/geofenceEventTriggerNotification";

    //Database
    public static final String GEOFENCE_EVENT_ENTITY = "geofence_event_entity";
    public static final String GEOFENCE_OBJECT_ENTITY = "geofence_object_entity";

    //tasks
    public static final String SAVE_LAST_KNOWN_LOCATION_TASK = "save_last_known_location";
    public static final String SIGN_IN_TASK = "sign_in";
    public static final String GET_GEOFENCES_TASK = "get_geofences";
    public static final String CHILD_UPDATE_FCM_TOKEN_TASK = "child_update_fcm_token";
    public static final String PUSH_NOTIFICATION_TO_PARENT_TASK = "push_notification_to_parent";

}
