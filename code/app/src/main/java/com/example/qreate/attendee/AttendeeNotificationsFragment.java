package com.example.qreate.attendee;

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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.AccountProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.organizer.OrganizerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A Fragment that displays notifications to the attendee. This class is responsible for
 * fetching notification data from Firestore and displaying it in a ListView. Each notification
 * consists of a message and details, encapsulated in a Notif object. The fragment uses a custom
 * ArrayAdapter (NotifArrayAdapter) to display the notifications in the ListView.
 *
 * @author Shraddha Mehta
 */

public class AttendeeNotificationsFragment extends Fragment {

    private ListView notificationsListView;
    private ArrayList<Notif> notificationsArrayList;
    private NotifArrayAdapter notifArrayAdapter;
    private FirebaseFirestore db;

    /**
     * This method inflates the layout for the notifications page and sets up the
     * ListView with a custom ArrayAdapter.
     * It also initiates fetching of notification data from Firestore.
     *
     * @param inflater LayoutInflater: The LayoutInflater object that can be used to inflate
     *                 any views in the fragment.
     * @param container ViewGroup: If non-null, this is the parent view that the fragment's
     *                 UI should be attached to.
     * @param savedInstanceState Bundle: If non-null, this fragment is being re-constructed
     *                 from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.attendee_notifications_page, container, false);

        ImageButton profileButton = view.findViewById(R.id.profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        //initialize data
        notificationsArrayList = new ArrayList<>();
        //set up adapter
        notifArrayAdapter = new NotifArrayAdapter(getContext(), notificationsArrayList);
        //set up the ListView
        notificationsListView = view.findViewById(R.id.notif_list_view);
        notificationsListView.setAdapter(notifArrayAdapter);

        //set up item click listener
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        fetchProfilePicInfoFromDataBase();
        //fetchNotificationsFromFireStore();

        return view;
    }

    /**
     * Fetches notification data from Firestore, parses the data into Notif objects, and adds
     * them to the notificationsArrayList. It then notifies the notifArrayAdapter of the data
     * change to refresh the ListView. If there is an error fetching data, it logs the error.
     */
//    private void fetchNotificationsFromFireStore() {
//        db.collection("notifications")
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    for(QueryDocumentSnapshot documentSnapshot : querySnapshot){
//                        String notificationMessage = documentSnapshot.getString("message");
//                        String organizerDetails = documentSnapshot.getString("details");
//
//                        Notif notification = new Notif(notificationMessage, organizerDetails);
//                        notificationsArrayList.add(notification);
//                    }
//                    notifArrayAdapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Firestore", "Error fetching notifications", e);
//                    Toast.makeText(getContext(), "failed to fetch notifications. Try again later.", Toast.LENGTH_SHORT).show();
//                });
//    }

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
                                ImageButton defaultProfileButton = getView().findViewById(R.id.profile);
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



}
