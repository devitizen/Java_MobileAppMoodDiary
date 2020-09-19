package com.devitizen.mooddiary.data;

/**
 * Parameters of API response
 */
public class WrItem {
    private int id;                 // Weather condition id
    private String main;            // Group of weather parameters (Rain, Snow, Extreme etc.)
    private String description;     // Weather condition within the group
    private String icon;            // Weather icon id

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return icon
     */
    public String getIcon() {
        return icon;
    }
}
