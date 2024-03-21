package com.example.qreate.administrator;

/**
 * This class defines an event (queried from the database) and is used to store its name and details.
 */
public class AdministratorEvent {
    private final String eventName;
    private final String eventOrganizer;

    /**
     * This is a constructor for the AdministratorEvent class
     * @param eventName the name of the event
     * @param eventOrganizer the name of the event organizer
     */
    public AdministratorEvent(String eventName, String eventOrganizer) {
        this.eventName = eventName;
        this.eventOrganizer = eventOrganizer;
    }

    /**
     * This method returns the event name
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * This method returns the name of the event organizer
     * @return the name of the event organizer
     */
    public String getEventOrganizer() {
        return eventOrganizer;
    }
}
