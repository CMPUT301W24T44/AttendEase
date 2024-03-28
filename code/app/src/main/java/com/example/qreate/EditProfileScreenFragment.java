package com.example.qreate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.qreate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
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
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri selectedImageUri;
    private OnFragmentInteractionListener mListener;
    private ImageView profileImageView;


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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    private void fetchUserProfile() {
        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .whereEqualTo("device_id", device_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String imageUrl = document.getString("profile_picture");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Use Picasso to load the image
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(profileImageView);
                        } else {
                            // Handle case where imageUrl is null or empty
                            Log.d("EditProfile", "No profile image URL found.");
                        }
                    } else {
                        Log.e("EditProfile", "Error fetching user profile.", task.getException());
                    }
                });
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
        //This one goes to the next screen, wherease the class AccountProfileScreenFragment destroys itself and returns to previous fragment
        // so two classes, same fragment layout but different behaviour
        // on pressing confirm, validates user details and returns

        profileImageView = view.findViewById(R.id.empty_profile_pic);
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
                mGetContent.launch(intent);
            }
        });
        fetchUserProfile();

        return view;
    }


    private final ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();

                        // Set the ImageView with the new image URI using Picasso
                        Picasso.get()
                                .load(selectedImageUri)
                                .into(profileImageView);

                        // Continue with updating the Firestore
                        updateProfilePictureInFirestore(selectedImageUri.toString());
                    }
                }
            });


    @Override
    public void onResume() {
        super.onResume();
        fetchUserProfile();
    }

    // Method to update Firestore with the new image URI
    private void updateProfilePictureInFirestore(String imageUrl) {
        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(device_id)
                .update("profile_picture", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EditProfile", "Profile image updated successfully.");
                    Toast.makeText(getContext(), "Profile image updated.", Toast.LENGTH_SHORT).show();

                    // Refresh the profile ImageView after successful update
                    fetchUserProfile();
                })
                .addOnFailureListener(e -> {
                    Log.w("EditProfile", "Error updating profile image.", e);
                });
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


        //generating initials
        String initials = getInitials(name);
        Bitmap profilePicture = generateProfilePicture(initials);

        //fix
        //set pfp to be new ImageView
        ImageView emptyPfP = view.findViewById(R.id.empty_profile_pic);
        emptyPfP.setImageBitmap(profilePicture);



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

        //check if initials are there
        if (TextUtils.isEmpty(initials)) {
            nonEmptyInput = false;
        }

        // Button status
        boolean status = switchButton.isChecked();

        
        if (!nonEmptyInput) {
            Toast.makeText(getActivity(), "Please Enter Your Details", Toast.LENGTH_SHORT).show();

        } else if (!validEmail) {
            Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_SHORT).show();

        } else {
            sendUserInfoToFirestore(name, phone, email, homepage, status, profilePicture, initials);
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
     * @param profilePicture
     * @param initials
     */
    private void sendUserInfoToFirestore(String name, String phone, String email, String homepage, boolean status, Bitmap profilePicture, String initials) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Map<String, Object> user = new HashMap<>();
        user.put("device_id", device_id);
        user.put("name", name);
        user.put("phone_number", phone);
        user.put("email", email);
        user.put("homepage", homepage);
        user.put("allow_coordinates", status);
        user.put("initials", initials);

        // Decide whether to use the URI or the encoded Bitmap
        if (selectedImageUri != null) {
            // If you have a URI, presumably you've uploaded the image to Firebase Storage and should use the URI
            user.put("profile_picture", selectedImageUri.toString());
        } else if (profilePicture != null) {
            // Otherwise, if you have a Bitmap, encode it
            user.put("profile_picture", encodeBitmap(profilePicture));
        } else {
            // Handle the case where there's no picture
            Log.d("Firestore", "No profile picture to upload");
        }

        // Assuming you're updating an existing user document
        db.collection("Users").document(device_id)
                .update(user)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User info updated successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating user info", e));
    }


    //bitmap to Base64
    private String encodeBitmap(Bitmap profilePictureBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profilePictureBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] byteArray = baos.toByteArray();
        String stringBase64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
        return stringBase64;

    }

    /**
     * Generate initials from user name
     * @param name The user's name
     * @return initials of user
     */
    private String getInitials(String name){
        String [] words = name.split("\\s+");
        StringBuilder initials = new StringBuilder();
        for(int i = 0; i< words.length; i++){
            String word = words[i];
            if(!TextUtils.isEmpty(word) && Character.isLetter(word.charAt(0))){
                initials.append(word.charAt(0));
                if(i<words.length -1){
                    initials.append(".");
                }
            }
        }
        return initials.toString().toUpperCase();
    }


    // generate a bitmap with initials drawn in
    private Bitmap generateProfilePicture(String initials){
        int width = 160;
        int height = 160;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        //background circle
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#FCA311"));
        canvas.drawCircle(width/2f, height/2f, width / 2f, backgroundPaint);

        //Draw initials in the text
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(70);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // "/ 2f" will calculate center of bitmap
        Rect textBounds = new Rect();
        textPaint.getTextBounds(initials, 0, initials.length(), textBounds);
        float x = canvas.getWidth()/ 2f;
        float y = (canvas.getHeight()/ 2f) + (textBounds.height()/ 2f);
        canvas.drawText(initials, x, y, textPaint);

        return bitmap;
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
