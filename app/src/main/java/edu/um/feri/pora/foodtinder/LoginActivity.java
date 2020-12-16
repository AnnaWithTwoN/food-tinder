package edu.um.feri.pora.foodtinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(View view){
        Log.i("Login", "sing in clicked");

        EditText emailEditText = (EditText)findViewById(R.id.emailEditText);
        EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);

        if(emailEditText.getText().length() != 0 && passwordEditText.getText().length() != 0) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(getApplicationContext(), "Authentication successful", Toast.LENGTH_SHORT).show();
                                //FirebaseUser user = mAuth.getCurrentUser();
                                finish();
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                ((TextView)findViewById(R.id.errorTextView)).setText(task.getException().getMessage());
                                //updateUI(null);
                            }
                        }
                    });
        }

    }

    public void goToRegister(View view){
        Intent i = new Intent(getBaseContext(), RegisterActivity.class);
        startActivity(i);
    }
}