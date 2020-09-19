package com.devitizen.mooddiary;

import android.graphics.Color;

import java.text.SimpleDateFormat;

/**
 * Defines some constant variables and date formats
 */
public class AppConstants {

    // request codes in getting weather info
    static final int REQ_LOCATION_BY_ADDRESS = 101;
    static final int REQ_WEATHER_BY_GRID = 102;

    // request code in selecting how to get photo in the New menu
    static final int REQ_PHOTO_CAPTURE = 103;
    static final int REQ_PHOTO_SELECTION = 104;

    // whether the existing photo is located in the New menu
    static final int CONTENT_PHOTO = 105;
    static final int CONTENT_PHOTO_EX = 106;

    // path of the saved picture
    static String FOLDER_PHOTO;

    // values for database in NoteDatabase
    static String DATABASE_NAME = "note.db";
    static String TABLE_NOTE = "NOTE";
    static int DATABASE_VERSION = 1;

    // whether new note is created or the existing note is modified
    static final int MODE_INSERT = 1;
    static final int MODE_MODIFY = 2;

    // number of notes per page in the List menu
    static final int NUM_NOTES_PER_PAGE = 20;

    // sets colors for the charts in the Stat menu
    static final int[] graphColor = {Color.rgb(108,180,184),
            Color.rgb(190,220,227), Color.rgb(243,243,246),
            Color.rgb(227,220,213), Color.rgb(248,131,121)
    };

    // date formats
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static SimpleDateFormat dateFormat3 = new SimpleDateFormat("MM/dd");
    static SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat dateFormat5 = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat dateFormat6 = new SimpleDateFormat("EEE, MMM d");
    static SimpleDateFormat dateFormat7 = new SimpleDateFormat("yyyy");
    static SimpleDateFormat dateFormat8 = new SimpleDateFormat("EEE, MMM d, a KK:mm");

    // date formats for Korean
    static SimpleDateFormat dateFormat9 = new SimpleDateFormat("M월 d일 EEE요일");
    static SimpleDateFormat dateFormat10 = new SimpleDateFormat("yyyy년");
    static SimpleDateFormat dateFormat11 = new SimpleDateFormat("M월 d일 EEE요일, a KK:mm");

}
