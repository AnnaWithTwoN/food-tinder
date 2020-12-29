package edu.um.feri.pora.foodtinder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.um.feri.pora.foodtinder.rvadapters.ConversationsAdapter;
import edu.um.feri.pora.foodtinder.MyApplication;
import edu.um.feri.pora.foodtinder.R;
import edu.um.feri.pora.lib.User;

public class ConversationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ConversationsAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private DatabaseReference databaseRef;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        getSupportActionBar().hide();

        app = (MyApplication) getApplication();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.conversationsContainer);
        //recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new ConversationsAdapter(app.getUser().getId());
        recyclerViewAdapter.setOnItemClickListener(new ConversationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View item, int position) {
                User user = app.getUser();
                String id = user.getConversations().get(position);
                Intent i = new Intent(ConversationsActivity.this, MessagingActivity.class);
                i.putExtra("convId", id);
                startActivity(i);
            }
        });

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}