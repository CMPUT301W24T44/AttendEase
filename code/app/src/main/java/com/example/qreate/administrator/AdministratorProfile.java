package com.example.qreate.administrator;

/**
 * This class defines a profile (queried from the database) and is used to store the profile name, image and other details(which will be implemented Part 4).
 */
public class AdministratorProfile {
    private String profileName;
    private String profileImage;
    private String id;

    /**
     *This is a constructor for the AdministratorProfile class
     * @param profileName the name of the profile owner
     * @param profileImage the profile picture of the profile
     */
    public AdministratorProfile(String profileName, String profileImage, String id) {
        this.profileName = profileName;
        this.profileImage = profileImage;
        this.id = id;
    }

    /**
     * This method returns the name of the profile owner
     * @return the name of the profile owner
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * This method returns the profile picture of the profile
     * @return the profile picture of the profile
     */
    public String getProfileImage() {
        return profileImage;
    }

    public String getId() {
        return id;
    }
}
