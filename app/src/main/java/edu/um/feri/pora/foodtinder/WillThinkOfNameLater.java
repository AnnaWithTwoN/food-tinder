package edu.um.feri.pora.foodtinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yuyakaido.android.cardstackview.*;

import java.util.ArrayList;
import java.util.List;

import edu.um.feri.pora.lib.Photo;
import edu.um.feri.pora.lib.User;

public class WillThinkOfNameLater extends AppCompatActivity {
    private MyApplication app;
    private User user;
    private String TAG = "Shit";
    private DatabaseReference databaseRef;
    List<User> items;
    CardStackView cardStackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_will_think_of_name_later);
        app = (MyApplication) getApplication();
        user = app.getUser();
        items = new ArrayList<>();

        cardStackView = findViewById(R.id.cardStackView);
        databaseRef = FirebaseDatabase.getInstance().getReference();

        final CardStackLayoutManager manager = new CardStackLayoutManager(this, new CardStackListener() {
            User userOnCard;

            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped:" );
                if (direction == Direction.Right){
                    Toast.makeText(WillThinkOfNameLater.this, "Direction Right", Toast.LENGTH_SHORT).show();
                    // mark this person as liked
                    userOnCard.addLikedBy(user.getId());
                    user.addLiked(userOnCard.getId());
                    databaseRef.child("users").child(userOnCard.getId()).setValue(userOnCard);
                    if(user.isLikedBy(userOnCard.getId())){
                        Toast.makeText(WillThinkOfNameLater.this, "You have a match!", Toast.LENGTH_SHORT).show();
                    }
                }
                if (direction == Direction.Left){
                    Toast.makeText(WillThinkOfNameLater.this, "Direction Left", Toast.LENGTH_SHORT).show();
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
                userOnCard = items.get(position);
                Log.d(TAG, "onCardAppeared: ");
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                //TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardDisappeared: ");
            }
        });
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

    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseRef.child("users").child(user.getId()).setValue(user);
        app.setUser(user);
    }
}