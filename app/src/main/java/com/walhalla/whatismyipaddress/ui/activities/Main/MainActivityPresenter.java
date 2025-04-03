package com.walhalla.whatismyipaddress.ui.activities.Main;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.walhalla.ui.DLog;
import com.walhalla.whatismyipaddress.R;
import com.walhalla.whatismyipaddress.notification.ButtonReceiver;

public class MainActivityPresenter {

    /**
     * final String icon = String.format(handler, data.getCode().toLowerCase());
     * Notification
     */
    private final NotificationManager mNotificationManager;

    private final int NOTIFICATION_ID = 123321;
    private final String CHANNEL_ID = "channel_ip_info";

    private final View view;

    public MainActivityPresenter(AppCompatActivity a, View view) {
        this.view = view;
        mNotificationManager = (NotificationManager) a.getSystemService(Context.NOTIFICATION_SERVICE);
        //mNotificationManager = a.getSystemService(NotificationManager.class);
        createNotificationChannel(a, mNotificationManager);
    }


    private void createNotificationChannel(Context context, NotificationManager mNotificationManager) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_desc);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    //NotificationManager.IMPORTANCE_DEFAULT //with sounds notification
                    NotificationManager.IMPORTANCE_LOW //without sounds notification
            );
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public void notifyMaker(AppCompatActivity context, Class<?> cls, String message) {
        Intent intent = new Intent(context, cls);
        //Content
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(cls);


        //Android 12  [ api >= 23]
        final int flag0 = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.M
                ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;


        final int flag1 = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.M
                ? 0 | PendingIntent.FLAG_IMMUTABLE
                : 0;

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder
                .getPendingIntent(0, flag0);
        //Close button
        Intent buttonIntent = new Intent(context, ButtonReceiver.class);
        buttonIntent.putExtra("notificationId", NOTIFICATION_ID);

        //Create the PendingIntent
        PendingIntent REMOVE_INTENT = PendingIntent.getBroadcast(
                context.getApplicationContext(), NOTIFICATION_ID, buttonIntent, flag1);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(
                context, 0, intent, flag0
        );

        // The PendingIntent to launch activity.
        //            PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
        //                    new Intent(this, SubdomainActivity.class), 0);


        int mm = context.getResources().getIdentifier("ic_notification2", "drawable", context.getPackageName());

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .addAction(R.drawable.ic_open_in_browser, context.getString(R.string.app_name),
                                // activityPendingIntent
                                resultPendingIntent
                        )
                        .addAction(R.drawable.ic_cancel_black_24, context.getString(R.string.remove_battery_info),
                                // servicePendingIntent
                                REMOVE_INTENT
                        )
                        .setTicker(message)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(context.getString(R.string.label_public_ip))
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setOngoing(true);

        if (mm > 0) {
            //@@@mBuilder.setSmallIcon(R.drawable.ic_notification2);
            mBuilder.setSmallIcon(mm);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(CHANNEL_ID); // Channel ID
        }

        mBuilder.setContentIntent(resultPendingIntent);
        // mId allows you to update the notification later on.
        try {
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } catch (Exception e) {
            DLog.d("@@@w" + e.getLocalizedMessage());
        }
    }


    public interface View {
        void showProgressBar();
    }
}
