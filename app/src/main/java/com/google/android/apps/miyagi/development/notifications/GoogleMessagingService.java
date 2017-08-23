package com.google.android.apps.miyagi.development.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.helpers.AnalyticsHelper;
import com.google.android.apps.miyagi.development.ui.splash.SplashScreenActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by emilzawadzki on 08.02.2017.
 */

public class GoogleMessagingService extends FirebaseMessagingService {

    @Inject Lazy<AnalyticsHelper> mAnalyticsHelper;
    private int mNotificationId = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        ((GoogleApplication) getApplication()).getAppComponent().inject(this);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = notification != null ? notification.getTitle() : "";
        String content = notification != null ? notification.getBody() : "";

        mAnalyticsHelper.get().trackEvent(null, getString(R.string.event_category_push), getString(R.string.event_action_open), content);

        showSystemNotification(title, content);
    }

    private void showSystemNotification(String notificationTitle, String notificationBody) {

        Intent actionIntent = new Intent(GoogleApplication.getInstance(), SplashScreenActivity.class);
        actionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //check if system can handle the intent
        if (actionIntent.resolveActivity(getPackageManager()) != null) {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, actionIntent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder
                    .setContentTitle(notificationTitle)
                    .setSmallIcon(R.drawable.ic_noti_small_icon)
                    .setLargeIcon(largeIcon)
                    .setContentText(notificationBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(++mNotificationId, notificationBuilder.build());
        }
    }
}