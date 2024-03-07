package com.example.qreate.organizer;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.qreate.Event;
import com.example.qreate.EventSpinnerArrayAdapter;
import com.example.qreate.R;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QRShareActivity extends AppCompatActivity {
    ArrayList<Event> events;
    Spinner eventsSpinner;
    EventSpinnerArrayAdapter eventSpinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_share_qr_code_screen);

        events = new ArrayList<Event>();

        addEventsInit();

        eventSpinnerArrayAdapter = new EventSpinnerArrayAdapter(this, events);

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
            events.add((new Event(cities[i], provinces[i])));
        }
    }
}


