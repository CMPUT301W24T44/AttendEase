package com.example.qreate.administrator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class AdministratorEventDetailsFragment extends Fragment {
    private TextView eventName;
    private TextView eventOrganizer;
    private TextView eventDescription;
    private TextView eventDate;
    private TextView eventTime;
    private TextView eventLocation;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_event_details_page, container, false);
        db = FirebaseFirestore.getInstance();
        eventName = view.findViewById(R.id.event_details_event_name);
        eventOrganizer = view.findViewById(R.id.event_details_event_organizer);
        eventDescription = view.findViewById(R.id.event_details_event_description);
        eventDate = view.findViewById(R.id.event_details_event_date);
        eventTime = view.findViewById(R.id.event_details_event_time);
        eventLocation = view.findViewById(R.id.event_details_event_location);

        // Retrieve the event ID passed from the previous fragment
        Bundle args = getArguments();
        String eventId = null;
        if (args != null) {
            eventId = args.getString("eventId");
        }

        // Query Firestore for the event details using the event ID
        if (eventId != null) {
            db.collection("Events").document(eventId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Extract event details from the document and update the UI
                                eventName.setText(document.getString("name"));
                                eventOrganizer.setText(document.getString("organizer"));
                                eventDescription.setText(document.getString("description"));
                                eventDate.setText(document.getString("date"));
                                eventTime.setText(document.getString("time"));
                                eventLocation.setText(document.getString("location"));
                                // Ensure you have fields named accordingly in your Firestore document
                            } else {
                                Log.d("Firestore", "Error getting documents: ", task.getException());
                            }
                        } else {
                            Log.d("Firestore", "Task Failure: ", task.getException());
                        }
                    });
        }

        return view;
    }

    public static AdministratorEventDetailsFragment newInstance(String eventId) {
        AdministratorEventDetailsFragment fragment = new AdministratorEventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }
}
