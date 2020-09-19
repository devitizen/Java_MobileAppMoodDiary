package com.devitizen.mooddiary;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;
import java.util.Locale;

/**
 * Creates the main menu which is related to the List (Fragment1) and the Stat (Fragment3).
 * Also sets the date in the main menu.
 */
public class FragmentMenu extends Fragment {

    private TextView textViewTodayDate, textViewTodayDate2;
    private Button buttonViewMenu1, buttonViewMenu2, buttonViewMenu3;

    private MainActivity mainActivity;
    private Context context;

    /**
     * Creates views
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState saved instance state
     * @return view group
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_menu, container, false);

        mainActivity = (MainActivity) getActivity();

        textViewTodayDate = viewGroup.findViewById(R.id.textViewTodayDate);
        textViewTodayDate2 = viewGroup.findViewById(R.id.textViewTodayDate2);

        // set the List menu button
        buttonViewMenu1 = viewGroup.findViewById(R.id.buttonViewMenu1);
        buttonViewMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onMenuSelected(1);
                buttonViewMenu1.setBackgroundResource(R.drawable.menu_button2);
                buttonViewMenu2.setBackgroundResource(R.drawable.menu_button);
                buttonViewMenu3.setBackgroundResource(R.drawable.menu_button);
            }
        });

        // set the New menu button
        buttonViewMenu2 = viewGroup.findViewById(R.id.buttonViewMenu2);
        buttonViewMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onMenuSelected(2);
                buttonViewMenu1.setBackgroundResource(R.drawable.menu_button);
                buttonViewMenu2.setBackgroundResource(R.drawable.menu_button2);
                buttonViewMenu3.setBackgroundResource(R.drawable.menu_button);
            }
        });

        // set the Stat menu button
        buttonViewMenu3 = viewGroup.findViewById(R.id.buttonViewMenu3);
        buttonViewMenu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onMenuSelected(3);
                buttonViewMenu1.setBackgroundResource(R.drawable.menu_button);
                buttonViewMenu2.setBackgroundResource(R.drawable.menu_button);
                buttonViewMenu3.setBackgroundResource(R.drawable.menu_button2);
            }
        });

        // set the current date
        setToday();

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
    }

    /**
     * Sets the current date
     */
    public void setToday() {
        Date date = new Date();
        String currentDate = AppConstants.dateFormat6.format(date);
        String currentYear = AppConstants.dateFormat7.format(date);

        if (Locale.getDefault().getLanguage().equals("ko")) {
            currentDate = AppConstants.dateFormat9.format(date);
            currentYear = AppConstants.dateFormat10.format(date);
        }

        textViewTodayDate.setText(currentDate);
        textViewTodayDate2.setText(currentYear);
    }

}
