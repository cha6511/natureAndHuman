package human.nature.customerorderapp.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import human.nature.customerorderapp.AppSharedPreference;
import human.nature.customerorderapp.LoginActivity;
import human.nature.customerorderapp.R;
import human.nature.customerorderapp.StoreListActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    NotificationCompat.Builder notificationBuilder;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("message", remoteMessage.getData().toString());
        sendPushNotification("주문 알림", remoteMessage.getData().get("content"));
    }
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }

    private void sendPushNotification(String title, String message){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent();
        AppSharedPreference spf = new AppSharedPreference(this);
        if (!spf.isLogin()) {
            intent = new Intent(this, LoginActivity.class);
            // 푸쉬를 눌렀을 때 이동할 화면/
        } else {
            intent = new Intent(this, StoreListActivity.class);  // 푸쉬를 눌렀을 때 이동할 화면/
            Intent QQ = new Intent("INTENT_FILTER");
            sendBroadcast(QQ);
        }



        intent.putExtra("click", "refresh");

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelID = "CustomerOderApp";
            String channelName = "getMsg";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelID, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);

            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelID)
                    .setSmallIcon(getNotificationIcon())
                    .setTicker(title)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pendingIntent);
        } else {
            notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(getNotificationIcon())
                    .setTicker(title)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pendingIntent);
        }

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "PushIdentificator");
        wakeLock.acquire(6000);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        wakeLock.release();
    }
}
