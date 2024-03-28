package com.example.qreate;


import static com.google.common.io.Files.getFileExtension;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;


;import java.util.HashMap;
import java.util.Map;

/**
 * This class allows the user to change/update his profile picture or generate a default profile picture
 * for the user to use
 * @author Akib Zaman Choudhury
 */
public class UpdateProfileScreenActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference storageRef;
    private StorageTask uploadTask;
    private FirebaseFirestore db;

    /**
     * Creates and inflates the update_profile_pic layout
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_pic);

        storageRef = FirebaseStorage.getInstance().getReference("user_profiles");

        Button galleryButton = findViewById(R.id.button_gallary);
        Button saveChangesButton = findViewById(R.id.save_changes_button);
        ImageButton backButton = findViewById(R.id.update_profile_screen_backbutton);


        saveChangesButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        }));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


    }


    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String fileExtension = mime.getExtensionFromMimeType(cR.getType(imageUri));

            if (fileExtension != null) {
                StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + fileExtension);

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            updateProfilePictureInFirestore(downloadUrl);
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("imageUri", downloadUrl);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }))
                        .addOnFailureListener(e -> {
                            Toast.makeText(UpdateProfileScreenActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        });
            } else {
                Toast.makeText(UpdateProfileScreenActivity.this, "Could not determine file type", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void updateProfilePictureInFirestore(String imageUrl) {
        String device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .whereEqualTo("device_id", device_id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Assuming 'device_id' is unique and there's only one document per user
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        DocumentReference userDocRef = documentSnapshot.getReference();
                        userDocRef.update("profile_picture", imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("UpdateProfile", "Profile picture updated successfully.");
                                    Toast.makeText(UpdateProfileScreenActivity.this, "Profile picture updated successfully.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Log.e("UpdateProfile", "Error updating profile picture", e));
                    } else {
                        Log.e("UpdateProfile", "User document not found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("UpdateProfile", "Error fetching user document", e));
    }




    private String getFileExtension(String mimeType) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

}

