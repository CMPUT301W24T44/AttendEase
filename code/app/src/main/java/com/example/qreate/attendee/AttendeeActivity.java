package com.example.qreate.attendee;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.example.qreate.HomeScreenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AttendeeActivity extends AppCompatActivity {
    /*
    This class is used as the MainActivity class for the Administrator UI
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_handler);

        BottomNavigationView bottomNavigationView = findViewById(R.id.attendee_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder);

        //inflates the homescreen fragment automatically
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.attendee_handler_frame,homeScreenFragment).commit();
    }

}
