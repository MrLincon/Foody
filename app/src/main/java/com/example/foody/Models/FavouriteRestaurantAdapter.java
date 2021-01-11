package com.example.foody.Models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class FavouriteRestaurantAdapter extends FirestorePagingAdapter<FavouriteRestaurants, FavouriteRestaurantAdapter.FavouriteRestaurantHolder> {

    private OnItemClickListener listener;
    private Context mContext;

    public FavouriteRestaurantAdapter(@NonNull FirestorePagingOptions<FavouriteRestaurants> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final FavouriteRestaurantHolder holder, int position, @NonNull final FavouriteRestaurants model) {
        holder.Name.setText(model.getName());
        holder.Day.setText(model.getDay());
        holder.Time.setText(model.getTime());
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state) {

            case LOADING_INITIAL:
                Log.d("Paging Log", "Loading Initial data");
                break;
            case LOADING_MORE:
                Log.d("Paging Log", "Loading next page");
                break;
            case FINISHED:
                Log.d("Paging Log", "All data loaded");
                break;
            case LOADED:
                Log.d("Paging Log", "Total data loaded "+getItemCount());
                break;
            case ERROR:
                Log.d("Paging Log", "Error loading data");
                break;
        }
    }

    @NonNull
    @Override
    public FavouriteRestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_restaurant_layout,
                parent, false);

        return new FavouriteRestaurantHolder(view);
    }

    class FavouriteRestaurantHolder extends RecyclerView.ViewHolder {
        TextView Name, Day, Time;
        public FavouriteRestaurantHolder(View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Day = itemView.findViewById(R.id.day_opened);
            Time = itemView.findViewById(R.id.time_opened);

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
