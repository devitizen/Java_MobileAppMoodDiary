package com.devitizen.mooddiary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * All data entered by users and fetched from the external source (weather API) are saved in this database.
 */
public class NoteDatabase {

    private static final String TAG = "NoteDatabase";

    /**
     * Singleton instance
     */
    private static NoteDatabase database;

    private SQLiteDatabase db;
    private Context context;

    /**
     * Constructor
     *
     * @param context context
     */
    private NoteDatabase(Context context) {
        this.context = context;
    }

    /**
     * Gets database by Singleton instance
     *
     * @param context context
     * @return database
     */
    public static NoteDatabase getInstance(Context context) {
        if(database == null) {
            database = new NoteDatabase(context);
        }
        return database;
    }

    /**
     * Opens database
     *
     * @return true
     */
    public boolean open() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        println("Opening database [" + AppConstants.DATABASE_NAME + "].");

        return  true;
    }

    /**
     * Closes the database
     */
    public void close() {
        db.close();
        database = null;
        println("Closing database [" + AppConstants.DATABASE_NAME + "].");
    }

    /**
     * Gets cursor from the response of query
     *
     * @param SQL sql query string
     * @return cursor
     */
    public Cursor rawQuery(String SQL) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SQL,null);
            println("cursor count : " + cursor.getCount());
        } catch (Exception e) {
            println("Exception in executeQuery");
        }

        return cursor;
    }

    /**
     * Executes sql query
     *
     * @param SQL sql query string
     * @return true
     */
    public boolean execSQL(String SQL) {
        try {
            db.execSQL(SQL);
        } catch (Exception e) {
            println("Exception in executeQuery");
        }

        return true;
    }

    /**
     * Prints log
     *
     * @param data input date
     */
    public void println(String data) {
        Log.d(TAG, data);
    }


    /**
     * ************************************************************
     * Innter Class : DatabaseHelper
     * ************************************************************
     */
    class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * Constructor
         *
         * @param context
         */
        public DatabaseHelper(Context context) {
            super(context, AppConstants.DATABASE_NAME, null, AppConstants.DATABASE_VERSION);
        }

        /**
         * Implements SQLitOpenHelper
         *
         * @param db SQLiteDatabase
         */
        @Override
        public void onCreate(SQLiteDatabase db) {

            String DROP_SQL = "drop table if exists " + AppConstants.TABLE_NOTE;
            db.execSQL(DROP_SQL);

            String CREATE_SQL = "create table " + AppConstants.TABLE_NOTE + "("
                    + " _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + " WEATHER TEXT DEFAULT '', "
                    + " WEATHERDESCRIPTION TEXT DEFAULT '', "
                    + " ADDRESS TEXT DEFAULT '', "
                    + " LOCATION_X TEXT DEFAULT '', "
                    + " LOCATION_Y TEXT DEFAULT '', "
                    + " CONTENTS TEXT DEFAULT '', "
                    + " MOOD TEXT, "
                    + " PICTURE TEXT DEFAULT '', "
                    + " CREATE_DATE DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')), "
                    + " MODIFY_DATE DATETIME DEFAULT (DATETIME('NOW','LOCALTIME')) "
                    + ")";
            db.execSQL(CREATE_SQL);

            String CREATE_INDEX_SQL = "create index " + AppConstants.TABLE_NOTE + "_IDX ON "
                    + AppConstants.TABLE_NOTE + "("
                    + "CREATE_DATE"
                    + ")";
            db.execSQL(CREATE_INDEX_SQL);

            println("creating database [" + AppConstants.DATABASE_NAME + "].");
            println("creating table [" + AppConstants.TABLE_NOTE + "].");
        }

        /**
         * Overrides SQLitOpenHelper
         *
         * @param db SQLiteDatabase
         */
        @Override
        public void onOpen(SQLiteDatabase db) {
            println("Opened database [" + AppConstants.DATABASE_NAME + "].");
        }

        /**
         * Implements SQLitOpenHelper
         *
         * @param db  SQLiteDatabase
         * @param oldVersion oldVersion
         * @param newVersion newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion +".");
        }

    }

}
