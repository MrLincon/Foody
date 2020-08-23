package com.example.foody.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.foody.Authentication.LoginActivity;
import com.example.foody.Models.ThemeSettings;
import com.example.foody.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView close;

    private CardView logOut, darkMode;

    ThemeSettings themeSettings;
    private Switch darkModeSwitch;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Theme Settings
        themeSettings = new ThemeSettings(this);
        if (themeSettings.loadNightModeState() == false) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        //...............

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        close = findViewById(R.id.close);
        logOut = findViewById(R.id.log_out);
        darkModeSwitch = findViewById(R.id.switch_dark_mode);
        darkMode = findViewById(R.id.dark_mode);

//        For Action Bar
        toolbarTitle.setText("Settings");
//...............

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        darkModeSwitch.setClickable(false);
        if (themeSettings.loadNightModeState() == false) {
            darkModeSwitch.setChecked(false);
        } else {
            darkModeSwitch.setChecked(true);
        }

        darkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (themeSettings.loadNightModeState() == false) {
                    themeSettings.setNightModeState(true);
                    restartApp();   //Recreate activity
                } else {
                    themeSettings.setNightModeState(false);
                    restartApp();   //Recreate activity
                }
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent signOut = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(signOut);
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent close = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(close);
                finish();
            }
        });

    }


    private void restartApp() {
        Intent restartApp = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(restartApp);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}