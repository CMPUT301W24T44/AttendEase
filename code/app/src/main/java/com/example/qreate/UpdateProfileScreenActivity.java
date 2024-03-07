package com.example.qreate;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;

public class UpdateProfileScreenActivity extends AppCompatActivity {

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

