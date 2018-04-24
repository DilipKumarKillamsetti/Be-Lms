package com.mahindra.be_lms.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.mahindra.be_lms.MyApplication;
import com.mahindra.be_lms.R;
import com.mahindra.be_lms.activities.DashboardActivity;
import com.mahindra.be_lms.activities.LoginActivity;
import com.mahindra.be_lms.activities.MainActivity;
import com.mahindra.be_lms.activities.NotificationDialogActivity;
import com.mahindra.be_lms.db.Notifications;
import com.mahindra.be_lms.db.Profile;
import com.mahindra.be_lms.db.Queries;
import com.mahindra.be_lms.db.QueriesDao;
import com.mahindra.be_lms.db.QueryResponse;
import com.mahindra.be_lms.db.User;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.lib.MySharedPreference;
import com.mahindra.be_lms.lib.WebViewCachePref;
import com.mahindra.be_lms.util.Constants;
import com.mahindra.be_lms.util.DBHelper;
import com.mahindra.be_lms.util.ImageHelper;
import com.mahindra.be_lms.volley.VolleySingleton;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Pravin on 10/26/16.
 * Updated by Chaitali
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // new MySharedPreference(this).putPref("payload", remoteMessage.getData().toString());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        //clear notification type in preferences

        Log.e("JSON_OBJECT====", remoteMessage.getData().toString());
        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        Log.e("JSON_OBJECT", object.toString());
        if (MyApplication.isActivityVisible()) {
            MyApplication.mySharedPreference.setNotificationType("");
            L.l(TAG, "App is opened");
            DBHelper dbHelper = new DBHelper();
//            String messageBody = remoteMessage.getData().get("body");
//            String title = remoteMessage.getData().get("title");
            String messageBody = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();
            MyApplication.mySharedPreference.setDialogTitle(title);
            MyApplication.mySharedPreference.setDialogMessage(messageBody);
           // final String notificationType = SortNotification(remoteMessage, messageBody, title, dbHelper);
            Intent intent = null;
            intent = new Intent(this, NotificationDialogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("", true);
            startActivity(intent);
        } else {

            L.l(TAG, "App is closed");
            if (remoteMessage.getData().containsKey("notificationType")) {
                Log.d(TAG, "onMessageReceived: Notification Type" + remoteMessage.getData().get("notificationType"));
                if (remoteMessage.getData().get("type").equals("General") && remoteMessage.getData().get("notificationType").equals("2")) {

                    Log.d(TAG, "onMessageReceived: CLEAR USER DATA EXPIRE NOTIFICATION SUCCESSFULLY WORK!!!!!");
                    logoutUser();
                    MyApplication.mySharedPreference.setNotificationType(remoteMessage.getData().get("notificationType"));
                }
            }
            sendNotification(remoteMessage);
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(RemoteMessage remoteMessage) {
        Log.d(TAG, "sendNotification: Method Call");

        String messageBody = remoteMessage.getData().get("body");
        String title = remoteMessage.getData().get("title");
        Intent intent = null;
        DBHelper dbHelper = new DBHelper();
        //String notificationType = SortNotification(remoteMessage, messageBody, title, dbHelper);
        if (MyApplication.mySharedPreference.checkUserLogin()) {
            intent = new Intent(this, DashboardActivity.class);
          //  intent.putExtra(notificationType, true);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Nullable
    private String SortNotification(RemoteMessage remoteMessage, String messageBody, String title, DBHelper dbHelper) {
        Log.d(TAG, "SortNotification: Method call");
        String notificationType = null;
        /*{attachmentLink==../../uploads/notification_attachment/aboutpkm_Tulips.jpg, type=General, title=hellooo, message=demo testing}*/
        if (remoteMessage.getData().containsKey("type")) {
            if (remoteMessage.getData().get("type").equals("query_replay")) {
                //data payload: {query_id=23, title=Reply for Query, message=new notification with query id, responsible_person=MADMIN}
                /*{query_id=37, type=query_replay, title=Reply for Query, attachmentLink=uploads/query_attachment/aboutpkm_Jellyfish.jpg, message=atchmnt, responsible_person=MADMIN}*/
                //without attachment {query_id=37, type=query_replay, title=Reply for Query, message=fsadfsd, responsible_person=MADMIN}
                //{query_id=48, type=query_replay, title=Reply for Query, attachmentLink=uploads/query_attachment/51_chaitali.jpg,
                // message=Give reply, responsible_person=MADMIN}
              /*  {query_id=69, type=query_replay, title=Reply for Query,
              extra_link={"144":{"Radiator cleaner and antifreeze recommendation":"1310151038260R.pdf"}},
              message=doc link, notificationType=3, responsible_person=MADMIN} */
                QueryResponse queryResponse = new QueryResponse();
                queryResponse.setQueryID(remoteMessage.getData().get("query_id"));
                queryResponse.setMessage(messageBody);
                queryResponse.setTitle(title);
                queryResponse.setMsg_type("response");
                queryResponse.setResposePerson(remoteMessage.getData().get("responsible_person"));
                if (remoteMessage.getData().containsKey("attachmentLink")) {
                    L.l(TAG, "Reply attachment present");
                    queryResponse.setQueryReplyAttachment(remoteMessage.getData().get("attachmentLink"));
                } else {
                    L.l(TAG, "Reply attachment absent");
                    queryResponse.setQueryReplyAttachment("");
                }
                if (remoteMessage.getData().containsKey("notificationType")) {
                    queryResponse.setQueryResponseType(remoteMessage.getData().get("notificationType"));
                } else {
                    queryResponse.setQueryResponseType("");
                }
                if (remoteMessage.getData().containsKey("extra_link")) {
                    queryResponse.setQueryResponseExtraLink(remoteMessage.getData().get("extra_link"));
                } else {
                    queryResponse.setQueryResponseExtraLink("");
                }
                dbHelper.saveResponse(queryResponse);
                Notifications notifications = new Notifications();
                notifications.setNotificationType(remoteMessage.getData().get("type"));
                notifications.setNotificationTitle(title);
                notifications.setNotificationMsg(messageBody);
                dbHelper.saveNotifications(notifications);
                notificationType = "query_notification";
            } else if(remoteMessage.getData().get("type").equals("Query_close")){
                Queries queries=dbHelper.getQueriesByInsertedID(remoteMessage.getData().get("queryid"));
                queries.setQueryStatus(true);
                dbHelper.updateQuery(queries);
                Notifications notifications = new Notifications();
                notifications.setNotificationType(remoteMessage.getData().get("type"));
                notifications.setNotificationTitle(title);
                notifications.setNotificationMsg(messageBody);
                dbHelper.saveNotifications(notifications);
                notificationType = "query_notification";

            }else if (remoteMessage.getData().get("type").equals("request_for_profile")) {
                Notifications notifications = new Notifications();
                notifications.setNotificationType(remoteMessage.getData().get("type"));
                notifications.setNotificationTitle(title);
                notifications.setNotificationMsg(messageBody);
                dbHelper.saveNotifications(notifications);
                Profile profile = dbHelper.getProfileByID(remoteMessage.getData().get("profile_id"));
                profile.setProfile_approval(true);
                dbHelper.updateProfile(profile);
                notificationType = "request_for_profile_notification";
            } else if (remoteMessage.getData().get("type").equals("profile_update")) {
                Log.d(TAG, "onCreate: profile update");
                new DBHelper().deleteUser();
                User user = new Gson().fromJson(remoteMessage.getData().get("data"), User.class);
                L.l(TAG, "updated user: " + user.toString());
                new DBHelper().saveUser(user);
                MyApplication.mySharedPreference.saveUser(user.getUserID(), user.getFullname(), user.getUserRole());
                if (!TextUtils.isEmpty(user.getUserPicture())) {
                    L.l(this, "Profile Img Exists");
                    ///Bitmap bitmap=new ImageHelper().getImageBitmapFromUrl(Constants.PROFILE_UPLOAD_URL+ user.getUserPicture());
                    String url = Constants.PROFILE_UPLOAD_URL + user.getUserPicture();
                    L.l(this, "PROFILE IMAGE URL: " + url);
                    getImageBitmap(url, user.getUserPicture());
                }
                Notifications notifications = new Notifications();
                notifications.setNotificationType(remoteMessage.getData().get("type"));
                notifications.setNotificationTitle(title);
                notifications.setNotificationMsg(messageBody);
                new DBHelper().saveNotifications(notifications);
                notificationType = "profile_update_notification";
            } else if (remoteMessage.getData().get("type").equals("General")) {
//                {type=General, title=412017, attachmentLink=uploads/notification_attachment/aboutpkm_Tulips.jpg, message=Testing, notificationType=2}
               /* {type=General, title=Demo, attachmentLink=uploads/notification_attachment/aboutpkm_Desert.jpg, message=asfdsf, notificationType=1}*/
                /*{type=General, title=demooooooo testing, attachmentLink=, message=Hellllllllloooooo, notificationType=1}*/
/*                {type=General, title=testing video attachments, attachmentLink=,
extra_link={"141":{"LMS initial registration":"1305160840540R.pdf"}}, message=testing video attachments, notificationType=3}*/
                L.l(TAG, "General Notification: ");
                Notifications notifications = new Notifications();
                notifications.setNotificationType(remoteMessage.getData().get("type"));
                notifications.setNotificationTitle(title);
                notifications.setNotificationMsg(messageBody);
                notifications.setNotificationattachLink(remoteMessage.getData().get("attachmentLink"));
                notifications.setNotificationOtherType(remoteMessage.getData().get("notificationType"));
                if (remoteMessage.getData().get("notificationType").equals("3")) {
                    notifications.setNotificationextraLink(remoteMessage.getData().get("extra_link"));
                } else {
                    if (remoteMessage.getData().containsKey("extra_link")) {
                        notifications.setNotificationextraLink("");
                    } else {
                        notifications.setNotificationextraLink("");
                    }
                }
                new DBHelper().saveNotifications(notifications);
                notificationType = "General";
                MyApplication.mySharedPreference.setNotificationType(remoteMessage.getData().get("notificationType"));
            } else {
                Notifications notifications = new Notifications();
                notifications.setNotificationType(remoteMessage.getData().get("type"));
                notifications.setNotificationTitle(title);
                notifications.setNotificationMsg(messageBody);
                new DBHelper().saveNotifications(notifications);
                notificationType = "other_type_notification";
            }
        } /*else {
            Notifications notifications = new Notifications();
            notifications.setNotificationType("no_type");
            notifications.setNotificationTitle(title);
            notifications.setNotificationMsg(messageBody);
            dbHelper.saveNotifications(notifications);
            notificationType = "no_type_available_notification";
        }*/
        return notificationType;
    }

    //To get bitmap format image from url
    private void getImageBitmap(String url, final String filename) {
        L.l(this, "PROFILE PIC FLAG: " + MyApplication.flagProfilePicSet);
        ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                if (response != null) {
                    L.l("BITMAP not NULL: " + response.toString());
                    boolean imagcreated = new ImageHelper().createDirectoryAndSaveFile(response, MyApplication.mySharedPreference.getUserId() + ".png");
                    //L.l(this, "" + imagcreated);
                    MyApplication.flagProfilePicSet = imagcreated;
                    L.l("PROFIEL PIC FLAG UPDATE: " + MyApplication.flagProfilePicSet);
                } else {
                    L.dismiss_pd();
                    L.l("Bitmap NULL");
                }
            }
        }, 200, 200, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //do stuff
                        L.l("PROFILE Fetch volley error: " + error.getMessage());
                    }
                });
        imgRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getsInstance().addToRequestQueue(imgRequest);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        L.l(TAG, "userWhiteIcon: " + useWhiteIcon);
        return useWhiteIcon ? R.drawable.ic_notification_gray : R.drawable.ic_notification_gray;
    }

    private void logoutUser() {
        try {
            DBHelper dbHelper = new DBHelper();
            dbHelper.clearUserData();
            MyApplication.mySharedPreference.setUserLogin(false);
            MyApplication.mySharedPreference.putPref(MySharedPreference.DISCLAIMER_ACCEPT, false);
            MyApplication.mySharedPreference.putPref(MySharedPreference.SAFETY_ACCEPT, false);
            WebViewCachePref.newInstance(this).clearAllPref();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}