package edu.um.feri.pora.foodtinder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.um.feri.pora.foodtinder.MyApplication;
import edu.um.feri.pora.foodtinder.NewMessagesService;
import edu.um.feri.pora.foodtinder.R;
import edu.um.feri.pora.lib.User;

public class MainActivity extends AppCompatActivity {
    private MyApplication app;
    private FirebaseUser currentUser;
    private User user;
    private FirebaseAuth mAuth;
    private String TAG = "Main activity";

    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        app = (MyApplication) getApplication();

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
        } else if(app.getUser() == null){
            databaseRef.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    app.setUser(user);
                    TextView helloTextView = (TextView) findViewById(R.id.userTextView);
                    helloTextView.setText(user.getName());
                    ((TextView) findViewById(R.id.emailTextView)).setText(currentUser.getEmail());
                    ImageView image = (ImageView) findViewById(R.id.imageView);
                    Picasso.get().load(user.getPhotoUri()).into(image);

                    startNewMessagesListener();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //TODO handle error
                }
            });
        } else {
            user = app.getUser();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startNewMessagesListener(){
        // check if not currently running
        Intent serviceIntent = new Intent(this, NewMessagesService.class);
        serviceIntent.putStringArrayListExtra("convIds", (ArrayList<String>) user.getConversations());
        serviceIntent.putExtra("userId", user.getId());
        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
    }

    private void stopNewMessagesListener(){
        Intent serviceIntent = new Intent(this, NewMessagesService.class);
        stopService(serviceIntent);
    }

    public void startExploring(View view){
        Intent i = new Intent(getBaseContext(), ExplorerActivity.class);
        startActivity(i);
    }

    public void openConversations(View view){
        Intent i = new Intent(getBaseContext(), ConversationsActivity.class);
        startActivity(i);
    }

    public void logout(View view){
        mAuth.signOut();
        app.setUser(null);
        stopNewMessagesListener();
        recreate();
    }

}