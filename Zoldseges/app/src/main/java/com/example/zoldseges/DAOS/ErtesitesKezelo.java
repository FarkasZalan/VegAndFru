package com.example.zoldseges.DAOS;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.zoldseges.Activitys.FelhasznaloKezeles.RendeleseimActivity;
import com.example.zoldseges.R;

public class ErtesitesKezelo {

    private final NotificationManager notificationManager;

    private final String channelId = "Vasarlo rendeles";
    private final Context context;

    public ErtesitesKezelo(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        ertesitesBuild();
    }

    private void ertesitesBuild() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "catorna", NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);
            channel.setDescription("Új rendelés értesítés!");
            this.notificationManager.createNotificationChannel(channel);
        }
    }

    public void ertesitesKuldes() {
        Intent intent = new Intent(context, RendeleseimActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Sikeres Rendelés!")
                .setContentText("Rendelésed beérkezett a rendszerünkbe, ha át szeretnéd tekinteni előző rendeléseidet azt a fiókodon belül teheted meg!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        this.notificationManager.notify(1, builder.build());

    }
}
