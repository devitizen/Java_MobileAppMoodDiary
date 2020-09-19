package com.devitizen.mooddiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import lib.kingja.switchbutton.SwitchMultiButton;

/**
 * Creates the notes list by getting data from the SQLiteDatabase and setting them to card views.
 */
public class Fragment1 extends Fragment implements OnBackPressedListener {

    private static final String TAG = "Fragment1";
    private int recordCount = -1;
    private ArrayList<String> spinnerItems;

    private MainActivity mainActivity;
    private NoteAdapter noteAdapter;

    private Cursor outCursor;
    private Context context;

    /**
     * Creates views.
     * Adds listeners to the recycler adapter and the switch button.
     * Creates recycler view and spinner for the page drop-down menu
     * Sets the preference on the switch button
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState saved instance
     * @return view group
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

        mainActivity = (MainActivity) getActivity();

        // adds listener to noteAdapter
        noteAdapter = new NoteAdapter();
        noteAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position) {
                Note note = noteAdapter.getItem(position);
                mainActivity.showFragment2(note);       // show the New with the selected note
            }
        });

        // adds listener to switch button
        final SwitchMultiButton switchButton = viewGroup.findViewById(R.id.switchButton);
        switchButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                noteAdapter.switchLayout(position);
                noteAdapter.notifyDataSetChanged();
                mainActivity.savePref(position);        // save the preference on displaying the list
            }
        });

        // creates a view for recyclerView
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = viewGroup.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(noteAdapter);

        // sets the preference on displaying the list
        noteAdapter.switchLayout(mainActivity.restorePref());
        switchButton.setSelectedTab(mainActivity.restorePref());

        // gets data from the database, and creates a cursor
        getNotesData();

        // creates the page drop-down menu
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
                R.layout.spinner_item, spinnerItems);
        final Spinner pageSpinner = viewGroup.findViewById(R.id.pageSpinner);
        pageSpinner.setAdapter(arrayAdapter);
        pageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // makes notes from the cursor, and sets it to noteAdapter
                setNotesToList(position * AppConstants.NUM_NOTES_PER_PAGE, (position + 1) * AppConstants.NUM_NOTES_PER_PAGE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return viewGroup;
    }

    /**
     * Attaches to the activity
     *
     * @param context context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Detaches from the activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (context != null) {
            context = null;
        }
        outCursor.close();
    }

    /**
     * Resumes on the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        mainActivity.setOnBackPressedListener(this);
    }

    /**
     * Implements OnBackPressedListener
     */
    @Override
    public void onBackButtonPressed() {
        showBackButtonDialog();
    }

    /**
     * Gets data from the database, and creates a cursor
     */
    public void getNotesData() {
        String sql = "select _id, WEATHER, WEATHERDESCRIPTION, ADDRESS, LOCATION_X, LOCATION_Y, "
                + "CONTENTS, MOOD, PICTURE, CREATE_DATE, MODIFY_DATE from "
                + AppConstants.TABLE_NOTE
                + " order by CREATE_DATE desc";

        NoteDatabase database = NoteDatabase.getInstance(context);
        if (database != null) {
            outCursor = database.rawQuery(sql);
            recordCount = outCursor.getCount();

            int numPages = (recordCount - recordCount % AppConstants.NUM_NOTES_PER_PAGE) / AppConstants.NUM_NOTES_PER_PAGE + 1;
            spinnerItems = new ArrayList<>();
            for (int i = 0; i < numPages; i++) {
                spinnerItems.add(String.valueOf(i + 1));
            }
        }
    }

    /**
     * Makes notes from the cursor, and sets it to noteAdapter
     *
     * @param startIndex starting index in the list
     * @param endIndex   ending index in the list
     */
    public void setNotesToList(int startIndex, int endIndex) {
        if (endIndex > recordCount) {
            endIndex = recordCount;
        }
        ArrayList<Note> notes = new ArrayList<>();

        outCursor.moveToPosition(startIndex - 1);
        for (int i = startIndex; i < endIndex; i++) {
            // move to the next line
            outCursor.moveToNext();

            // get data from the cursor
            int _id = outCursor.getInt(0);
            String weather = outCursor.getString(1);
            String weatherDescription = outCursor.getString(2);
            String address = outCursor.getString(3);
            String locationX = outCursor.getString(4);
            String locationY = outCursor.getString(5);
            String contents = outCursor.getString(6);
            String mood = outCursor.getString(7);
            String picture = outCursor.getString(8);
            String dateStr = outCursor.getString(9);

            // print out the log
            println("#" + i + " -> " + _id + ", " + weather + ", "
                    + weatherDescription + ", " + address + ", " + locationX + ", "
                    + locationY + ", " + contents + ", " + mood + ", " + picture
                    + ", " + dateStr);

            // sets notes in the list by fetching the data from database
            notes.add(new Note(_id, weather, weatherDescription, address, locationX, locationY,
                    contents, mood, picture, dateStr));
        }

        noteAdapter.setItems(notes);
        noteAdapter.notifyDataSetChanged();
    }

    /**
     * Shows dialog to handle back button
     */
    public void showBackButtonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.alert_Message_01);
        builder.setPositiveButton(R.string.alert_Button_01, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainActivity.finish();
            }
        });
        builder.setNegativeButton(R.string.alert_Button_02, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    /**
     * Prints log
     *
     * @param msg input message
     */
    public void println(String msg) {
        Log.d(TAG, msg);
    }

}
