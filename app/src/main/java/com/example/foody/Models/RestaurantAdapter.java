package com.example.foody.Models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.foody.R;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class RestaurantAdapter extends FirestorePagingAdapter<Restaurant, RestaurantAdapter.RestaurantHolder> {

    private OnItemClickListener listener;
    private Context mContext;
    private Context context;
    SwipeRefreshLayout mswipeRefreshLayout;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public RestaurantAdapter(@NonNull FirestorePagingOptions<Restaurant> options, SwipeRefreshLayout swipeRefreshLayout) {
        super(options);
        this.mswipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull final RestaurantHolder holder, int position, @NonNull final Restaurant model) {
        final String post_id = getItem(position).getId();
        final String user_id = firebaseAuth.getCurrentUser().getUid();

        holder.Name.setText(model.getName());
        holder.Location.setText(model.getLocation());
        holder.Day.setText(model.getDay());
        holder.Time.setText(model.getTime());
        Glide.with(mContext).load(model.getUserImageUrl()).into(holder.UserImage);


        //Favourite Features
        holder.Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("user_id", user_id);
                firebaseFirestore.collection("UserDetails").document(user_id).collection("Favourites").document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> favourites = new HashMap<>();
                            favourites.put("name", model.getName());
                            favourites.put("location", model.getLocation());
                            favourites.put("day", model.getDay());
                            favourites.put("time", model.getTime());
                            favourites.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("UserDetails").document(user_id).collection("Favourites").document(post_id).set(favourites);

                        } else {

                            firebaseFirestore.collection("UserDetails").document(user_id).collection("Favourites").document(post_id).delete();

                        }
                    }
                });

            }
        });

       // Update favourite icon
        firebaseFirestore.collection("UserDetails").document(user_id).collection("Favourites").document(post_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.Favourite.setImageResource(R.drawable.ic_heart_selected);

                } else {

                    holder.Favourite.setImageResource(R.drawable.ic_heart);

                }

            }
        });
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state) {

            case LOADING_INITIAL:
                mswipeRefreshLayout.setRefreshing(true);
                Log.d("Paging Log", "Loading Initial data");
                break;
            case LOADING_MORE:
                mswipeRefreshLayout.setRefreshing(true);
                Log.d("Paging Log", "Loading next page");
                break;
            case FINISHED:
                mswipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log", "All data loaded");
                break;
            case LOADED:
                mswipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log", "Total data loaded "+getItemCount());
                break;
            case ERROR:
                mswipeRefreshLayout.setRefreshing(false);
                Log.d("Paging Log", "Error loading data");
                break;
        }
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_layout,
                parent, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new RestaurantHolder(view);
    }

    class RestaurantHolder extends RecyclerView.ViewHolder {
        TextView Name, Location, Day, Time;
        ImageView Favourite, UserImage;
        public RestaurantHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Location = itemView.findViewById(R.id.location);
            UserImage = itemView.findViewById(R.id.profile);
            Day = itemView.findViewById(R.id.day_opened);
            Time = itemView.findViewById(R.id.time_opened);
            Favourite = itemView.findViewById(R.id.favourite);

            mContext = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
