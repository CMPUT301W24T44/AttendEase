package com.example.qreate.organizer;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.qreate.EditProfileScreenFragment;
import com.example.qreate.HomeScreenFragment;
import com.example.qreate.R;
import com.example.qreate.WelcomeScreenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class OrganizerActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener {

    private String device_id;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_handler);

        bottomNavigationView = findViewById(R.id.organizer_handler_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder); //This line is here so that there is no default item selected, it selects a menu item that is invisible

        // Authenticates if user exists and sends them to the appropriate page
        authenticateUser(this);

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

    public void firstTimeLoginOrganizer() {
        // Inflates the welcomescreen fragment if its the user's first time logging in

        sendUserIdToFirestore(this); //Sends user android id to database

        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.organizer_handler_frame,welcomeScreenFragment).commit();


    }

    public void homeScreenOrganizer() {

        bottomNavigationView.setVisibility(View.VISIBLE);
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.organizer_handler_frame,homeScreenFragment).commit();

    }

    public void onFragmentDestroyed() {
        //After filling in user info from edit profile screen, this function is called

        bottomNavigationView.setVisibility(View.VISIBLE);
        homeScreenOrganizer();

    }

    private void sendUserIdToFirestore(Context context) {

        // Get a Firestore instance
        db = FirebaseFirestore.getInstance();
        Log.d("FirestoreConnection", "Firestore has been initialized.");
        // Get the unique Android ID
        device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        // Prepare the data to send
        Map<String, Object> device = new HashMap<>();
        device.put("device_id", device_id);

        // Send the unique ID to Firestore
        db.collection("Users").add(device)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestoredemo", "DocumentSnapshot successfully written!");
                    // Show a Toast message
                    //Toast.makeText(context, "It worked", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestoredemo", "Error writing document", e);
                    // Optionally, you could also show a Toast on failure
                    //Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkIfUserExists(String collectionName, String fieldName, String uniqueId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .whereEqualTo(fieldName, uniqueId)
                .limit(1) // Optimizes the query by limiting to the first match
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            homeScreenOrganizer(); // Takes user directly to the home screen
                            Log.d("UniqueIDCheck", "The unique ID exists in the collection.");
                        } else {
                            firstTimeLoginOrganizer(); // Takes user to the profile page
                            Log.d("UniqueIDCheck", "The unique ID does not exist in the collection.");
                        }
                    } else {
                        Log.e("UniqueIDCheck", "Failed to perform the query.", task.getException());
                    }
                });
    }

    private void authenticateUser(Context context) {
        //Function to help set up checkIfUserExists

        bottomNavigationView.setVisibility(View.INVISIBLE);
        // Get the unique Android ID
        device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String collectionName = "Users";
        String fieldName = "device_id";

        //Checks to see if user exisits
        checkIfUserExists(collectionName, fieldName, device_id);

    }


}
