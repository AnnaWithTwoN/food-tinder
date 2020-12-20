package edu.um.feri.pora.foodtinder.rvadapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import edu.um.feri.pora.foodtinder.R;
import edu.um.feri.pora.lib.Conversation;
import edu.um.feri.pora.lib.User;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder>{

    private List<Conversation> conversations;
    String userId;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        public void onItemClick(View item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userTextView;
        ImageView pictureImageView;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            this.userTextView = (TextView) itemView.findViewById(R.id.userTextView);
            this.pictureImageView = (ImageView) itemView.findViewById(R.id.pictureImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.i("click", "Was clicked on " + position);
                    if(listener != null) {
                        listener.onItemClick(v, position);
                    }
                }
            });
        }
    }

    public ConversationsAdapter(String userId){
        this.userId = userId;
        conversations = new ArrayList<Conversation>();

        // get list of conversations of current user
        FirebaseDatabase.getInstance().getReference("users").child(userId).child("conversations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(final DataSnapshot conv: snapshot.getChildren()){
                    // find conversation
                    FirebaseDatabase.getInstance().getReference("conversations").child(conv.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            conversations.add(snapshot.getValue(Conversation.class));
                            notifyItemInserted(conversations.size() - 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @NonNull
    @Override
    public ConversationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_conversation, parent, false);
        ConversationsAdapter.ViewHolder viewHolder = new ConversationsAdapter.ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationsAdapter.ViewHolder holder, int position) {
        Conversation current = conversations.get(position);

        User opponent = current.getOpponentA().getId().equals(userId) ? current.getOpponentB() : current.getOpponentA();
        holder.userTextView.setText(opponent.getName());
        Picasso.get().load(opponent.getPhotoUri()).into(holder.pictureImageView);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

}
