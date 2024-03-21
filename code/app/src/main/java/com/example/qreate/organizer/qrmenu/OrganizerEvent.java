package com.example.qreate.organizer.qrmenu;

import java.util.List;

/**
 * The following class represents a event
 *
 * Outstanding Issue: Needs to also store a image (the poster)
 * @author Denis Soh
 */
public class OrganizerEvent {
    private final String event;
    private final String detail;
    private String organizer;
    private final String date;
    private String posterUrl;
    private String signInQrCodeUrl;
    private String promotionalQrCodeUrl;
    private List<String> usersSignedUp;

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

    public OrganizerEvent(String event, String detail, String date){
        this.event = event;
        this.detail = detail;
        //TODO ORGANIZER POSTER AND QR URLS
        this.date = date;
    }

    /**
     * returns name
     *
     * @return String
     */
}
