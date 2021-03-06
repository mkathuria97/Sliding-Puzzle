package com.mayankkathuria.puzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Puzzle extends AppCompatActivity implements SensorEventListener {

    //the number of the shuffles to arrange the tile in non-numerical order
    public static final int NUM_OF_SHUFFLES = 500;

    //handles the senstivity of gyroscope/sensor events
    public static final int SENSTIVITY = 4;

    //the time gap in milliseconds between two gyroscope/sensor events
    public static final int TIME_GAP = 300;

    //the height of the tile
    int TILE_HEIGHT = 200;

    //the width of the tile
    int TILE_WIDTH = 200;


    //used to set the X coordinate of the tiles in the puzzle
    int XPOSITION = 350;

    //used to set the Y coordinate of the tiles in the puzzle
    int YPOSITION = 350;

    //stores the total number of tiles in the puzzle
    private int totalNumOfTiles;

    //stores the number of tiles in a row or a column
    private int numOfTilesInRowCol;

    //stores the axis of rotation in the X direction
    private float rotationX;

    //stores the axis of rotation in the Y direction
    private float rotationY;

    //stores whether the puzzle is shuffled or not
    private boolean isShuffled = false;

    //stores the sliding options - motion sensors or click, to move the tiles in the game
    private String slide;

    //stores the initial and the updated X coordinate of the empty tile
    private int emptyTileXPos;

    //stores the initial and the updated Y coordinate of the empty tile
    private int emptyTileYPos;

    //seconds taken by the user to solve the game
    private int seconds;

    //minutes taken by ths user to solve the game
    private int minutes;

    //hours taken by the user to solve the game
    private int hours;

    //stores the current time
    private long timestamp = System.currentTimeMillis();

    private SensorManager sensorManager;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        numOfTilesInRowCol = settings.getInt("puzzleSize", 3);
        slide = settings.getString("tileSlide", "motionSensors");

        totalNumOfTiles = (numOfTilesInRowCol * numOfTilesInRowCol) - 1;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        TILE_HEIGHT = (int) (0.139 * width);
        TILE_WIDTH = (int) (0.139 * width);
        XPOSITION = (int) (0.2435 * width);
        YPOSITION = (int) (0.2435 * width);

        //the X coordinate of the empty tile
        emptyTileXPos = (numOfTilesInRowCol - 1) * XPOSITION;

        //the Y coordinate of the empty tile
        emptyTileYPos = (numOfTilesInRowCol - 1) * YPOSITION;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ConstraintLayout main = (ConstraintLayout) findViewById(R.id.Main);



        //constructs the puzzle
        buildPuzzle();
    }

    /**
     *
     * @param view shuffle button
     */
    public void onShuffleButtonClicked(View view) {

        //view that contain other views
        final ConstraintLayout container = (ConstraintLayout) findViewById(R.id.Container);

        isShuffled = true;

        for (int i = 0; i < NUM_OF_SHUFFLES; i++) {

            //stores the tiles that neighbor the empty tile
            ArrayList<Button> neighbors = new ArrayList<Button>();

            //stores the empty tile row number
            int emptyTileRow = (emptyTileYPos / YPOSITION) + 1;

            //stores the empty tile column number
            int emptyTileColumn = (emptyTileXPos / XPOSITION) + 1;

            NeighborID neighborIDs = new NeighborID(emptyTileRow, emptyTileColumn);
            ArrayList<Integer> emptyTileNeighborIds = neighborIDs.getNeighboringTilesIds();

            //add valid neighboring tiles to the neighbor ArrayList
            for (int j = 0; j < emptyTileNeighborIds.size(); j++) {
                int emptyTileNeighborId = emptyTileNeighborIds.get(j);
                Button emptyTileNeighbor = (Button) container.findViewById
                        (emptyTileNeighborId);

                if (emptyTileNeighbor != null) {
                    neighbors.add(emptyTileNeighbor);
                }
            }

            //gets the index of the random tile that neighbors the empty tile
            int randomNeighbor = (int) (Math.random() * neighbors.size());
            int id = Integer.parseInt((emptyTileRow) + "" + (emptyTileColumn));

            Button emptyTileNeighbor = neighbors.get(randomNeighbor);

            emptyTileNeighbor.setId(id);
            changeTilePosition(emptyTileNeighbor);

            startTimer();
        }
    }

    /**
     * starts the timer as soon as the puzzle is shuffled
     */
    public void startTimer(){

        if (timer != null) {
            timer.cancel();
        }

        seconds = 0;
        minutes = 0;
        hours = 0;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            int iClicks = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        TextView timerTextView = (TextView) findViewById(R.id.timer);

                        // task to be done every 1000 milliseconds
                        iClicks = iClicks + 1;
                        seconds = iClicks % 60;
                        minutes = (iClicks / 60);
                        hours = (minutes / (60));

                        timerTextView.setText(String.valueOf(String.format("%d: %d: %d",
                                                             hours, minutes, seconds)));
                    }
                });

            }
        }, 0, 1000);
    }


    /**
     * constructs the puzzle of the given size
     */
    private void buildPuzzle() {


        for (int i = 0; i < totalNumOfTiles; i++) {

            //creates a new button
            final Button tile = new Button(this);

            //sets the width of the tile
            tile.setWidth(TILE_WIDTH);

            //sets the height of the tile
            tile.setHeight(TILE_HEIGHT);

            //used to calculate the X coordinate of the tile
            int tileXPos = i % numOfTilesInRowCol;

            //used to calculate the X coordinate of the tile
            int tileYPos = (int) Math.floor(i / numOfTilesInRowCol);

            //sets the X coordinate of the button created
            tile.setX(tileXPos * XPOSITION);

            //sets the Y coordinate of the button created
            tile.setY(tileYPos * YPOSITION);

            //sets the id of the button created
            int id = Integer.parseInt((tileYPos + 1) + "" + (tileXPos + 1));
            tile.setId(id);

            //sets the text for the button created
            tile.setText(String.valueOf(i + 1));
            final ConstraintLayout container = (ConstraintLayout) findViewById(R.id.Container);
            container.addView(tile);

            //slides the tile that neighbors the empty tile to its place
            tile.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (isShuffled && slide.equalsIgnoreCase("click")) {
                        //the X coordinate of the tile
                        int tileXPos = (int) tile.getX();

                        //the Y coordinate of the tile
                        int tileYPos = (int) tile.getY();

                        if ((Math.abs(tileXPos - emptyTileXPos) == XPOSITION &&
                            tileYPos == emptyTileYPos) ||
                            (Math.abs(tileYPos - emptyTileYPos) == YPOSITION &&
                            tileXPos == emptyTileXPos)) {

                            int tileNewXPos = tileXPos - emptyTileXPos;
                            int tileNewYPos = tileYPos - emptyTileYPos;

                            //updates the X coordinate of the tile that neighbors the empty tile
                            tile.setX(tileXPos - tileNewXPos);

                            //updates the Y coordinate of the tile that neighbors the empty tile
                            tile.setY(tileYPos - tileNewYPos);
                            //updates the X coordinate of the empty tile
                            emptyTileXPos = tileXPos;

                            //updates the Y coordinate of the empty tile
                            emptyTileYPos = tileYPos;
                        }

                        //redirects to the high score screen if the puzzle is arranged into numerical
                        //order
                        message();
                    }
                }
            });
        }
    }




    /**
     * @param newConfig represents all of the current configurations
     *                  avoid the activity from restarting based on configuration changes
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     *
     * @param sensor
     * @param accuracy when the accuracy of the registered sensor has changed
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     *
     * @param event a new sensor event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        final int type = event.sensor.getType();

        //stores the current time
        long currentTime = System.currentTimeMillis();

        if (type == Sensor.TYPE_GYROSCOPE) {

            //ensures the time gap between the two sensor events should be at least 300 milliseconds
            if (currentTime - timestamp > TIME_GAP) {

                // axis of the rotation in the X direction
                float axisX = event.values[0];

                //axis of the rotation in the Y direction
                float axisY = event.values[1];

                Rotation rotate = new Rotation(axisX, axisY, SENSTIVITY);
                float[] rotation = rotate.getRotation();
                rotationX = rotation[0];
                rotationY = rotation[1];

                if(Math.abs(rotationX) >= SENSTIVITY || Math.abs(rotationY) >= SENSTIVITY) {
                    slide();
                }
            }
        }
    }

    //slides the tile in the direction in which the device is rotated
    public void slide(){

        if (isShuffled && slide.equalsIgnoreCase("motionSensors")) {

            final ConstraintLayout container = (ConstraintLayout) findViewById(R.id.Container);

            //stores the empty tile row number
            int emptyTileRow = (emptyTileYPos / YPOSITION) + 1;

            //stores the empty tile column number
            int emptyTileColumn = (emptyTileXPos / XPOSITION) + 1;

            //stores the Id of the neighboring tile
            int tileId = -1;
            NeighborID neighborIDs = new NeighborID(emptyTileRow, emptyTileColumn);

            if (rotationY > 0) {
                tileId = neighborIDs.getLeftTileId();
            } else if (rotationY < 0) {
                tileId = neighborIDs.getRightTileId();
            } else if (rotationX < 0) {
                tileId = neighborIDs.getBottomTileId();
            } else if (rotationX > 0) {
                tileId = neighborIDs.getTopTileId();
            }

            Button emptyTileNeighbor = (Button) container.findViewById(tileId);

            if (emptyTileNeighbor != null) {

                int id = Integer.parseInt((emptyTileRow) + "" + (emptyTileColumn));
                emptyTileNeighbor.setId(id);
                changeTilePosition(emptyTileNeighbor);

                //redirects to the high score screen if the puzzle is arranged into numerical
                //order
                message();
            }

            //stores the current time
            timestamp = System.currentTimeMillis();
        }
    }

    /**
     *
     * @param emptyTileNeighbor the tile that is required to be moved
     * updates the position of the tiles
     */
    public void changeTilePosition(Button emptyTileNeighbor){

        //the X coordinate of the tile that neighbors the empty tile
        int tileXPos = (int) emptyTileNeighbor.getX();

        //the Y coordinate of the tile that neighbors the empty tile
        int tileYPos = (int) emptyTileNeighbor.getY();

        //updates the X coordinate of the tile that neighbors the empty tile
        emptyTileNeighbor.setX(emptyTileXPos);

        //updates the Y coordinate of the tile that neighbors the empty tile
        emptyTileNeighbor.setY(emptyTileYPos);

        emptyTileXPos = tileXPos;
        emptyTileYPos = tileYPos;
    }

    @Override
    protected void onResume() {

        super.onResume();

        // listener for the gyroscope sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * prints a message when the user arranges the tiles in the numerical order
     */
    public void message() {

        //stores the number of tiles that matches their expected positions
        int count = 0;

        //view that contain other views
        final ConstraintLayout container = (ConstraintLayout) findViewById(R.id.Container);

        //stores the count of child in the view group
        int childViewCount = container.getChildCount();

        for (int i = 0; i < childViewCount; i++) {

            Button button = (Button) container.getChildAt(i);

            //the expected X and Y coordinate of the tile
            int expectedXPos = (i % numOfTilesInRowCol) * XPOSITION;
            int expectedYPos = (int) ((Math.floor(i / numOfTilesInRowCol))) * YPOSITION;

            //the actual X and Y coordinate of the tile
            int tileXPos = (int) button.getX();
            int tileYPos = (int) button.getY();

            if (expectedXPos == tileXPos && expectedYPos == tileYPos) {
                count++;
            }
        }

        if (count == totalNumOfTiles) {
            if (timer != null) {
                timer.cancel();
            }

            Intent intent = new Intent(getApplicationContext(), HighScore.class);

            intent.putExtra("Hours", hours);
            intent.putExtra("Minutes", minutes);
            intent.putExtra("Seconds", seconds);

            startActivity(intent);

            isShuffled = false;
        }
    }
}