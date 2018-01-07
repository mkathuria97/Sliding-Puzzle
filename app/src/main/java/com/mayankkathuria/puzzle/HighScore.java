package com.mayankkathuria.puzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class HighScore extends AppCompatActivity {

    //stores the current size of the puzzle
    private int size;

    //stores the other size of the puzzle
    private int otherSize;

    //stores the lowest hour by the player in the game
    private int userLowestHours;

    //stores the lowest minutes by the player in the game
    private int userLowestMinutes;

    //stores the lowest seconds by the player in the game
    private int userLowestSeconds;

    private FirebaseDatabase database;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        //the hours taken by the player in the current game
        int hours = getIntent().getIntExtra("Hours", 24);

        //the minutes taken by the player in the current game
        int minutes = getIntent().getIntExtra("Minutes", 60);

        //the seconds taken by the player in the current game
        int seconds = getIntent().getIntExtra("Seconds", 60);

        settings = getSharedPreferences("GAME_DATA",
                Context.MODE_PRIVATE);

        //stores the puzzle size of the current game
        size = settings.getInt("puzzleSize", 3);

        TextView userHighScore;
        TextView otherSizeUserHighScore;

        if(size == 3){
            userHighScore = (TextView) findViewById(R.id.size3UserHighScore);
            otherSizeUserHighScore = (TextView) findViewById(R.id.size4UserHighScore);
            otherSize = 4;
        } else {
            userHighScore = (TextView) findViewById(R.id.size4UserHighScore);
            otherSizeUserHighScore = (TextView) findViewById(R.id.size3UserHighScore);
            otherSize = 3;
        }

        //the lowest hours taken by the user to solve the game of the current size
        userLowestHours = settings.getInt(size + "UserLowestHours", 24);

        //the lowest minutes taken by the user to solve the game of the current size
        userLowestMinutes = settings.getInt(size + "UserLowestMinutes", 60);

        //the lowest seconds taken by the user to solve the game of the current size
        userLowestSeconds = settings.getInt(size + "UserLowestSeconds", 60);

        //the lowest hours taken by the user to solve the game of the other size
        int otherSizeUserLowestHours = settings.getInt(otherSize + "UserLowestHours", 24);

        //the lowest minutes taken by the user to solve the game of the other size
        int otherSizeUserLowestMinutes = settings.getInt(otherSize + "UserLowestMinutes", 60);

        //the lowest seconds taken by the user to solve the game of the other size
        int otherSizeUserLowestSeconds = settings.getInt(otherSize + "UserLowestSeconds", 60);

        boolean userHighestScoreChanged = updateScore(hours, minutes, seconds,
                userLowestHours, userLowestMinutes, userLowestSeconds);

        //updates the score if the time taken by the user in the current game is lower than the
        //highest score
        if (userHighestScoreChanged) {
            SharedPreferences.Editor editor = settings.edit();

            userLowestHours = hours;
            userLowestMinutes = minutes;
            userLowestSeconds = seconds;

            editor.putInt(size + "UserLowestHours", hours);
            editor.putInt(size + "UserLowestMinutes", minutes);
            editor.putInt(size + "UserLowestSeconds", seconds);
            editor.apply();
        }

        userHighScore.setText(userLowestHours + " hours " + userLowestMinutes + " minutes " +
                    userLowestSeconds + " seconds");

        otherSizeUserHighScore.setText(otherSizeUserLowestHours + " hours " +
                                       otherSizeUserLowestMinutes + " minutes " +
                                       otherSizeUserLowestSeconds + " seconds");

        DatabaseReference ref = database.getInstance().getReference("puzzle");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get map of users in data snapshot
                collectTimeTaken((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });
    }

    /**
     *
     * @param puzzle map containing the value of all database references
     */
    public void collectTimeTaken(Map<String, Object> puzzle) {

        ArrayList<Integer> timeTaken = new ArrayList<>();

        //iterate through the sizes of the puzzle
        for (Map.Entry<String, Object> entry : puzzle.entrySet()) {

            //get time map
            Map time = (Map) entry.getValue();

            //get the fields and append to list
            for (Object key : time.keySet()) {
                int value = Integer.parseInt(time.get(key).toString());
                timeTaken.add(value);
            }
        }

        //stores whether the highest score has changed or not
        boolean highestScoreChanged;

        TextView highestScore;
        TextView otherSizeHighestScore;

        if (size == 3) {
            highestScoreChanged = updateScore(userLowestHours, userLowestMinutes, userLowestSeconds, timeTaken.get(5), timeTaken.get(4), timeTaken.get(3));
            highestScore = (TextView) findViewById(R.id.size3HighestScore);
            otherSizeHighestScore = (TextView) findViewById(R.id.size4HighestScore);
        } else {
            highestScoreChanged = updateScore(userLowestHours, userLowestMinutes,
                    userLowestSeconds, timeTaken.get(2), timeTaken.get(1), timeTaken.get(0));
            highestScore = (TextView) findViewById(R.id.size4HighestScore);
            otherSizeHighestScore = (TextView) findViewById(R.id.size3HighestScore);
        }

        //stores the index of the array containing the time
        int index;
        if(otherSize == 3){
            index = 2;
            otherSizeHighestScore.setText(timeTaken.get(5) + " hours " + timeTaken.get(4) +
                    " minutes " + timeTaken.get(3) + " seconds");
        } else {
            index = 5;
            otherSizeHighestScore.setText(timeTaken.get(2) + " hours " + timeTaken.get(1) +
                    " minutes " + timeTaken.get(0) + " seconds");
        }

        //updates the highest score if the highest score of the user is lower than that of
        //the highest score by any user of the game
        if (highestScoreChanged) {
            SharedPreferences.Editor editor = settings.edit();
            DatabaseReference lowestHoursRef = database.getReference().
                                               child("puzzle/size" + size + "/LowestHours");
            DatabaseReference lowestMinutesRef = database.getReference().
                                                 child("puzzle/size" + size +"/LowestMinutes");
            DatabaseReference lowestSecondsRef = database.getReference().
                                                 child("puzzle/size" + size +"/LowestSeconds");

            lowestHoursRef.setValue(userLowestHours);
            lowestMinutesRef.setValue(userLowestMinutes);
            lowestSecondsRef.setValue(userLowestSeconds);

            highestScore.setText(userLowestHours + " hours " + userLowestMinutes + " minutes "
                    + userLowestSeconds + " seconds");
        } else {
            highestScore.setText(timeTaken.get(index) + " hours " + timeTaken.get(index - 1)
                    + " minutes " + timeTaken.get(index - 2) + " seconds");
        }
    }

    /**
     *
     * @return true if there is any score update and false otherwise
     */
    public boolean updateScore(int hours, int minutes, int seconds, int lowestHours,
                               int lowestMinutes, int lowestSeconds) {


        boolean highestScoreChanged = false;

        if (hours < lowestHours) {
            highestScoreChanged = true;
        } else if ((hours == lowestHours) && (minutes < lowestMinutes)) {
            highestScoreChanged = true;
        } else if ((hours == lowestHours) && (minutes == lowestMinutes) &&
                (seconds < lowestSeconds)) {
            highestScoreChanged = true;
        }

        return highestScoreChanged;
    }
}
