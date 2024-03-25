package com.example.qreate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the Edit profile screen fragment that allows the user to update his User info.
 * After the use clicks the confirm button
 * the Class validates the inserted User info and updates the database.
 * Furthermore it also removes itself from the backstack of the fragment stack
 *
 * @author Akib Zaman Choudhury
 */
public class EditProfileScreenFragment extends Fragment {

    // Regex pattern for validating an email address
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    // Compile the regex into a Pattern object
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Interface for implementing onFragmentDestroyed().
     * which is methods for handling the UI after the EditProfileScreenFragment is removed from
     * the back stack
     */
    public interface OnFragmentInteractionListener {
        void onFragmentDestroyed();
    }

    /**
     * Interface for the fragment listener
     */
    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Creates view and inflates the edit_profile_info layout.
     * The method also validates the inserted User info and updates the database.
     * Furthermore it removes itself from the backstack of the fragment stack after the
     * user clicks the confirm button
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
        View view = inflater.inflate(R.layout.edit_profile_info, container, false);

        Button confirmDataButton = view.findViewById(R.id.edit_profile_confirm_button);
        confirmDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("confirm", "confirm pressed");
                authenticateUserInfo(view);
            }
        });

        ImageButton addPhotoButton = view.findViewById(R.id.add_photo_button);
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ProfilePic", "Add photo button pressed");
                Intent intent = new Intent(getActivity(), UpdateProfileScreenActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }



    private void authenticateUserInfo(View view) {

        boolean nonEmptyInput = true;
        boolean validName = true;
        boolean validEmail = true;

        EditText editTextName = view.findViewById(R.id.edit_name);
        EditText editTextPhone = view.findViewById(R.id.edit_number);
        EditText editTextEmail = view.findViewById(R.id.edit_email);
        EditText editTextHomepage = view.findViewById(R.id.edit_homepage_website);
        SwitchCompat switchButton = view.findViewById(R.id.edit_profile_switchcompat);

        // Retrieve user input from EditTexts
        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        String email = editTextEmail.getText().toString();
        String homepage = editTextHomepage.getText().toString();
        
        
        // Name condition check.
        if (TextUtils.isEmpty(name)) {
            nonEmptyInput = false;
        }

        // Phone condition check
        if (TextUtils.isEmpty(phone)) {
            nonEmptyInput = false;
        }

        // Email condition check.
        if (TextUtils.isEmpty(email)) {
            nonEmptyInput = false;
        }
        if (!isValidEmail(email)) {
            validEmail = false;
        }

        // Homepage condition check
        if (TextUtils.isEmpty(homepage)) {
            nonEmptyInput = false;
        }

        // Button status
        boolean status = switchButton.isChecked();

        
        if (!nonEmptyInput) {
            Toast.makeText(getActivity(), "Please Enter Your Details", Toast.LENGTH_SHORT).show();

        } else if (!validEmail) {
            Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_SHORT).show();

        } else {
            sendUserInfoToFirestore(name, phone, email, homepage, status);
            removeFragment(); //removes the fragment
        }

    }



    /**
     * The following method is used to update the database
     *
     * @param name
     * @param phone
     * @param email
     * @param homepage
     * @param status
     */
    private void sendUserInfoToFirestore(String name, String phone, String email, String homepage, boolean status) {

        // Get a Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("FirestoreConnection", "Firestore has been initialized.");
        // Get the unique Android ID
        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        // Prepare the data to send
        Map<String, Object> device = new HashMap<>();
        device.put("device_id", device_id);
        device.put("name", name);
        device.put("phone_number", phone);
        device.put("email", email);
        device.put("homepage", homepage);
        device.put("allow_coordinates", status);

        // Send the unique ID to Firestore
        db.collection("Users").add(device);

    }

    /**
     * Validates if the given string is a valid email address.
     *
     * @param email the string to be validated
     * @return true if the string is a valid email address; false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }


    public void removeFragment() {
        //removes fragment from the back stack, in this case its the current fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this); // 'this' refers to the current fragment
        fragmentTransaction.commit();
        fragmentManager.popBackStack(); // This line ensures the fragment is removed from the back stack
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.onFragmentDestroyed();
        }
    }

}
