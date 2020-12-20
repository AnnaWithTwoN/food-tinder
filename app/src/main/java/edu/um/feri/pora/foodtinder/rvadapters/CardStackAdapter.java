package edu.um.feri.pora.foodtinder.rvadapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.um.feri.pora.foodtinder.R;
import edu.um.feri.pora.lib.User;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<User> items;

    public CardStackAdapter(List<User> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView nameTextView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.photo);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }

        void setData(User user) {
            Picasso.get().load(Uri.parse(user.getPhotoUri())).into(image);
            nameTextView.setText(user.getName());
        }
    }

    public List<User> getItems() {
        return items;
    }

    public void setItems(List<User> items) {
        this.items = items;
    }
}