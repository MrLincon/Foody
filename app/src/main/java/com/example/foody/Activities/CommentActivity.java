package com.example.foody.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.foody.Models.Comment;
import com.example.foody.Models.CommentAdapter;
import com.example.foody.R;
import com.example.foody.Tabs.FragmentFeed;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String ID, userID;

    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        recyclerView = findViewById(R.id.comment_recyclerview);

        final Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        ID = intent.getStringExtra(DetailsActivity.EXTRA_ID);
        Toast.makeText(this, ID, Toast.LENGTH_SHORT).show();

        db = FirebaseFirestore.getInstance();


        loadComments();
    }

    private void loadComments() {
        CollectionReference comments = db.collection("Comments");

        Query query = comments.whereEqualTo("post_id", ID);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<Comment> options = new FirestorePagingOptions.Builder<Comment>()
                .setQuery(query, config, Comment.class)
                .build();

        adapter = new CommentAdapter(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}