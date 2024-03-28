package com.example.qreate.administrator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdministratorImageDetailsFragment extends Fragment {
    private ImageView image;
    private TextView imageRef;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_image_details_page, container, false);
        db = FirebaseFirestore.getInstance();
        image = view.findViewById(R.id.image_details_picture);
        imageRef = view.findViewById(R.id.image_details_reference);

        // Retrieve the event ID passed from the previous fragment
        Bundle args = getArguments();
        String imageId = null;
        String imageType = null;
        if (args != null) {
            imageId = args.getString("imageId");
            imageType = getArguments().getString("imageType");
        }

        // Query Firestore for the event details using the event ID
        if (imageId != null && imageType != null) {
            db.collection(imageType).document(imageId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //image.setText(document.getString("profile_picture"));
                                imageRef.setText(document.getString("name"));
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

    public static AdministratorImageDetailsFragment newInstance(String imageId, String imageType) {
        AdministratorImageDetailsFragment fragment = new AdministratorImageDetailsFragment();
        Bundle args = new Bundle();
        args.putString("imageId", imageId);
        args.putString("imageType", imageType);
        fragment.setArguments(args);
        return fragment;
    }
}
