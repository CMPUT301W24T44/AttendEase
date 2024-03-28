package com.example.qreate;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * This class allows the user to change/update his profile picture or generate a default profile picture
 * for the user to use
 * @author Akib Zaman Choudhury
 */
public class UpdateProfileScreenActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    /**
     * Creates and inflates the update_profile_pic layout
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_pic);

        Button saveChangesButton = findViewById(R.id.save_changes_button);
        ImageButton backButton = findViewById(R.id.update_profile_screen_backbutton);
        Button galleryButton = findViewById(R.id.button_gallary);

        saveChangesButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        }));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        galleryButton.setOnClickListener(v -> openFileChooser());

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // You can display the selected image using an ImageView
            // ImageView imageView = findViewById(R.id.your_image_view);
            // imageView.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("uploads").child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                // Handle success
                Toast.makeText(UpdateProfileScreenActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> {
                // Handle failure
                Toast.makeText(UpdateProfileScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}

