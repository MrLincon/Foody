package com.example.foody.Tabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foody.Activities.DetailsActivity;
import com.example.foody.Models.BottomNavigationViewBehavior;
import com.example.foody.Models.Feed;
import com.example.foody.Models.FeedAdapter;
import com.example.foody.Models.FeedRecyclerDecoration;
import com.example.foody.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;


public class FragmentFeed extends Fragment {

    View view;
    RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;
    SwipeRefreshLayout swipeRefreshLayout;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference feed = db.collection("Feed");

    private FeedAdapter adapter;


    public static final String EXTRA_ID = "com.example.foody.EXTRA_ID";
//    public static final String EXTRA_NAME = "com.example.foody.EXTRA_NAME";
//    public static final String EXTRA_RESTAURANT = "com.example.foody.EXTRA_RESTAURANT";
//    public static final String EXTRA_DETAILS = "com.example.foody.EXTRA_DETAILS";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView = view.findViewById(R.id.feed_recyclerview);
        int topPadding = getResources().getDimensionPixelSize(R.dimen.topPadding);
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.bottomPadding);
        recyclerView.addItemDecoration(new FeedRecyclerDecoration(topPadding, bottomPadding));

        bottomNavigationView = (getActivity()).findViewById(R.id.bottom_navigation);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        getFeed();

        return view;
    }

    private void getFeed() {

        Query query = feed.orderBy("timestamp", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<Feed> options = new FirestorePagingOptions.Builder<Feed>()
                .setQuery(query, config, Feed.class)
                .build();

        adapter = new FeedAdapter(options,swipeRefreshLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });

        adapter.setOnItemClickListener(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                String id = documentSnapshot.getId();
                Feed feed = documentSnapshot.toObject(Feed.class);

                Intent intent = new Intent(getActivity(), DetailsActivity.class);

                String name = feed.getName();
//                String restaurant = feed.getRestaurant();
//                String details = feed.getDetails();

                intent.putExtra(EXTRA_ID, id);
//                intent.putExtra(EXTRA_NAME, name);
//                intent.putExtra(EXTRA_RESTAURANT, name);
//                intent.putExtra(EXTRA_DETAILS, details);

                startActivity(intent);
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
