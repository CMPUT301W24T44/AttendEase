package com.example.qreate.organizer.notificationsmenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.AccountProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.organizer.OrganizerActivity;
import com.example.qreate.organizer.notificationsmenu.OrganizerNotificationsSendActivity;
import com.example.qreate.organizer.qrmenu.OrganizerQRmenuFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * The following class allows the User(Organizer) to send notifications to all Attendees
 * @author Akib Zaman Choudhurhy
 */
public class OrganizerNotificationsMenuFragment extends Fragment {

    /**
     * Creates the view and inflates the organizer_notifications_menu_screen layout
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.organizer_notifications_menu_screen, container, false);

        ImageButton profileButton = view.findViewById(R.id.notifications_menu_screen_profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });



        Button button = view.findViewById(R.id.notifications_menu_screen_send_notifications);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrganizerNotificationsSendActivity.class);
                startActivity(intent);
            }
        });

        fetchProfilePicInfoFromDataBase();

        return view;
    }


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
                                ImageButton defaultProfileButton = getView().findViewById(R.id.notifications_menu_screen_profile_button);
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

    private void accountProfile() {
        //Handles fragment transaction related to the account profile

        ((OrganizerActivity)getActivity()).hideBottomNavigationBar();

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.organizer_handler_frame, new AccountProfileScreenFragment("organizer"));
        transaction.addToBackStack(null); // Add this transaction to the back stack
        transaction.commit();
    }
}
