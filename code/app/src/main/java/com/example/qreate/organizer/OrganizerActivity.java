package com.example.qreate.organizer;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrganizerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_handler);

        BottomNavigationView bottomNavigationView = findViewById(R.id.organizer_handler_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder); //This line is here so that there is no default item selected, it selects a menu item that is invisible

        //inflates the homescreen fragment automatically
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.organizer_handler_frame,homeScreenFragment).commit();


//        //Testing purpose
//        EditProfileScreenFragment editProfileScreenFragment = new EditProfileScreenFragment();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.organizer_handler_frame,editProfileScreenFragment).commit();


        //Used if/else to check for selected id because switch was being a bitch
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.qr_menu) {
                    selectedFragment = new QRmenuFragment();
                } else if (itemId == R.id.notifications_menu) {
                    selectedFragment = new NotificationsMenuFragment();
                } else if (itemId == R.id.attendee_list_menu) {
                    selectedFragment = new AttendeeListMenuFragment();
                } else if (itemId == R.id.geolocation_menu) {
                    selectedFragment = new GeolocationMenuFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.organizer_handler_frame, selectedFragment).commit();
                    return true;
                }
                return true;
            }
        });


    }




}
