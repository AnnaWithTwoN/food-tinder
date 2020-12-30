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

    //Pozdravljeni, imam vprasanje glede 9 vaje PORA - jaz bi zelela imeti service, ki bi ves cas tekel in poslusal dogodke od baze. Zaradi nove police od androida jaz ne morem uporabiti navaden service, ker operacijska sistema ga bo ubila. Kar bi uporabila namesto tega? Foreground service mi ni vsec, ker moram kazati grdo obvestilo, jobService pa ne more teci nenehno

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        getSupportActionBar().hide();

        convId = getIntent().getExtras().getString("convId");

        app = (MyApplication) getApplication();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.messagingContainer);
        //recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new MessagesAdapter(getBaseContext(), convId, app.getUser().getId());

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}