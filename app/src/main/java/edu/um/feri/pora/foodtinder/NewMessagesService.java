package edu.um.feri.pora.foodtinder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import edu.um.feri.pora.foodtinder.activities.MessagingActivity;
import edu.um.feri.pora.lib.Conversation;
import edu.um.feri.pora.lib.Message;

public class NewMessagesService extends Service {
    String userId;
    private Bitmap senderImage;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<String> convIds = intent.getExtras().getStringArrayList("convIds");
        userId = intent.getExtras().getString("userId");

        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_SERVICE_ID)
                .setSmallIcon(R.drawable.ic_android)
                .setContentTitle("Foreground service")
                .setContentText("New messages listener")
                .build();

        startForeground(1, notification);

        // do work here
        // listen to new matches
        FirebaseDatabase.getInstance().getReference("users").child(userId).child("conversations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String newConvId = snapshot.getValue(String.class);

                if (true) {
                    createNewConvNotification("You have a new match!", "Check it out!", newConvId);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // listen to new messages
        for(final String convId: convIds) {
            FirebaseDatabase.getInstance().getReference("conversations").child(convId).child("messages").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Message newMsg = snapshot.getValue(Message.class);

                    if (!newMsg.getSender().getId().equals(userId)) {
                        createNewMsgNotification(newMsg.getSender().getName(), newMsg.getBody(), newMsg.getSender().getPhotoUri(), convId);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }


        return START_NOT_STICKY;
    }

    private void createNewMsgNotification(final String title, final String body, final String photoUri, final String convId){
        Picasso.get().load(photoUri).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                senderImage = bitmap;
                Intent intent = new Intent(NewMessagesService.this, MessagingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // put extras: id of conversation
                intent.putExtra("convId", convId);
                PendingIntent pendingIntent = PendingIntent.getActivity(NewMessagesService.this, 0, intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(NewMessagesService.this, MyApplication.CHANNEL_MESSAGES_ID)
                        .setSmallIcon(R.drawable.ic_message_icon)
                        .setLargeIcon(senderImage)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(MyApplication.notificationId++, builder.build());
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
    }

    private void createNewConvNotification(final String title, final String body, String convId){

        Intent intent = new Intent(NewMessagesService.this, MessagingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // put extras: id of conversation
        intent.putExtra("convId", convId);
        PendingIntent pendingIntent = PendingIntent.getActivity(NewMessagesService.this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(NewMessagesService.this, MyApplication.CHANNEL_MESSAGES_ID)
                .setSmallIcon(R.drawable.ic_message_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MyApplication.notificationId++, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
