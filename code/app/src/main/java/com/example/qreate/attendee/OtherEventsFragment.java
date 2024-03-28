package com.example.qreate.attendee;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.AccountProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.administrator.AdministratorEvent;
import com.example.qreate.administrator.EventArrayAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OtherEventsFragment extends Fragment implements EventArrayAdapter.OnEventSelectedListener {
    private EventArrayAdapter eventArrayAdapter;
    private ListView eventList;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;

    /**
     * Creates and Inflates the other events view
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return other events view
     */
    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.other_events_listview,container,false);

        eventList = view.findViewById(R.id.other_event_list);
        db = FirebaseFirestore.getInstance();



        AppCompatButton backButton = view.findViewById(R.id.button_back_other_event_details);
        ImageButton profileButton = view.findViewById(R.id.other_events_profile_pic_icon);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goBackToAttendeeEventDetails();
            }
        });

        fetchProfilePicInfoFromDataBase();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAllEvents();
    }

    public void loadAllEvents(){
        CollectionReference eventsRef = db.collection("Events");
        ArrayList<AdministratorEvent> events = new ArrayList<>();
        eventArrayAdapter = new EventArrayAdapter(getContext(), events, this); // Use class field here
        eventList.setAdapter(eventArrayAdapter);

        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<AdministratorEvent> eventsList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String eventName = document.getString("name");
                    String eventOrganizer = document.getString("organizer");
                    String eventId = document.getId();
                    eventsList.add(new AdministratorEvent(eventName, eventOrganizer, eventId));
                }
                // Update the adapter with the new list
                eventArrayAdapter.clear();
                eventArrayAdapter.addAll(eventsList);
                eventArrayAdapter.notifyDataSetChanged();
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });


    }
    

    /**
     * Fetch info about user information specifically their profile pic stored on firebase
     */
    private void fetchProfilePicInfoFromDataBase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("Users")
                .whereEqualTo("device_id", device_id)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        if(querySnapshot != null && !querySnapshot.isEmpty()){
                            DocumentSnapshot documentSnap = querySnapshot.getDocuments().get(0);
                            String generatedProfilePicBase64 = documentSnap.getString("generated_pic");
                            if(generatedProfilePicBase64 != null){
                                //decode and then set
                                Bitmap profileBitmap = decodeBase64(generatedProfilePicBase64);

                                //set to image button
                                ImageButton defaultProfileButton = getView().findViewById(R.id.other_events_profile_pic_icon);
                                defaultProfileButton.setImageBitmap(profileBitmap);

                            }
                        }
                    }else {
                        Log.e("FetchInfoFromUser", "Error fetching info from firestore", task.getException());
                    }
                });


    }

    /**
     * Returns a bitmap image from a generated profile pic stored in Base64 on Firebase
     * @param generatedProfilePicBase64
     * @return bitmap of generated profile pic
     */
    private Bitmap decodeBase64(String generatedProfilePicBase64) {
        byte[] bytes = android.util.Base64.decode(generatedProfilePicBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }

    /**
     * Goes back to main event details page.
     */
    private void goBackToAttendeeEventDetails(){
        getParentFragmentManager().popBackStack();
    }

    /**
     * To make the drop down dashboard button functional
     * @param view
     */
    private void showPopupMenu(View view) {
        // Initialize the PopupMenu
        PopupMenu popupMenu = new PopupMenu(getActivity(), view); // For Fragment, use getActivity() instead of this
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        //Sets text color to white
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            MenuItem item = popupMenu.getMenu().getItem(i);
            SpannableString spanString = new SpannableString(popupMenu.getMenu().getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0); // Set color to white
            item.setTitle(spanString);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.profile_account) {
                    accountProfile();
                    return true;

                } else if (id == R.id.profile_logout) {
                    getActivity().finish();
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.show();
    }

    /**
     * Switching views when user needs to update their profile
     */
    private void accountProfile() {
        //Handles fragment transaction related to the account profile

        ((AttendeeActivity)getActivity()).hideBottomNavigationBar();

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.attendee_handler_frame, new AccountProfileScreenFragment("attendee"));
        transaction.addToBackStack(null); // Add this transaction to the back stack
        transaction.commit();
    }

    @Override
    public void onEventSelected() {
        hideBottomNavigationBar(); // Implement this method
        showDetailsNavigationBar(); // Implement this method
    }


    private void hideBottomNavigationBar() {
        // Find the BottomNavigationView and set its visibility to GONE
        ((AttendeeActivity)getActivity()).hideBottomNavigationBar();
    }

    private void showDetailsNavigationBar() {
        // Find the BottomNavigationView and set its visibility to GONE
        ((AttendeeActivity)getActivity()).showDetailsNavigationBar();
    }

    public String getSelectedEventId() {
        return eventArrayAdapter.getSelectedEventId();
    }



}