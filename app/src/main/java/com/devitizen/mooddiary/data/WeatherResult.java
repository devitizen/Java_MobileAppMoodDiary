package com.devitizen.mooddiary.data;

import java.util.ArrayList;

/**
 * This is an API(OpenWeather) response in the format of JSON.
 * Some parameters have their own sub-parameters.
 */
public class WeatherResult {

    private WrCoord coord;
    private ArrayList<WrItem> weather;
    private String base;
    private WrMain main;
    private int visibility;
    private WrWind wind;
    private WrClouds clouds;
    private long dt;
    private WrSys sys;
    private int timezone;
    private int id;
    private String name;
    private int cod;

    /**
     * @return weather parameter in the array format. It uses only the first index.
     */
    public ArrayList<WrItem> getWeather() {
        return weather;
    }
}
