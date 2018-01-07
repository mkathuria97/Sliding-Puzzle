package com.mayankkathuria.puzzle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newGame = (Button) findViewById(R.id.newGame);
        Button highScore = (Button) findViewById(R.id.highestScoreButton);
        Button settings = (Button) findViewById(R.id.settings);
        Button quit = (Button) findViewById(R.id.quit);

        //starts a new puzzle
        newGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Puzzle.class);
                startActivity(intent);
            }
        });

        //shows the highest score in the game and the highest score of the player
        highScore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HighScore.class);
                startActivity(intent);
            }
        });

        //allows to change the settings of the game
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        //allows to quit the game
        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }
}
