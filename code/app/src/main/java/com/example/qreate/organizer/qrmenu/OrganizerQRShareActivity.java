package com.example.qreate.organizer.qrmenu;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventSpinnerArrayAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class OrganizerQRShareActivity extends AppCompatActivity {
    ArrayList<OrganizerEvent> events;
    private FirebaseFirestore db;
    Spinner eventsSpinner;
    OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_share_qr_code_screen);
        db = FirebaseFirestore.getInstance();

        events = new ArrayList<OrganizerEvent>();

        addEventsInit();

        eventSpinnerArrayAdapter = new OrganizerEventSpinnerArrayAdapter(this, events);

        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS
        eventsSpinner = findViewById(R.id.share_qr_code_spinner);

        eventsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addEventsInit();

        eventSpinnerArrayAdapter.setDropDownViewResource(R.layout.organizer_event_list_recycler_row_layout);

        eventsSpinner.setAdapter(eventSpinnerArrayAdapter);

        Button shareButton = findViewById(R.id.share_qr_code_sharebutton);

        shareButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                //GOTTA PUT THE IMAGE LOCATION HERE
                Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/qricon.png");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(sharingIntent, "Share Image"));
            }
        }));

        //Back Button
        ImageButton backButton = findViewById(R.id.share_qr_code_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Temporary to test swap this with the firebase data
    private void addEventsInit(){
        String []cities ={"Edmonton", "Vancouver", "Toronto", "Hamilton", "Denver", "Los Angeles"};
        String []provinces = {"AB", "BC", "ON", "ON", "CO", "CA"};
        for(int i=0;i<cities.length;i++){
            events.add((new OrganizerEvent(cities[i], provinces[i],"date", "doesnt work")));
        }
        // this code literally does the same thing but grabbing from firebase idk why it doesnt work TODO fix it
        /*CollectionReference eventsRef = db.collection("Events");

        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String eventName = document.getString("name");
                    String eventDetails = document.getString("description");
                    String eventDate = document.getString("date");
                    String eventOrganizer = document.getString("organizer");
                    events.add(new OrganizerEvent(eventName, eventDetails, eventDate, eventOrganizer));
                }
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });*/
    }
}


