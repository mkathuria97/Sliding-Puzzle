package com.mayankkathuria.puzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import com.mayankkathuria.puzzle.R;

public class Settings extends AppCompatActivity {

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);

        RadioButton button;

        int size = settings.getInt("puzzleSize", 3);

        if (size == 3) {
            button = (RadioButton) findViewById(R.id.size3);
        } else {
            button = (RadioButton) findViewById(R.id.size4);
        }

        //checks the radio button
        button.setChecked(true);

        String tileSlide = settings.getString("tileSlide", "motionSensors");

        if (tileSlide.equalsIgnoreCase("motionSensors")) {
            button = (RadioButton) findViewById(R.id.motionSensors);
        } else {
            button = (RadioButton) findViewById(R.id.click);
        }

        //checks the radio button
        button.setChecked(true);
    }

    /**
     *
     * @param view RadioGroup for size
     */
    public void onRadioButtonClicked(View view) {

        SharedPreferences.Editor editor = settings.edit();

        //checks whether the button is checked or not
        boolean checked = ((RadioButton) view).isChecked();

        //check which radio button was clicked
        switch(view.getId()) {
            case R.id.size3:
                if (checked) {
                    editor.putInt("puzzleSize", 3);
                    break;
                }

            case R.id.size4:
                if (checked) {
                    editor.putInt("puzzleSize", 4);
                    break;
                }
        }

        editor.apply();
    }

    /**
     *
     * @param view RadioGroup for tile motion
     */
    public void onSlideRadioButtonClicked(View view){

        SharedPreferences.Editor editor = settings.edit();

        ////checks whether the button is checked or not
        boolean checked = ((RadioButton) view).isChecked();

        //check which radio button was clicked
        switch(view.getId()) {
            case R.id.motionSensors:
                if (checked) {
                    editor.putString("tileSlide", "motionSensors");
                    break;
                }

            case R.id.click:
                if (checked) {
                    editor.putString("tileSlide", "click");
                    break;
                }
        }

        editor.apply();
    }
}


