package com.devitizen.mooddiary;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Creates a menu only for the New. Users can select three options, save, delete, and close.
 */
public class Fragment2Menu extends Fragment {

    private MainActivity mainActivity;
    private Context context;

    /**
     * Creates views
     *
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState saved instance state
     * @return view group
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment2_menu, container, false);

        mainActivity = (MainActivity)getActivity();

        // save menu
        Button saveButton = viewGroup.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onTabSelected(0);
            }
        });

        // delete menu
        Button deleteButton = viewGroup.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onTabSelected(1);
            }
        });

        // close menu
        Button closeButton = viewGroup.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onTabSelected(2);
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
        if(context != null) {
            context = null;
        }
    }

}