package com.devitizen.mooddiary.data;

/**
 * Parameters of API response
 */
public class WrSys {
    private int type;           // Internal parameter
    private int id;             // Internal parameter
    private double message;     // Internal parameter
    private String country;     // Country code (GB, JP etc.)
    private long sunrise;       // Sunrise time, unix, UTC
    private long sunset;        // Sunset time, unix, UTC
}
