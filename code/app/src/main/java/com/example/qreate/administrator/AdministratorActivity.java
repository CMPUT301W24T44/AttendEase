package com.example.qreate.administrator;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.example.qreate.organizer.HomeScreenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdministratorActivity extends AppCompatActivity {
    /*
    This class is used as the MainActivity class for the Administrator UI
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator_handler);

        BottomNavigationView bottomNavigationView = findViewById(R.id.administrator_handler_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder); //This line is here so that there is no default item selected, it selects a menu item that is invisible

        //inflates the homescreen fragment automatically
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.administrator_handler_frame,homeScreenFragment).commit();
    }
}
