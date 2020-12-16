package edu.um.feri.pora.foodtinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp(View view){
        Log.i(TAG, "sing up clicked");

        EditText emailEditText = (EditText)findViewById(R.id.emailEditText);
        EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);

        if(emailEditText.getText().length() != 0 && passwordEditText.getText().length() != 0) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                                /*FirebaseUser user = mAuth.getCurrentUser();
                                TextView errorTextView = (TextView)findViewById(R.id.errorTextView);
                                errorTextView.setText(user.getEmail());*/
                                finish();
                            } else {

                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                                ((TextView)findViewById(R.id.errorTextView)).setText(task.getException().getMessage());
                                //updateUI(null);
                            }
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
        }

    }
}