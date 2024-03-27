package com.example.qreate.administrator;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.EditProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.HomeScreenFragment;
import com.example.qreate.WelcomeScreenFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * The following class is responsible for all activities related to the Administrator
 *
 * Outstanding Issue: AdministratorActivityTest does not PASS unless line 48: authenticateUser(this); is commented.
 */
public class AdministratorActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener {
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private String selectedEventId;
    private String selectedProfileId;
    private String selectedImageId;


    /**
     * Creates the view and sets activity to the administrator_handler layout
     * Authenticates user
     * set up the navigation bar
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator_handler);

        db = FirebaseFirestore.getInstance();

        bottomNavigationView = findViewById(R.id.administrator_handler_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder);

        authenticateUser(this);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if ((itemId == R.id.events_icon) || (itemId == R.id.profiles_icon) || (itemId == R.id.images_icon)) {
                    selectedFragment = new AdministratorDashboardFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.administrator_handler_frame, selectedFragment).commitNow();

                    if (selectedFragment instanceof AdministratorDashboardFragment) {
                        if (itemId == R.id.events_icon) {
                            ((AdministratorDashboardFragment) selectedFragment).loadEvents();
                        } else if (itemId == R.id.profiles_icon) {
                            ((AdministratorDashboardFragment) selectedFragment).loadProfiles();
                        } else if (itemId == R.id.images_icon) {
                            ((AdministratorDashboardFragment) selectedFragment).loadImages();
                        }
                    }
                }
                return true;
            }
        });
    }


    public void hideMainBottomNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_handler_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar disappear
    }

    public void showMainBottomNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_handler_navigation_bar);
        navBar.setVisibility(View.VISIBLE); // Make the bottom navigation bar reappear
    }

    public void showDetailsNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_view_details_navigation_bar);
        navBar.setVisibility(View.VISIBLE); // Make the bottom navigation bar reappear
        navBar.setSelectedItemId(R.id.defaultNavPlaceholder);
        setupDetailsNavigationBar();
    }

    public void hideDetailsNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_view_details_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar reappear
    }

    public void showDeleteNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_delete_navigation_bar);
        navBar.setVisibility(View.VISIBLE); // Make the bottom navigation bar reappear
        navBar.setSelectedItemId(R.id.defaultNavPlaceholder);
        setupDeleteNavigationBar();
    }

    public void hideDeleteNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_delete_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar reappear
    }

    public void setupDeleteNavigationBar(){
        BottomNavigationView deleteNavBar = findViewById(R.id.administrator_delete_navigation_bar);
        BottomNavigationView navBar = findViewById(R.id.administrator_handler_navigation_bar);
        int navBarItemId = navBar.getSelectedItemId();

        deleteNavBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                AdministratorDashboardFragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.cancel_icon_two) {
                    hideDeleteNavigationBar();
                    showMainBottomNavigationBar();
                    getSupportFragmentManager().popBackStackImmediate();

                    if ((navBarItemId == R.id.events_icon) || (navBarItemId == R.id.profiles_icon) || (navBarItemId == R.id.images_icon)) {
                        selectedFragment = new AdministratorDashboardFragment();
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.administrator_handler_frame, selectedFragment).commitNow();

                        if (selectedFragment instanceof AdministratorDashboardFragment) {
                            if (navBarItemId == R.id.events_icon) {
                                selectedFragment.loadEvents();
                            } else if (navBarItemId == R.id.profiles_icon) {
                                selectedFragment.loadProfiles();
                            } else if (navBarItemId == R.id.images_icon) {
                                selectedFragment.loadImages();
                            }
                        }
                    }
                }
                else if (itemId == R.id.delete_icon) {
                    if (navBarItemId == R.id.events_icon && selectedEventId != null) {
                        deleteEvent(selectedEventId);
                    }
                    if ((navBarItemId == R.id.profiles_icon) && (selectedProfileId != null)) {
                        deleteProfile(selectedProfileId);
                    }
                    if ((navBarItemId==R.id.images_icon) && (selectedImageId != null)) {
                        deleteImage(selectedImageId);
                    }
                    hideDeleteNavigationBar();
                    showMainBottomNavigationBar();
                    getSupportFragmentManager().popBackStackImmediate();

                    if ((navBarItemId == R.id.events_icon) || (navBarItemId == R.id.profiles_icon) || (navBarItemId == R.id.images_icon)) {
                        selectedFragment = new AdministratorDashboardFragment();
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.administrator_handler_frame, selectedFragment).commitNow();

                        if (selectedFragment instanceof AdministratorDashboardFragment) {
                            if (navBarItemId == R.id.events_icon) {
                                selectedFragment.loadEvents();
                            } else if (navBarItemId == R.id.profiles_icon) {
                                selectedFragment.loadProfiles();
                            } else if (navBarItemId == R.id.images_icon) {
                                selectedFragment.loadImages();
                            }
                        }
                    }



                }
                return true;
            }
        });

    }

    private void deleteEvent(String eventId) {
        db.collection("Events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Delete Event", "Event successfully deleted!"))
                .addOnFailureListener(e -> Log.w("Delete Event", "Error deleting event", e));
    }

    private void deleteProfile(String profileId) {
        db.collection("Users").document(profileId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Delete Profile", "Profile successfully deleted!"))
                .addOnFailureListener(e -> Log.w("Delete Profile", "Error deleting profile", e));
    }

    private void deleteImage(String imageId) {
        DocumentReference eventDoc = db.collection("Events").document(imageId);
        eventDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // The task is successful, now check if the document exists
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        HashMap<String,Object> data = new HashMap<>();
                        data.put("poster", FieldValue.delete());
                        eventDoc.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Field successfully deleted
                                Log.d("Firestore", "Field successfully deleted");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error deleting the field
                                Log.w("Firestore", "Error deleting field", e);
                            }
                        });

                        Log.d("Events Document", "Events Document exists!");
                    } else {
                        Log.d("Events Document", "Events Document does not exist!");
                    }
                } else {
                    // The task failed with an exception
                    Log.d("Events Document", "Failed with: ", task.getException());
                }
            }
        });


    }


    public void setupDetailsNavigationBar() {
        BottomNavigationView detailsNavBar = findViewById(R.id.administrator_view_details_navigation_bar);
        BottomNavigationView navBar = findViewById(R.id.administrator_handler_navigation_bar);
        int navBarItemId = navBar.getSelectedItemId();

        detailsNavBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.cancel_icon) {
                    hideDetailsNavigationBar();
                    showMainBottomNavigationBar();

                } else if (itemId == R.id.view_details_icon) {
                    hideDetailsNavigationBar();
                    showDeleteNavigationBar();
                    if (navBarItemId==R.id.events_icon) {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.administrator_handler_frame);
                        if (currentFragment instanceof AdministratorDashboardFragment) {
                            selectedEventId = ((AdministratorDashboardFragment) currentFragment).getSelectedEventId();
                            if (selectedEventId != null) {
                                navigateToEventDetails(selectedEventId);
                            }
                        }
                    }

                    else if (navBarItemId==R.id.profiles_icon) {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.administrator_handler_frame);
                        if (currentFragment instanceof AdministratorDashboardFragment) {
                            selectedProfileId = ((AdministratorDashboardFragment) currentFragment).getSelectedProfileId();
                            if (selectedProfileId != null) {
                                navigateToProfileDetails(selectedProfileId);
                            }
                        }
                    }
                    else if (navBarItemId==R.id.images_icon) {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.administrator_handler_frame);
                        if (currentFragment instanceof AdministratorDashboardFragment) {
                            selectedImageId = ((AdministratorDashboardFragment) currentFragment).getSelectedImageId();
                            if (selectedImageId != null) {
                                navigateToImageDetails(selectedImageId);
                            }
                        }
                    }
                }
                return true;
            }
        });
    }

    private void navigateToEventDetails(String eventId) {
        AdministratorEventDetailsFragment detailsFragment = AdministratorEventDetailsFragment.newInstance(eventId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.administrator_handler_frame, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToImageDetails(String imageId) {
        AdministratorImageDetailsFragment detailsFragment = AdministratorImageDetailsFragment.newInstance(imageId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.administrator_handler_frame, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToProfileDetails(String profileId) {
        AdministratorProfileDetailsFragment detailsFragment = AdministratorProfileDetailsFragment.newInstance(profileId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.administrator_handler_frame, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    /**
     * Interface method implemented for when the edit menu fragment is destroyed
     */
    public void onFragmentDestroyed() {
        //After filling in user info from edit profile screen, this function is called.
        bottomNavigationView.setVisibility(View.VISIBLE);
        homeScreenAdministrator();
    }

