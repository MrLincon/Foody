package com.example.foody.BottomNav;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.foody.Activities.ProfileActivity;
import com.example.foody.Activities.SettingsActivity;
import com.example.foody.R;


public class FragmentPost extends Fragment {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView settings,profile;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post, container, false);


        toolbar = view.findViewById(R.id.toolbar);
        toolbarTitle = view.findViewById(R.id.toolbar_title);
        settings = view.findViewById(R.id.settings);
        profile = view.findViewById(R.id.profile);


        //        For Action Bar
        toolbarTitle.setText("Post");


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(getContext(), SettingsActivity.class);
                startActivity(settings);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(getContext(), ProfileActivity.class);
                startActivity(profile);
            }
        });

        return view;
    }
}
