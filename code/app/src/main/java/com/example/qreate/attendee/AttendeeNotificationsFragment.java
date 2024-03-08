package com.example.qreate.attendee;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AttendeeNotificationsFragment extends Fragment {

    private ListView notificationsListView;
    private ArrayList<Notif> notificationsArrayList;
    private NotifArrayAdapter notifArrayAdapter;
    private FirebaseFirestore db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.attendee_notifications_page, container, false);

        //initialize data
        notificationsArrayList = new ArrayList<>();
        //set up adapter
        notifArrayAdapter = new NotifArrayAdapter(getContext(), notificationsArrayList);
        //set up the ListView
        notificationsListView = view.findViewById(R.id.notif_list_view);
        notificationsListView.setAdapter(notifArrayAdapter);


        //Fetch Data from firestore
        db = FirebaseFirestore.getInstance();
        fetchNotificationsFromFireStore();


        //set up item click listener
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return view;
    }

    private void fetchNotificationsFromFireStore() {
        db.collection("notifications")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                            String notificationMessage = documentSnapshot.getString("message");
                            String notificationsDetails = documentSnapshot.getString("details");

                            Notif notification = new Notif(notificationMessage, notificationsDetails);
                            notificationsArrayList.add(notification);
                        }
                        notifArrayAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "error fetching notifications", e);
                    }
                });
    }

}