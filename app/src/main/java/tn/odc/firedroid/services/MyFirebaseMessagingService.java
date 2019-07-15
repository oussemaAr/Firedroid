package tn.odc.firedroid.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tn.odc.firedroid.utils.Utils;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";


    /**
     * Called when a notification is received
     *
     * @param remoteMessage the notification received form the FCM server
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "onMessageReceived: " + remoteMessage.getMessageId());
        String content = String.format("%s send a message to the group", remoteMessage.getData().get("username"));
        Utils.createNotification(this, content);
    }


    /**
     * Called when a new token is delivered due to invalidation of the old one
     *
     * @param s the new Token
     */
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "onNewToken: " + s);
    }
}
