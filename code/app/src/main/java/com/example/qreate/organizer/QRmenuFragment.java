package com.example.qreate.organizer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

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

        ImageButton profileButton = view.findViewById(R.id.qr_menu_screen_profile_button);
        Button generateButton = view.findViewById(R.id.qr_menu_screen_button_generate_qr_code);
        Button reuseExistingQRButton = view.findViewById(R.id.qr_menu_screen_button_reuse_qr_code);
        Button eventListButton = view.findViewById(R.id.qr_menu_screen_button_event_list);
        Button shareQRButton = view.findViewById(R.id.qr_menu_screen_button_share_qr_code);

        registerForContextMenu(profileButton); //floating profile menu

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QRGeneratorActivity.class);
                startActivity(intent);
            }
        });
        reuseExistingQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QRReuseExistingActivity.class);
                startActivity(intent);
            }
        });
        eventListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QREventListActivity.class);
                startActivity(intent);
            }
        });
        shareQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QRShareActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    //Creates the profile pop up menu
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.profile_menu, menu);

        //Colors the text color white
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            SpannableString s = new SpannableString(menuItem.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            menuItem.setTitle(s);
        }
    }

}
