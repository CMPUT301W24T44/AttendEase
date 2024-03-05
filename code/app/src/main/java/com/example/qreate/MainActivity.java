package com.example.qreate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qreate.organizer.OrganizerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change this to the appropriate layout after Ryan and Shradha have created the xml file
        setContentView(R.layout.activity_main);

        // The ids to the button will change as well after the layout has been created
        Button attendeeButton = findViewById(R.id.test_Attendee);
        Button organizerButton = findViewById(R.id.test_Organizer);
        Button administratorButton = findViewById(R.id.test_Administrator);



        //***************** IMPORTANT!!!!! ***********************************************************************
        // Each interface will have its own Activity class that will handle everything
        // This is so that we don't unnecessarily populate the main activity and create merge conflicts
        // A package directory has been created for each UI
        // Do Whatever you want to do with the other classes but DON'T TOUCH MainActivity for the time being
        // You may change the .setOnClickListener stuff in regards to the UI that you are implementing
        //*********************************************************************************************************

        attendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrganizerActivity.class);
                startActivity(intent);

            }
        });

        administratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}