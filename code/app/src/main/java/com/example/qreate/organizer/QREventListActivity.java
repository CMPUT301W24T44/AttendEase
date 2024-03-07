package com.example.qreate.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qreate.Event;
import com.example.qreate.EventListArrayAdapter;
import com.example.qreate.R;

import java.util.ArrayList;

public class QREventListActivity extends AppCompatActivity {
    ArrayList<Event> events;
    RecyclerView eventsView;
    EventListArrayAdapter eventListArrayAdapter;
    //Not finished recycler view is more annoying than i though gonna probably swap to list view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_list_screen);
        events = new ArrayList<Event>();

        addEventsInit();

        eventListArrayAdapter = new EventListArrayAdapter(this, events);

        eventsView = findViewById(R.id.event_list_screen_eventlist);
        eventsView.setAdapter(eventListArrayAdapter);

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
            events.add((new Event(cities[i], provinces[i])));
        }
    }
}
