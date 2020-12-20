package edu.um.feri.pora.foodtinder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.um.feri.pora.foodtinder.rvadapters.MessagesAdapter;
import edu.um.feri.pora.foodtinder.MyApplication;
import edu.um.feri.pora.foodtinder.R;
import edu.um.feri.pora.lib.Message;

public class MessagingActivity extends AppCompatActivity {
    private MyApplication app;
    String convId;

    private RecyclerView recyclerView;
    private MessagesAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        getSupportActionBar().hide();

        convId = getIntent().getExtras().getString("id");

        app = (MyApplication) getApplication();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.messagingContainer);
        //recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new MessagesAdapter(convId, app.getUser().getId());

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public void sendMessage(View view){
        EditText messageEditText = (EditText)findViewById(R.id.messageEditText);

        if(messageEditText.getText().length() != 0) {
            String body = messageEditText.getText().toString();
            Message msg = new Message(app.getUser(), body);
            databaseRef.child("conversations").child(convId).child("messages").push().setValue(msg);
            messageEditText.setText("");
        }
    }
}