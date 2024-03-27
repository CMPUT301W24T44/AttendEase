package com.example.qreate.attendee;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.qreate.R;

public class UpcomingEventsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upcoming_events_listview,container,false);

        AppCompatButton backButton = view.findViewById(R.id.button_back_upcoming_event_details);

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goBackToAttendeeEventDetails();
            }
        });

        return view;

    }

    private void goBackToAttendeeEventDetails(){
        getParentFragmentManager().popBackStack();
    }


}