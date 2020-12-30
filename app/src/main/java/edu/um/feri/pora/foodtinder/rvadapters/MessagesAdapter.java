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

    public MessagesAdapter(){}

    public MessagesAdapter(Context context, final String convId, final String userId){
        this.context = context;
        this.userId = userId;
        messages = new ArrayList<Message>();
        FirebaseDatabase.getInstance().getReference("conversations").child(convId).child("opened").setValue(true);

        FirebaseDatabase.getInstance().getReference("conversations").child(convId).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message newMsg = snapshot.getValue(Message.class);
                messages.add(newMsg);
                notifyItemInserted(messages.size() - 1);

                // set message to seen
                if(!newMsg.isSeen() && !newMsg.getSender().equals(userId)) {
                    newMsg.setSeen(true);
                }

                FirebaseDatabase.getInstance().getReference("conversations").child(convId).child("messages").child(snapshot.getKey()).setValue(newMsg);
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