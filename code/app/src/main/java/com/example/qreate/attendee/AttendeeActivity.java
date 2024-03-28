package com.example.qreate.attendee;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.EditProfileScreenFragment;
import com.example.qreate.HomeScreenFragment;
import com.example.qreate.R;
import com.example.qreate.WelcomeScreenFragment;
import com.example.qreate.administrator.AdministratorDashboardFragment;
import com.example.qreate.administrator.AdministratorEventDetailsFragment;
import com.example.qreate.attendee.AttendeeEventDetailsFragment;
import com.example.qreate.attendee.AttendeeNotificationsFragment;
import com.example.qreate.attendee.AttendeeScanFragment;
import com.example.qreate.organizer.OrganizerActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AttendeeActivity manages the main UI for attendee users within the application,
 * providing navigation through a bottom navigation bar to various fragments such as
 * notifications, event scanning, and event details.
 * It is responsible for initializing and managing the user interface related to attendee operations,
 * handling navigation between different sections of the app, and performing initial user authentication.
 * This class also implements the OnFragmentInteractionListener interface from the EditProfileScreenFragment
 * to handle interaction events in the user profile editing scenario.
 *
 * @author Akib Zaman Choudhury
 */

public class AttendeeActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener {
    /*
    This class is used as the MainActivity class for the Administrator UI
     */

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BottomNavigationView bottomNavigationView;
    private String retrievedDocumentId;
    private String tokenFCM;
    private String selectedEventId;

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int)
     * to programmatically interact with widgets in the UI, setting up the bottom navigation
     * to handle fragment switching, and authenticating the user.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                          Otherwise it should be null.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_handler);


        bottomNavigationView = findViewById(R.id.attendee_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholderAttendee);


        authenticateUser(this);


        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.notifications_icon) {
                    selectedFragment = new AttendeeNotificationsFragment();
                } else if (itemId == R.id.qr_icon) {
                    selectedFragment = new AttendeeScanFragment();
                } else if (itemId == R.id.events_icon_nav) {
                    selectedFragment = new AttendeeEventDetailsFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.attendee_handler_frame, selectedFragment).commit();
                    return true;
                }

                return false;
            }
        });
    }

    /**
     *  onFragmentDestroyed() is called when an interaction in the EditProfileScreenFragment
     *  is detected that requires the parent activity (AttendeeActivity) to perform a
     *  subsequent action, such as returning to the home screen.
     */
    public void onFragmentDestroyed() {
        //After filling in user info from edit profile screen, this function is called

        bottomNavigationView.setVisibility(View.VISIBLE);
        createAttendeeCollection();
        homeScreenAttendee();
    }

    /**
     * Initiates the first-time login process for a new attendee, inflating the WelcomeScreenFragment
     * and making the bottom navigation bar invisible to focus the user's attention on the welcome
     * content.
     */

    public void firstTimeLoginAttendee() {
        // Inflates the welcomescreen fragment if its the user's first time logging in

        bottomNavigationView.setVisibility(View.INVISIBLE);
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment("attendee");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.attendee_handler_frame,welcomeScreenFragment).commit();
    }

    /**
     * Transitions the user interface to display the HomeScreenFragment for an attendee,
     * making the bottom navigation bar visible and replacing the current fragment container
     * content with the home screen fragment.
     */

    public void homeScreenAttendee() {

        authenticateAttendee(); // checks if attendee collection exists, if not then creates one

        bottomNavigationView.setVisibility(View.VISIBLE);
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.attendee_handler_frame,homeScreenFragment).commit();

    }

    /**
     * Checks if the user's unique ID exists in the specified Firestore collection,
     * directing the user either to the home screen or to the first-time login process
     * based on whether the ID is found.
     *
     * @param collectionName The name of the Firestore collection to search.
     * @param fieldName The document field name expected to contain the user's unique ID.
     * @param uniqueId The unique ID of the device/user being authenticated.
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
                            homeScreenAttendee(); // Takes user directly to the home screen
                            Log.d("UniqueIDCheck", "The unique ID exists in the collection.");
                        } else {
                            firstTimeLoginAttendee(); // Takes user to the profile page
                            Log.d("UniqueIDCheck", "The unique ID does not exist in the collection.");
                        }
                    } else {
                        Log.e("UniqueIDCheck", "Failed to perform the query.", task.getException());
                    }
                });
    }

    /**
     * Prepares for user authentication by making the bottom navigation bar invisible and
     * calling the checkIfUserExists method with the user's device ID and the relevant Firestore
     * collection details. This is an initial step in the app's security and personalization
     * process.
     *
     * @param context The Context in which the authentication method is called, typically the current Activity.
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

    public void hideBottomNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.attendee_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar disappear
    }

    public void showBottomNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.attendee_navigation_bar);
        navBar.setVisibility(View.VISIBLE); // Make the bottom navigation bar reappear
    }

    public void showDetailsNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.attendee_view_details_navigation_bar);
        navBar.setVisibility(View.VISIBLE);
        navBar.setSelectedItemId(R.id.defaultNavPlaceholder);
        setupDetailsNavigationBar();
    }

    public void hideDetailsNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.attendee_view_details_navigation_bar);
        navBar.setVisibility(View.INVISIBLE);
    }

    public void setupDetailsNavigationBar() {
        BottomNavigationView detailsNavBar = findViewById(R.id.attendee_view_details_navigation_bar);

        detailsNavBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.cancel_icon) {
                    hideDetailsNavigationBar();
                    showBottomNavigationBar();
                } else if (itemId == R.id.events_icon) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.attendee_handler_frame);
                    if (currentFragment instanceof OtherEventsFragment) {
                        selectedEventId = ((OtherEventsFragment) currentFragment).getSelectedEventId();
                        if (selectedEventId != null) {
                            navigateToEventDetails(selectedEventId);
                        }
                    }
                }
                return true;
            }
        });
    }

    private void navigateToEventDetails(String eventId) {
        AttendeeEventViewDetailsFragment detailsFragment = AttendeeEventViewDetailsFragment.newInstance(eventId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.attendee_handler_frame, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }











////////////// FUNCTIONS FOR HANDLING ORGANIZER COLLECTION CREATION //////////////////////////////////

    public interface DocumentIdCallback {
        void onDocumentIdRetrieved(String documentId);
        void onError(Exception e);
    }

    public interface FCMTokenCallback {
        void onTokenReceived(String tokenFCM);
    }

    public void createAttendeeCollection() {

        retrieveUserDocument(new DocumentIdCallback() {
            @Override
            public void onDocumentIdRetrieved(String documentId) {
                createFCMToken(new FCMTokenCallback() {
                    @Override
                    public void onTokenReceived(String tokenFCM) {

                        DocumentReference docRef = db.collection("Users").document(documentId);
                        List<String> eventList = new ArrayList<>();

                        Map<String, Object> device = new HashMap<>();
                        device.put("user_document_id", docRef);
                        device.put("event_list", eventList);
                        device.put("fcm_token", tokenFCM);

                        db.collection("Attendees").add(device);
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }



    private void authenticateAttendee() {
        //Function to help set up checkIfOrganizerExists

        retrieveUserDocument(new DocumentIdCallback() {
            @Override
            public void onDocumentIdRetrieved(String documentId) {

                DocumentReference docRef = db.collection("Users").document(documentId);
                String collectionName = "Attendees";
                String fieldName = "user_document_id";

                checkIfAttendeesExist(collectionName, fieldName, docRef);

            }

            @Override
            public void onError(Exception e) {
                // Handle the error, such as by logging or displaying a message
            }
        });

    }


    public void checkIfAttendeesExist(String collectionName, String fieldName, DocumentReference documentReference) {

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
                            createAttendeeCollection(); // creates organizer collection
                            Log.d("UniqueIDCheck", "The unique ID does not exist in the collection.");
                        }
                    } else {
                        Log.e("UniqueIDCheck", "Failed to perform the query.", task.getException());
                    }
                });


    }


    public void retrieveUserDocument(DocumentIdCallback callback) {

        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

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

    public void createFCMToken(FCMTokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String tokenFCM = task.getResult();
                    callback.onTokenReceived(tokenFCM);
                });
    }

//////////////////////////////////////////////// END //////////////////////////////////////////////////



}
