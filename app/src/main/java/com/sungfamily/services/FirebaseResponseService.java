package com.sungfamily.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sungfamily.R;
import com.sungfamily.constants.Constant;
import com.sungfamily.events.ChatMessageReceivedEvent;
import com.sungfamily.models.ChatMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;


public class FirebaseResponseService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Map<String,String> data = remoteMessage.getData();
        if (data.containsKey("message"))
        {
            String message = data.get("message");
            notifyUser(message);
            ChatMessage chatMessage = new ChatMessage(message, false, false);
            chatMessage.setRecipientToken(Constant.RECIPIENT_FB_TOKEN);
            EventBus.getDefault().post(new ChatMessageReceivedEvent(chatMessage));
        }
    }

    private void notifyUser(String messageBody)
    {
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("MY_NOTIFICATION_CHANNEL_ID", "Notification Channel",
                    NotificationManager.IMPORTANCE_MAX);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setWhen(System.currentTimeMillis());
            notificationBuilder.setChannelId("MY_NOTIFICATION_CHANNEL_ID");
        }

        notificationManager.notify(9999, notificationBuilder.build());

    }


}
