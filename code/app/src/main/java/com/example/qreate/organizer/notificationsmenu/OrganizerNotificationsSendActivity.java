package com.example.qreate.organizer.notificationsmenu;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventSpinnerArrayAdapter;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * The following class is responsible for allowing organizers to send notifications
 *
 * Outstanding Issue: Event spinner is set up but not pulling from firebase yet code for sending the notification has also yet to be done
 * @author Denis Soh
 */
public class OrganizerNotificationsSendActivity extends AppCompatActivity {
    ArrayList<OrganizerEvent> events;
    Spinner eventsSpinner;
    OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_send_notification_screen);
        events = new ArrayList<OrganizerEvent>();

        addEventsInit();

        eventSpinnerArrayAdapter = new OrganizerEventSpinnerArrayAdapter(this, events);

        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS
        eventsSpinner = findViewById(R.id.send_notifications_screen_spinner);

        eventsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * changes the string in the spinner
             *
             * @param parent the adapter view of the item
             * @param view the current view
             * @param position the current position in spinner
             * @param id the id
             *
             * @return
             */
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

        //Back Button
        ImageButton backButton = findViewById(R.id.send_notifications_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//      // Dummy code (modify it after dennis has completed events)
//    public void sendNotificationToAllUsers(String title, String message) {
//        store db = FirestoreClient.getFirestore();
//        ApiFuture<QuerySnapshot> query = db.collection("Attendees").get();
//        List<String> fcmTokens = new ArrayList<>();
//
//        // Retrieve all documents from the "Attendees" collection
//        QuerySnapshot querySnapshot = query.get();
//        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
//        for (QueryDocumentSnapshot document : documents) {
//            String fcmToken = document.getString("fcmToken");
//            if (fcmToken != null && !fcmToken.isEmpty()) {
//                fcmTokens.add(fcmToken);
//            }
//        }
//
//        // Prepare a message to be sent to all FCM tokens
//        MulticastMessage message = MulticastMessage.builder()
//                .putData("title", title)
//                .putData("message", message)
//                .addAllTokens(fcmTokens)
//                .build();
//
//        // Send the message
//        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
//        // You can log or handle the response if needed
//    }


    //Temporary to test swap this with the firebase data
    private void addEventsInit(){
        String []cities ={"Edmonton", "Vancouver", "Toronto", "Hamilton", "Denver", "Los Angeles"};
        String []provinces = {"AB", "BC", "ON", "ON", "CO", "CA"};
        for(int i=0;i<cities.length;i++){
            events.add((new OrganizerEvent(cities[i], provinces[i], "date", "idk why getting the device id doesnt work here")));
        }
    }
}
