package edu.um.feri.pora.foodtinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import edu.um.feri.pora.lib.Photo;
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

        app = (MyApplication) getApplication();

        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null ){
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
        } else if(app.getUser() == null){
            databaseRef.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    app.setUser(user);
                    TextView helloTextView = (TextView) findViewById(R.id.helloTextView);
                    helloTextView.setText(user.getName());
                    ImageView image = (ImageView) findViewById(R.id.imageView);
                    Picasso.get().load(user.getPhotoUri()).into(image);
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


    public void startExploring(View view){
        Intent i = new Intent(getBaseContext(), WillThinkOfNameLater.class);
        startActivity(i);
    }

    public void logout(View view){
        mAuth.signOut();
        recreate();
    }

}