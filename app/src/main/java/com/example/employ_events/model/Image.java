package com.example.employ_events.model;
/*
The purpose of this class is to define a image and set/get the image information.
 */
/**
 * Represents a facility with various attributes including name, image UrL
 * @author Aaron
 */
public class Image {
    String name, url;

    public Image() {}
    /**
     * Returns the name of the image.
     * @return the name of the image
     */
    public String getName() {
        return name;
    }
    /**
     * Returns the Url  of the image.
     * @return the Url  of the image
     */
    public String getUrl() {
        return url;
    }
    /**
     * Sets the name of the image.
     * @param name the new name for the image
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Returns the Url  of the image.
     * @param url the Url  of the image
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
