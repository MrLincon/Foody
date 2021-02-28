package com.example.foody.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foody.Activities.DetailsActivityOffer;
import com.example.foody.Models.Feed;
import com.example.foody.Models.FeedAdapter;
import com.example.foody.Models.FeedRecyclerDecoration;
import com.example.foody.Models.Offer;
import com.example.foody.Models.OfferAdapter;
import com.example.foody.R;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class FragmentOffers extends Fragment {

    View view;
    RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;
    SwipeRefreshLayout swipeRefreshLayout;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference feed = db.collection("Offer");

    private OfferAdapter adapter;


    public static final String EXTRA_ID = "com.example.foody.EXTRA_ID";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_offers, container, false);

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

        FirestorePagingOptions<Offer> options = new FirestorePagingOptions.Builder<Offer>()
                .setQuery(query, config, Offer.class)
                .build();

        adapter = new OfferAdapter(options,swipeRefreshLayout);
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

        adapter.setOnItemClickListener(new OfferAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot) {
                Offer offer = documentSnapshot.toObject(Offer.class);
                String id = documentSnapshot.getId();

                Intent intent = new Intent(getActivity(), DetailsActivityOffer.class);
                String name = offer.getName();
                intent.putExtra(EXTRA_ID, id);
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
