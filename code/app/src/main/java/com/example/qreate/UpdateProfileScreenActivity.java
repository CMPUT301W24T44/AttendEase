package com.example.qreate;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;

/**
 * This class allows the user to change/update his profile picture or generate a default profile picture
 * for the user to use
 * @author Akib Zaman Choudhury
 */
public class UpdateProfileScreenActivity extends AppCompatActivity {

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

        saveChangesButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to be filled in
            }
        }));

        ImageButton backButton = findViewById(R.id.update_profile_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}