    /**
     * Sets bottom menu bar to be invisible and opens up the welcome screen if its the user's first time logging in
     */
    public void firstTimeLoginAdministrator() {
        // Inflates the welcome screen fragment if its the user's first time logging in

        bottomNavigationView.setVisibility(View.INVISIBLE);
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment("administrator");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.administrator_handler_frame, welcomeScreenFragment).commit();
    }

    /**
     * Sets bottom menu bar to be visible and open up the home screen
     */
    public void homeScreenAdministrator() {

        //sendUserIdToFirestore(this); //Sends user android id to database

        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder);
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.administrator_handler_frame, homeScreenFragment).commit();

    }

    /**
     * Sends a query to check if user's android id already exists within the database
     * If android id exists, then it send the user directly to the home page,
     * else the app sends the user to the welcome page
     *
     * @param collectionName Collection name called 'Users' in the database
     * @param fieldName      Field name called 'device_id' in the Users collection
     * @param uniqueId       Unique android id of the user which is checked for in the database
     */
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
                            homeScreenAdministrator(); // Takes user directly to the home screen
                            Log.d("UniqueIDCheck", "The unique ID exists in the collection.");
                        } else {
                            firstTimeLoginAdministrator(); // Takes user to the profile page
                            Log.d("UniqueIDCheck", "The unique ID does not exist in the collection.");
                        }
                    } else {
                        Log.e("UniqueIDCheck", "Failed to perform the query.", task.getException());
                    }
                });
    }

    /**
     * This method sets up all necessary parameters for checkIfUserExists() method.
     *
     * @param context
     */
    private void authenticateUser(Context context) {
        //Function to help set up checkIfUserExists
        bottomNavigationView.setVisibility(View.INVISIBLE);
        // Get the unique Android ID
        String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String collectionName = "Users";
        String fieldName = "device_id";

        //Checks to see if user exists
        checkIfUserExists(collectionName, fieldName, device_id);

    }
}