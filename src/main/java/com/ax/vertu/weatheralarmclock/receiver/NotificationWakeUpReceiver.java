package com.ax.vertu.weatheralarmclock.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ax.vertu.weatheralarmclock.R;
import com.ax.vertu.weatheralarmclock.activity.AlarmActivity;

public class NotificationWakeUpReceiver extends BroadcastReceiver {
    public NotificationWakeUpReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("闹钟启动")
                .setContentText("欢迎使用!")
                .setAutoCancel(true);
        Intent resultIntent = new Intent(context, AlarmActivity.class);
        mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT));
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(AlarmActivity.notificationId, mBuilder.build());

    }
}
