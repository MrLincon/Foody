package com.example.foody.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foody.Models.FoodMenu;
import com.example.foody.Models.FoodMenuAdapter;
import com.example.foody.R;
import com.example.foody.Tabs.FragmentFeed;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MenuActivityView extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView back;
    private RecyclerView menuRecyclerview;
    SwipeRefreshLayout swipeRefreshLayout;

    Dialog popup;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String ID,userID;

    private FoodMenuAdapter adapter;
    private CollectionReference menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_view);

        toolbar = findViewById(R.id.toolbar_simple);
        toolbarTitle = findViewById(R.id.toolbar_title);
        back = findViewById(R.id.back);
        menuRecyclerview = findViewById(R.id.menu_recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        toolbarTitle.setText("Menu");

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        db = FirebaseFirestore.getInstance();

        final Intent intent = getIntent();
        ID = intent.getStringExtra(ProfileActivityView.EXTRA_ID_Restaurant);

        //Load food menu
        loadData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        popup = new Dialog(this);


    }

    private void loadData() {

        menu = db.collection("RestaurantDetails").document(ID).collection("Menu");

        Query query = menu.orderBy("timestamp", Query.Direction.ASCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(15)
                .build();

        FirestorePagingOptions<FoodMenu> options = new FirestorePagingOptions.Builder<FoodMenu>()
                .setQuery(query, config, FoodMenu.class)
                .build();

        adapter = new FoodMenuAdapter(options, swipeRefreshLayout);
        menuRecyclerview.setHasFixedSize(true);
        menuRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        menuRecyclerview.setAdapter(adapter);
        adapter.startListening();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}