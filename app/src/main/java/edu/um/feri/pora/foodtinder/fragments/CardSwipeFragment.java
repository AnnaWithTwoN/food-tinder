package edu.um.feri.pora.foodtinder.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.*;

import java.util.ArrayList;
import java.util.List;

import edu.um.feri.pora.foodtinder.activities.ExplorerActivity;
import edu.um.feri.pora.foodtinder.activities.MessagingActivity;
import edu.um.feri.pora.foodtinder.adapters.CardStackAdapter;
import edu.um.feri.pora.foodtinder.MyApplication;
import edu.um.feri.pora.foodtinder.R;
import edu.um.feri.pora.lib.Conversation;
import edu.um.feri.pora.lib.User;

public class CardSwipeFragment extends Fragment {
    private MyApplication app;
    private User user;
    private User userOnCard;
    private User prevUserOnCard;
    private String TAG = "ExplorerActivity";
    private DatabaseReference databaseRef;
    List<User> items;
    CardStackView cardStackView;

    public static CardSwipeFragment newInstance() {
        CardSwipeFragment fragment = new CardSwipeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_card_swiper, container, false);

        app = ((MyApplication)((ExplorerActivity) requireActivity()).getApplication());
        user = app.getUser();

        items = new ArrayList<>();

        cardStackView = root.findViewById(R.id.cardStackView);
        databaseRef = FirebaseDatabase.getInstance().getReference();

        final CardStackLayoutManager manager = new CardStackLayoutManager(getContext(), cardStackListener);
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        cardStackView.setLayoutManager(manager);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

        databaseRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    if(data.getKey().equals(user.getId()) || user.hasLiked(data.getKey())) continue;
                    User p = data.getValue(User.class);
                    items.add(p);
                }

                CardStackAdapter adapter = new CardStackAdapter(items);
                cardStackView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO error handling
            }
        });

        return root;
    }


    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    // here should be prevUser cause card appears before dialog is shown
                    //if(userOnCard.getId().equals(items.get(items.size() - 1).getId()))
                    //    prevUserOnCard = userOnCard;

                    String id = databaseRef.child("conversations").push().getKey();
                    Conversation conv = new Conversation(id, user, prevUserOnCard);
                    databaseRef.child("conversations").child(id).setValue(conv);
                    user.addConversation(id);
                    prevUserOnCard.addConversation(id);
                    databaseRef.child("users").child(user.getId()).setValue(user);
                    databaseRef.child("users").child(prevUserOnCard.getId()).setValue(prevUserOnCard);

                    Intent i = new Intent(getContext(), MessagingActivity.class);
                    i.putExtra("convId", id);
                    startActivity(i);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    CardStackListener cardStackListener = new CardStackListener() {

        @Override
        public void onCardDragging(Direction direction, float ratio) {
            Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
        }

        @Override
        public void onCardSwiped(Direction direction) {
            Log.d(TAG, "onCardSwiped:" );
            if (direction == Direction.Right){
                //Toast.makeText(ExplorerActivity.this, "Direction Right", Toast.LENGTH_SHORT).show();
                // mark this person as liked
                userOnCard.addLikedBy(user.getId());
                user.addLiked(userOnCard.getId());
                databaseRef.child("users").child(userOnCard.getId()).setValue(userOnCard);
                databaseRef.child("users").child(user.getId()).setValue(user);
                if(user.isLikedBy(userOnCard.getId())){
                    //Toast.makeText(ExplorerActivity.this, "You have a match!", Toast.LENGTH_SHORT).show();
                    prevUserOnCard = userOnCard;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("You have a match! Start dialog?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            }
            if (direction == Direction.Left){
                //Toast.makeText(ExplorerActivity.this, "Direction Left", Toast.LENGTH_SHORT).show();
            }

            // Paginating
                /*if (manager.getTopPosition() == adapter.getItemCount() - 5){
                    paginate();
                }*/

        }

        @Override
        public void onCardRewound() {
            Log.d(TAG, "onCardRewound: ");
        }

        @Override
        public void onCardCanceled() {
            Log.d(TAG, "onCardRewound: ");
        }

        @Override
        public void onCardAppeared(View view, int position) {
            //prevUserOnCard = userOnCard;
            userOnCard = items.get(position);
            Log.d(TAG, "onCardAppeared: " + userOnCard.getName());
        }

        @Override
        public void onCardDisappeared(View view, int position) {
            Log.d(TAG, "onCardDisappeared: ");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseRef.child("users").child(user.getId()).setValue(user);
        app.setUser(user);
    }
}