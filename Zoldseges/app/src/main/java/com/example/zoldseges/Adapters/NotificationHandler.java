package com.example.zoldseges.Adapters;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.zoldseges.Activitys.UserManagement.OrdersActivity;
import com.example.zoldseges.R;

public class NotificationHandler {

    private final NotificationManager notificationManager; // Manages notifications

    private final String channelId = "CustomerOrder"; // Channel ID for notification
    private final Context context; // Application context

    // Constructor to initialize context and notification manager
    public NotificationHandler(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel(); // Creates notification channel for devices with API 26+
    }

    // Creates notification channel for devices running Android O or later
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Order Channel", NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableLights(true); // Enables notification lights
            channel.enableVibration(true); // Enables vibration for notifications
            channel.setLightColor(Color.BLUE); // Sets the light color for the notification
            channel.setDescription("Új rendelés értesítés!");  // Channel description
            this.notificationManager.createNotificationChannel(channel);
        }
    }

    // Sends a notification to the user about a successful order
    public void sendNotification() {
        Intent intent = new Intent(context, OrdersActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification with title, content, and action
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // Notification icon
                .setContentTitle("Sikeres Rendelés!")  // Title of the notification
                .setContentText("Rendelésed beérkezett a rendszerünkbe, ha át szeretnéd tekinteni előző rendeléseidet azt a fiókodon belül teheted meg!") // Notification content
                .setContentIntent(pendingIntent) // Opens OrdersActivity when clicked
                .setAutoCancel(true); // Auto-cancels notification when clicked

        // Notify user with the built notification
        this.notificationManager.notify(1, builder.build());
    }
}
