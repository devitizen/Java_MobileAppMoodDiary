package com.devitizen.mooddiary;

/**
 * Creates a note by getting necessary data from database
 */
public class Note {

    private int _id;
    private String weather, weatherDescription, address, locationX, locationY,
                   contents, mood, picture, createDateStr;

    /**
     * Constructor
     *
     * @param _id id
     * @param weather  Weather icon id
     * @param weatherDescription Weather condition
     * @param address address
     * @param locationX locationX
     * @param locationY locationY
     * @param contents contents
     * @param mood mood
     * @param picture picture path
     * @param createDateStr created date
     */
    public Note(int _id, String weather, String weatherDescription, String address, String locationX, String locationY,
                String contents, String mood, String picture, String createDateStr) {
        this._id = _id;
        this.weather = weather;
        this.weatherDescription = weatherDescription;
        this.address = address;
        this.locationX = locationX;
        this.locationY = locationY;
        this.contents = contents;
        this.mood = mood;
        this.picture = picture;
        this.createDateStr = createDateStr;
    }

    public int get_id() {
        return _id;
    }

    public String getWeather() {
        return weather;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getAddress() {
        return address;
    }

    public String getContents() {
        return contents;
    }

    public String getMood() {
        return mood;
    }

    public String getPicture() {
        return picture;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

}
