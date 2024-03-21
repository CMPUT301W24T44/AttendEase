package com.example.qreate.administrator;

/**
 * This class defines an image (queried from the database) and is used to store it and its name.
 */
public class AdministratorImage {
    private final String imageName;
    private final String image;

    /**
     * This is a constructor for the AdministratorImage class
     * @param imageName the reference name of the image i.e. profile name image or event name poster
     * @param image the image in string form
     */
    public AdministratorImage(String imageName, String image) {
        this.imageName = imageName;
        this.image = image;
    }

    /**
     * This method returns the reference name of the image
     * @return the reference name of the image
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * This method returns the string form of the image
     * @return the string form of the image
     */
    public String getImage() {
        return image;
    }
}
