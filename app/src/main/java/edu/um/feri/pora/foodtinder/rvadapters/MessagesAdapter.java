package edu.um.feri.pora.foodtinder.rvadapters;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.um.feri.pora.foodtinder.MyApplication;
import edu.um.feri.pora.foodtinder.R;
import edu.um.feri.pora.foodtinder.activities.MessagingActivity;
import edu.um.feri.pora.lib.Message;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<Message> messages;
    private String userId;
    private Context context;
    Bitmap senderImage;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userTextView;
        TextView dateStampTextView;
        TextView bodyTextView;
        ConstraintLayout messageitem;
        ConstraintLayout messageContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            this.dateStampTextView = (TextView) itemView.findViewById(R.id.dateStampTextView);
            this.bodyTextView = (TextView) itemView.findViewById(R.id.bodyTextView);
            this.messageitem = (ConstraintLayout) itemView.findViewById(R.id.messageItem);
            this.messageContainer = (ConstraintLayout) itemView.findViewById(R.id.messageContainer);
        }
    }

    public MessagesAdapter(){

    }

    public MessagesAdapter(Context context, String convId, final String userId){
        this.context = context;
        this.userId = userId;
        messages = new ArrayList<Message>();

        FirebaseDatabase.getInstance().getReference("conversations").child(convId).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message newMsg = snapshot.getValue(Message.class);
                messages.add(newMsg);
                notifyItemInserted(messages.size() - 1);

                if(!newMsg.getSender().getId().equals(userId)) {
                    createNotification(newMsg.getSender().getName(), newMsg.getBody(), newMsg.getSender().getPhotoUri());
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

    private void createNotification(String title, String body, String photoUri){
        Picasso.get().load(photoUri).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                senderImage = bitmap;
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {}
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });

        Intent intent = new Intent(context, MessagingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // put extras: id of conversation
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message_icon)
                .setLargeIcon(senderImage)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MyApplication.notificationId++, builder.build());
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.message_item, parent, false);
        MessagesAdapter.ViewHolder viewHolder = new MessagesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {

        Message current = messages.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(current.getDateStamp());

        holder.dateStampTextView.setText(formattedDate);
        holder.userTextView.setText(current.getSender().getName());
        holder.bodyTextView.setText(current.getBody());


        ConstraintLayout constraintLayout = holder.messageContainer;
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        // проблема в том, что userId магически не тот
        if(current.getSender().getId().equals(userId)) {
            holder.messageitem.setBackgroundResource(R.drawable.msg_user_style);
            constraintSet.connect(R.id.messageItem, ConstraintSet.END, R.id.messageContainer, ConstraintSet.END,0);
        } else {
            holder.messageitem.setBackgroundResource(R.drawable.msg_opponent_style);
            constraintSet.connect(R.id.messageItem, ConstraintSet.START, R.id.messageContainer, ConstraintSet.START,0);
        }
        constraintSet.applyTo(constraintLayout);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}