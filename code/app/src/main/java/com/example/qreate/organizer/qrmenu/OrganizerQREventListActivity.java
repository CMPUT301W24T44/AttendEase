package com.example.qreate.organizer.qrmenu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qreate.R;

import java.util.ArrayList;

/**
 * Show the User a list of Events that he has created
 */
public class OrganizerQREventListActivity extends AppCompatActivity  {
    private OrganizerQREventListPopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_list_screen);

        //Create Event Button
        Button createEventButton = findViewById(R.id.event_list_screen_confirmbutton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating instance of the CustomPopupWindow
                popupWindow = new OrganizerQREventListPopupWindow(OrganizerQREventListActivity.this);
                // Showing the popup window
                popupWindow.showPopupWindow();
            }
        });



        //Back Button
        ImageButton backButton = findViewById(R.id.event_list_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Handle the result from the poster selection when creating an event
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OrganizerQREventListPopupWindow.IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            popupWindow.setImageUri(imageUri);
            String imageName = popupWindow.getFileNameByUri(this, imageUri);
            popupWindow.setImageName(imageName);
        }
    }




}
