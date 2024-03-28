package com.example.qreate.administrator;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.AccountProfileScreenFragment;
import com.example.qreate.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * The following class allows the administrator to see the Dashboard and view all {@link AdministratorEvent}, {@link AdministratorProfile} and {@link AdministratorImage}. (Deletion functionality will be implemented for Part 4)
 */
public class AdministratorDashboardFragment extends Fragment implements EventArrayAdapter.OnEventSelectedListener,ProfileArrayAdapter.OnProfileSelectedListener,ImageArrayAdapter.OnImageSelectedListener{
    private ListView list;
    private FirebaseFirestore db;
    private EventArrayAdapter eventArrayAdapter;
    private ProfileArrayAdapter profileArrayAdapter;
    private ImageArrayAdapter imageArrayAdapter;

    /**
     * Creates the view and inflates the administrator_dashboard layout, changing the custom ArrayAdapter ({@link EventArrayAdapter}, {@link ProfileArrayAdapter}, {@link ImageArrayAdapter}) for the ListView according to what the user chooses.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_dashboard, container, false);

        list = view.findViewById(R.id.list);
        db = FirebaseFirestore.getInstance();

        ImageButton profileButton = view.findViewById(R.id.admin_dashboard_profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        fetchProfilePicInfoFromDataBase(view);

        return view;
    }

    private void fetchProfilePicInfoFromDataBase(View rootView){
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
                                ImageButton defaultProfileButton = rootView.findViewById(R.id.admin_dashboard_profile_button);
                                defaultProfileButton.setImageBitmap(profileBitmap);

                            }
                        }
                    }else {
                        Log.e("FetchInfoFromUser", "Error fetching info from firestore", task.getException());
                    }
                });


    }

    private Bitmap decodeBase64(String generatedProfilePicBase64) {
        byte[] bytes = android.util.Base64.decode(generatedProfilePicBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
    public void loadEvents() {
        CollectionReference eventsRef = db.collection("Events");
        ArrayList<AdministratorEvent> events = new ArrayList<>();
        eventArrayAdapter = new EventArrayAdapter(getContext(), events, this); // Use class field here
        list.setAdapter(eventArrayAdapter);

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

    public void loadProfiles() {
        CollectionReference profilesRef = db.collection("Users");
        ArrayList<AdministratorProfile> profiles = new ArrayList<>();
        profileArrayAdapter = new ProfileArrayAdapter(getContext(), profiles,this);
        list.setAdapter(profileArrayAdapter);

        profilesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<AdministratorProfile> profilesList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String profileName = document.getString("name");
                    String profileImage = document.getString("profile_picture");
                    String profileId = document.getId();
                    profilesList.add(new AdministratorProfile(profileName, profileImage, profileId));
                }
                // Update the adapter with the new list
                profileArrayAdapter.clear();
                profileArrayAdapter.addAll(profilesList);
                profileArrayAdapter.notifyDataSetChanged();


            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }


    public void loadImages() {
        CollectionReference profilesImagesRef = db.collection("Users");
        CollectionReference postersImagesRef = db.collection("Events");
        ArrayList<AdministratorImage> images = new ArrayList<>();
        imageArrayAdapter = new ImageArrayAdapter(getContext(), images,this);
        list.setAdapter(imageArrayAdapter);

        profilesImagesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<AdministratorImage> imagesList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String imageName = document.getString("name");
                    String image = document.getString("profile_picture");
                    String imageId = document.getId();
                    imagesList.add(new AdministratorImage(imageName, image, imageId, "Profiles"));
                }
                // Update the adapter with the new list
                imageArrayAdapter.addAll(imagesList);
                imageArrayAdapter.notifyDataSetChanged();
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });

        postersImagesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<AdministratorImage> imagesList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String imageName = document.getString("name");
                    String image = document.getString("poster");
                    String imageId = document.getId();
                    imagesList.add(new AdministratorImage(imageName, image, imageId, "Events"));
                }
                // Update the adapter with the new list
                imageArrayAdapter.addAll(imagesList);
                imageArrayAdapter.notifyDataSetChanged();
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }

    private void accountProfile() {
        //Handles fragment transaction related to the account profile

        ((AdministratorActivity)getActivity()).hideMainBottomNavigationBar();

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.administrator_handler_frame, new AccountProfileScreenFragment("administrator"));
        transaction.addToBackStack(null); // Add this transaction to the back stack
        transaction.commit();
    }

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


    @Override
    public void onEventSelected() {
        hideBottomNavigationBar(); // Implement this method
        showDetailsNavigationBar(); // Implement this method
    }


    @Override
    public void onProfileSelected() {
        hideBottomNavigationBar(); // Implement this method
        showDetailsNavigationBar(); // Implement this method
    }

    @Override
    public void onImageSelected() {
        hideBottomNavigationBar(); // Implement this method
        showDetailsNavigationBar(); // Implement this method
    }

    /*
    public void clearEventSelection() {
        if (eventArrayAdapter != null) {
            eventArrayAdapter.clearSelection();
        }
        else if (profileArrayAdapter != null) {
            profileArrayAdapter.clearSelection();
        }
        else if (imageArrayAdapter != null) {
            imageArrayAdapter.clearSelection();
        }
    }

     */

    private void hideBottomNavigationBar() {
        // Find the BottomNavigationView and set its visibility to GONE
        ((AdministratorActivity)getActivity()).hideMainBottomNavigationBar();
    }

    private void showDetailsNavigationBar() {
        // Show the administrator_view_details_handler Bottom Navigation Bar
        // This assumes you have a method in AdministratorActivity to show this specific bar
        ((AdministratorActivity)getActivity()).showDetailsNavigationBar();
    }

    public String getSelectedEventId() {
        return eventArrayAdapter.getSelectedEventId();
    }

    public String getSelectedImageId() {
        return imageArrayAdapter.getSelectedImageId();
    }

    public String getSelectedImageType() {
        return imageArrayAdapter.getSelectedImageType();
    }

    public String getSelectedProfileId() {
        return profileArrayAdapter.getSelectedProfileId();
    }

}