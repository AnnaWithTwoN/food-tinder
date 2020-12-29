package edu.um.feri.pora.foodtinder;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import edu.um.feri.pora.foodtinder.activities.MessagingActivity;
import edu.um.feri.pora.lib.User;

public class MyApplication extends Application {
    public static final String CHANNEL_MESSAGES_ID = "Messages";
    public static final String CHANNEL_SERVICE_ID = "Services";
    public static int notificationId = 2;
    private User user;

    public MyApplication() {
        //FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerNotificationChannels();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void registerNotificationChannels() {
        // Configure the channel
        NotificationChannel channelMessages = new NotificationChannel(CHANNEL_MESSAGES_ID, "Messages Channel", NotificationManager.IMPORTANCE_HIGH);
        NotificationChannel channelServices = new NotificationChannel(CHANNEL_SERVICE_ID, "Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
        channelMessages.setDescription("New message");
        channelServices.setDescription("Foreground service");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(channelMessages);
        mNotificationManager.createNotificationChannel(channelServices);
    }

    private void createNotification(int id, int iconResource, String title, String body, String channelId) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId);
        mBuilder.setSmallIcon(iconResource).setContentTitle(title).setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
