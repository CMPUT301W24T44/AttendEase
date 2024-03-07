package com.example.qreate.administrator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class AdministratorDashboardFragment extends Fragment {
    private Button imagesButton;
    private Button profilesButton;
    private Button eventsButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_dashboard, container, false);

        eventsButton = view.findViewById(R.id.events);
        profilesButton = view.findViewById(R.id.profiles);
        imagesButton = view.findViewById(R.id.images);


        // Set click listeners for each button
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_button));
                profilesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));
                imagesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));

            }
        });

        profilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilesButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_button));
                imagesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));
                eventsButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));

            }
        });

        imagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagesButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_button));
                eventsButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));
                profilesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsButton = view.findViewById(R.id.events);

        // Programmatically clicking the "Events" button
        eventsButton.performClick();
    }

}