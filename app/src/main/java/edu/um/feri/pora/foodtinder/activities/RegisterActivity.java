package edu.um.feri.pora.foodtinder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import edu.um.feri.pora.foodtinder.R;
import edu.um.feri.pora.lib.User;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String TAG = "Register";
    private EditText name;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imagePreview;
    String userId;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        name = (EditText) findViewById(R.id.nameEditText);
        imagePreview = (ImageView) findViewById(R.id.imagePreview);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public void signUp(View view){
        Log.i(TAG, "sing up clicked");

        EditText emailEditText = (EditText)findViewById(R.id.emailEditText);
        EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);

        // TODO and image is choosen
        if(emailEditText.getText().length() != 0 && passwordEditText.getText().length() != 0 && name.getText().length() != 0 && imageUri != null) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            Task<AuthResult> res =  mAuth.createUserWithEmailAndPassword(email, password);

            while(!res.isComplete())
                continue;

            if (res.isSuccessful()) {

                userId = res.getResult().getUser().getUid();
                Log.d(TAG, "createUserWithEmail: " + userId);

                // upload image
                if (imageUri != null) {
                    StorageReference fileReference = storageRef.child("photos").child(userId + "." + getFileExtension(imageUri));
                    fileReference.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //Toast.makeText(getBaseContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful());
                                    Uri downloadUrl = urlTask.getResult();

                                    //user id, name, photoUrl
                                    User user = new User(userId, name.getText().toString(), downloadUrl.toString());
                                    databaseRef.child("users").child(userId).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.w(TAG, "createUserWithEmail:failure", res.getException());
                Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                ((TextView)findViewById(R.id.errorTextView)).setText(res.getException().getMessage());
            }

        }
        else {
            Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
        }

    }

    public void openFileChooser(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imagePreview);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}