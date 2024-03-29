package com.example.qreate.organizer.qrmenu;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.example.qreate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * The following class represents a event
 *
 * Outstanding Issue: Needs to also store a image (the poster)
 * @author Denis Soh
 */
public class OrganizerEvent {
    private String event;
    private String detail;
    private String organizer;
    private String date;
    private String documentID;
    private String posterUrl;
    private String signInQrCodeUrl;
    private String promotionalQrCodeUrl;
    private List<String> usersSignedUp;
    public String getDocumentID() {
        return documentID;
    }


    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }


    public String getEvent() {
        return event;
    }

    public String getOrganizer() {
        return organizer;
    }

    public String getDate() {
        return date;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getSignInQrCodeUrl() {
        return signInQrCodeUrl;
    }

    public String getPromotionalQrCodeUrl() {
        return promotionalQrCodeUrl;
    }

    public String getDetail() {
        return detail;
    }

    public List<String> getUsersSignedUp() {
        return usersSignedUp;
    }

    /**
     * event constructer
     *
     * @param event the event name
     * @param detail the event details
     *
     */

    public OrganizerEvent(String event, String detail, String date, String organizer, String documentID){
        this.event = event;
        this.detail = detail;
        //TODO ORGANIZER POSTER AND QR URLS
        this.date = date;
        this.organizer = organizer;
        this.documentID = documentID;
    }

    /**
     * returns name
     *
     * @return String
     */
    //THIS DOESNT WORK
}
