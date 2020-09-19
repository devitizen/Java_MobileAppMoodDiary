package com.devitizen.mooddiary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Creates the initial screen that will be loaded when the app is executed.
 */
public class SplashActivity extends AppCompatActivity {


    /**
     * Creates handler to show the main activity with the delayed time
     *
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }

}
