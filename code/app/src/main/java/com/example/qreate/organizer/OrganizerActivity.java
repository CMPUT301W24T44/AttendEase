package com.example.qreate.organizer;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.qreate.EditProfileScreenFragment;
import com.example.qreate.HomeScreenFragment;
import com.example.qreate.R;
import com.example.qreate.WelcomeScreenFragment;
import com.example.qreate.organizer.attendeesmenu.OrganizerAttendeeListMenuFragment;
import com.example.qreate.organizer.geolocationmenu.OrganizerGeolocationMenuFragment;
import com.example.qreate.organizer.notificationsmenu.OrganizerNotificationsMenuFragment;
import com.example.qreate.organizer.qrmenu.OrganizerQRmenuFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 * The following class is responsible for all activities related to the Organizer
 *
 * Outstanding Issue: OrganizerActivityTest does not PASS unless line 53: authenticateUser(this); is commented.
 * @author Akib Zaman Choudhury
 */
public class OrganizerActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BottomNavigationView bottomNavigationView;
    private String retrievedDocumentId;

    private String device_id;

    /**
     * Creates the view and sets activity to the organizer_handler layout
     * Authenticates user
     * set up the navigation bar
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_handler);

        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

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
                    selectedFragment = new OrganizerQRmenuFragment();
                } else if (itemId == R.id.notifications_menu) {
                    selectedFragment = new OrganizerNotificationsMenuFragment();
                } else if (itemId == R.id.attendee_list_menu) {
                    selectedFragment = new OrganizerAttendeeListMenuFragment();
                } else if (itemId == R.id.geolocation_menu) {
                    selectedFragment = new OrganizerGeolocationMenuFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.organizer_handler_frame, selectedFragment).commit();
                    return true;
                }
                return true;
            }
        });


    }


    /**
     * Sets bottom menu bar to be visible and open up the home screen
     */
    public void homeScreenOrganizer() {

        authenticateOrganizer(); // creates the Organizers collection if it doesn't exist
        bottomNavigationView.setVisibility(View.VISIBLE);
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.organizer_handler_frame,homeScreenFragment).commit();
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method sets up all necessary parameters for checkIfUserExists() method.
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

    /**
     * Sends a query to check if user's android id already exists within the database
     * If android id exists, then it send the user directly to the home page,
     * else the app sends the user to the welcome page
     *
     * @param collectionName
     * @param fieldName
     * @param uniqueId
     */
    private void checkIfUserExists(String collectionName, String fieldName, String uniqueId) {

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
///////////////////////////////////////////////////////////////////////////////////////////////////

///////////////// EVENT FOR WHEN USER LOGS IN FOR THE FIRST TIME //////////////////////////////////
    /**
     * Sets bottom menu bar to be invisible and open up the welcome screen
     */
    public void firstTimeLoginOrganizer() {
        // Inflates the welcome screen fragment if its the user's first time logging in

        bottomNavigationView.setVisibility(View.INVISIBLE);
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment("organizer");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.organizer_handler_frame,welcomeScreenFragment).commit();

    }

    /**
     * Interface method implemented for when the edit menu fragment is destroyed
     */
    public void onFragmentDestroyed() {
        //After filling in user info from edit profile screen, this function is called

        bottomNavigationView.setVisibility(View.VISIBLE);
        homeScreenOrganizer();

    }
///////////////////////////////////////////// END //////////////////////////////////////////////////


///////////////////////////// NAVIGATION BAR VISIBILITY ///////////////////////////////////////////////
    public void hideBottomNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.organizer_handler_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar disappear
    }

    public void showBottomNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.organizer_handler_navigation_bar);
        navBar.setVisibility(View.VISIBLE); // Make the bottom navigation bar reappear
    }
///////////////////////////////////////////// END ///////////////////////////////////////////////



////////////// FUNCTIONS FOR HANDLING ORGANIZER COLLECTION CREATION //////////////////////////////////

    public interface DocumentIdCallback {
        void onDocumentIdRetrieved(String documentId);
        void onError(Exception e);
    }

    public void createOrganizerCollection() {

        retrieveUserDocument(new DocumentIdCallback() {
            @Override
            public void onDocumentIdRetrieved(String documentId) {

                DocumentReference docRef = db.collection("Users").document(documentId);
                List<String> eventList = new ArrayList<>();

                // Add all necessary document fields here
                Map<String, Object> device = new HashMap<>();
                device.put("user_document_id", docRef);
                device.put("event_list", eventList);
                device.put("device_id", device_id);

                db.collection("Organizers").add(device);
                // Now safely inside the callback, knowing documentId is retrieved
            }

            @Override
            public void onError(Exception e) {
                // Handle the error, such as by logging or displaying a message
            }
        });
    }













    private void authenticateOrganizer() {
        //Function to help set up checkIfOrganizerExists

        retrieveUserDocument(new DocumentIdCallback() {
            @Override
            public void onDocumentIdRetrieved(String documentId) {

                DocumentReference docRef = db.collection("Users").document(documentId);
                String collectionName = "Organizers";
                String fieldName = "user_document_id";

                checkIfOrganizersExist(collectionName, fieldName, docRef);

            }

            @Override
            public void onError(Exception e) {
                // Handle the error, such as by logging or displaying a message
            }
        });

    }


    public void checkIfOrganizersExist(String collectionName, String fieldName, DocumentReference documentReference) {


        db.collection(collectionName)
                .whereEqualTo(fieldName, documentReference)
                .limit(1) // Optimizes the query by limiting to the first match
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            Log.d("UniqueIDCheck", "The unique ID exists in the collection.");
                        } else {
                            createOrganizerCollection(); // creates organizer collection
                            Log.d("UniqueIDCheck", "The unique ID does not exist in the collection.");
                        }
                    } else {
                        Log.e("UniqueIDCheck", "Failed to perform the query.", task.getException());
                    }
                });


    }









    public void retrieveUserDocument(DocumentIdCallback callback) {

        db.collection("Users")
                .whereEqualTo("device_id", device_id)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String documentId = document.getId();
                        callback.onDocumentIdRetrieved(documentId);
                    } else {
                        callback.onError(new Exception("Document not found or error in fetching document."));
                    }
                });
    }
//////////////////////////////////////////////// END //////////////////////////////////////////////////



}
