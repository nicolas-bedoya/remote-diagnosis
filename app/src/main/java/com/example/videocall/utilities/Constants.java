package com.example.videocall.utilities;

import java.util.HashMap;

public class Constants {

    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_DOCTOR = "doctor";
    public static final String KEY_PATIENT = "patient";
    public static final String KEY_MEDICAL_CENTER = "medical_center";
    public static final String KEY_FCM_TOKEN = "fcm_token";

    public static final String KEY_BLOOD_OXYGEN_CONCENTRATION = "blood_oxygen_concentration";
    public static final String KEY_HEART_RATE = "heart_rate";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_OVERALL_HEALTH_STATUS = "overall_health_status";


    public static final String KEY_PREFERENCE_NAME = "videoMeetingPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";

    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key=AAAAIKEXrD0:APA91bHCM2oxlbTtbGGlCZwF8kJnFUubgA8uV_59LrzQKk6h3wHHhb0cATRo78CGR1i-hqK2qox-bGjD_FymSFAqJdL7aPLKNuARbkCzxRX86SVighSlcEy58VNlqjK-n9uPfOMPyYJI"
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json");
        return headers;

    }
}
