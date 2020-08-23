package com.example.foody.Tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.foody.Activities.DetailsActivity;
import com.example.foody.Models.Feed;
import com.example.foody.Models.FeedAdapter;
import com.example.foody.Models.FeedRecyclerDecoration;
import com.example.foody.R;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class FragmentFeed2 extends Fragment {

    View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    BottomNavigationView bottomNavigationView;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference feed = db.collection("Feed");

    //    private FeedAdapter adapter;
    private FirestorePagingAdapter<Feed, FeedViewHolder> adapter;
    private Context mContext;

    private String ID;


    public static final String EXTRA_ID = "com.example.foody.EXTRA_ID";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed, container, false);

        recyclerView = view.findViewById(R.id.feed_recyclerview);
        int topPadding = getResources().getDimensionPixelSize(R.dimen.topPadding);
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.bottomPadding);
        recyclerView.addItemDecoration(new FeedRecyclerDecoration(topPadding, bottomPadding));

        bottomNavigationView = (getActivity()).findViewById(R.id.bottom_navigation);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        getFeed();

        return view;
    }

    private void getFeed() {

        Query query = feed.orderBy("name", Query.Direction.ASCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build();

        FirestorePagingOptions<Feed> options = new FirestorePagingOptions.Builder<Feed>()
                .setLifecycleOwner(this)
                .setQuery(query, config, new SnapshotParser<Feed>() {
                    @NonNull
                    @Override
                    public Feed parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Feed feed = snapshot.toObject(Feed.class);
                        ID = snapshot.getId();
                        feed.setId(ID);
                        return feed;
                    }
                })
                .build();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refresh();
            }
        });


        adapter = new FirestorePagingAdapter<Feed, FeedViewHolder>(options) {
            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_layout,
                        parent, false);
               return new FeedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FeedViewHolder feedViewHolder, int i, @NonNull Feed feed) {
                feedViewHolder.Name.setText(feed.getName());
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                super.onLoadingStateChanged(state);
                switch (state) {
                    case LOADING_INITIAL:
                mSwipeRefreshLayout.setRefreshing(true);
                        Log.d("Paging Log", "Loading Initial data");
                        break;
                    case LOADING_MORE:
                mSwipeRefreshLayout.setRefreshing(true);
                        Log.d("Paging Log", "Loading next page");
                        break;
                    case FINISHED:
                mSwipeRefreshLayout.setRefreshing(false);
                        Log.d("Paging Log", "All data loaded");
                        break;
                    case LOADED:
                mSwipeRefreshLayout.setRefreshing(false);
                        Log.d("Paging Log", "Total data loaded "+getItemCount());
                        break;
                    case ERROR:
                mSwipeRefreshLayout.setRefreshing(false);
                        Log.d("Paging Log", "Error loading data");
                        break;
                }
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);

            mContext = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String id = getId(position);

                    

                    Intent intent = new Intent(getActivity(), DetailsActivity.class);

                    intent.putExtra(EXTRA_ID, ID);

                    startActivity(intent);

                    Log.d("Log", "Clicked");
                    Log.d("Log","Adapter Position "+String.valueOf(position));
                    Log.d("Log","ID "+ID);
                }
            });
        }
    }
}
