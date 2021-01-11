package com.example.foody.Models;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavResRecyclerDecoration extends RecyclerView.ItemDecoration {
    int leftPadding,rightPadding;

    public FavResRecyclerDecoration(int leftPadding, int rightPadding) {
        this.leftPadding = leftPadding;
        this.rightPadding = rightPadding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemCount = state.getItemCount();

        if (itemCount > 0 && parent.getChildAdapterPosition(view) == itemCount - 1) {
            outRect.right = rightPadding;
        }
        outRect.right = rightPadding;
        outRect.left = leftPadding;
    }
}
