package com.example.foody.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foody.Activities.ProfileActivityView;
import com.example.foody.Models.Feed;
import com.example.foody.Models.FeedRecyclerDecoration;
import com.example.foody.Models.Restaurant;
import com.example.foody.Models.RestaurantAdapter;
import com.example.foody.R;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FragmentRestaurants extends Fragment {

    View view;
    RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;
    SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference restaurant = db.collection("RestaurantDetails");

    private RestaurantAdapter adapter;

    public static final String EXTRA_ID = "com.example.foody.EXTRA_ID";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurants, container, false);

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

        Query query = restaurant.orderBy("name", Query.Direction.ASCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<Restaurant> options = new FirestorePagingOptions.Builder<Restaurant>()
                .setQuery(query, config, Restaurant.class)
                .build();

        adapter = new RestaurantAdapter(options,swipeRefreshLayout);
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

        adapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                String id = documentSnapshot.getId();

                Intent intent = new Intent(getActivity(), ProfileActivityView.class);

                String name = restaurant.getName();
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
