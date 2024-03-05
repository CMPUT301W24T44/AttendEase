package com.example.qreate.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;

public class QRmenuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.qr_menu_screen, container, false);

        Button generateButton = view.findViewById(R.id.qr_menu_screen_button_generate_qr_code);
        Button reuseExistingButton = view.findViewById(R.id.qr_menu_screen_button_reuse_qr_code);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QRGeneratorActivity.class);
                startActivity(intent);
            }
        });
        reuseExistingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QRReuseExistingActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
