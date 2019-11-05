package com.geofencing.constants;

public final class Constants {
    //Network URLs
    public static final String GEOFENCE_URL = "http://10.0.2.2:80/safechild/get_data.php";
    public static final String LAST_KNOWN_LOCATION_URL = "http://10.0.2.2:80/safechild/save_last_known_location.php";
    //public static String SIGN_IN_URL = "http://10.0.2.2:80/safechild/child_login.php";
    //public static String GET_CHILD_DATA_URL = "http://10.0.2.2:80/safechild/get_child_data.php";
    public static String SIGN_IN_URL = "http://155.246.218.202:3000/authenticateChild";
    public static String GET_CHILD_DATA_URL = "http://155.246.218.202:3000/childData";

    //Database
    public static final String GEOFENCE_EVENT_ENTITY = "geofence_event_entity";
    public static final String GEOFENCE_OBJECT_ENTITY = "geofence_object_entity";

    //tasks
    public static final String SAVE_LAST_KNOWN_LOCATION = "save_last_known_location";
    public static final String SIGN_IN = "sign_in";
    public static final String GET_GEOFENCES = "get_geofences";
}
