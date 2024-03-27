package com.example.qreate.organizer.qrmenu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerCreateEventFragment;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventListArrayAdapter;

import java.util.ArrayList;

/**
 * Show the User a list of Events that he has created
 */
public class OrganizerQREventListActivity extends AppCompatActivity implements OrganizerCreateEventFragment.AddEventDialogListener {
    ArrayList<OrganizerEvent> events;
    RecyclerView eventsView;
    OrganizerEventListArrayAdapter eventListArrayAdapter;
    //Not finished recycler view is more annoying than i though gonna probably swap to list view

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_list_screen);
        events = new ArrayList<OrganizerEvent>();

        addEventsInit();

        eventListArrayAdapter = new OrganizerEventListArrayAdapter(this, events);

        eventsView = findViewById(R.id.event_list_screen_eventlist);
        eventsView.setAdapter(eventListArrayAdapter);

        //Create Event Button
        Button createEventButton = findViewById(R.id.event_list_screen_confirmbutton);

        createEventButton.setOnClickListener(v -> {
            new OrganizerCreateEventFragment().show(getSupportFragmentManager(), "Create Event");
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

    //Temporary to test swap this with the firebase data
    private void addEventsInit(){
        String []cities ={"Edmonton", "Vancouver", "Toronto", "Hamilton", "Denver", "Los Angeles"};
        String []provinces = {"AB", "BC", "ON", "ON", "CO", "CA"};
        for(int i=0;i<cities.length;i++){
            events.add((new OrganizerEvent(cities[i], provinces[i],"date", "get id doesnt work here either")));
        }
    }

    /**
     *
     * @param event
     */
    @Override
    public void addEvent(OrganizerEvent event) {
        //add event stuff into the database TODO
    }
}
