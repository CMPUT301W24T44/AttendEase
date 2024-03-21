package com.example.qreate.attendee;

/**
 * Represents a notification object within the application, specifically for the attendee interface.
 * This class encapsulates the details of a notification, including the notification message
 * and the name of the organizer who issued the notification.
 *
 * this class is used to populate lists or arrays where notification data is displayed
 * to the user in the Qreate app itself.
 *
 * @author Shraddha Mehta
 */

public class Notif {
    private final String notification;
    private final String organizer;

    /**
     * Constructs a new Notif object with specified notification message and organizer name.
     *
     * @param notification The notification message content.
     * @param organizer The name of the organizer related to this notification.
     */



    Notif(String notification, String organizer){
        this.notification = notification;
        this.organizer = organizer;
    }

    /**
     * Returns the notification message of this Notif instance.
     *
     * @return The notification message content.
     */

    String getNotificationDescription(){
        return this.notification;
    }

    /**
     * Returns the name of the organizer related to this notification.
     *
     * @return The organizer's name.
     */

    String getOrganizerName(){
        return this.organizer;
    }
}