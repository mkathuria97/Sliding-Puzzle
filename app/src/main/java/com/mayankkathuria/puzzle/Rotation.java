package com.mayankkathuria.puzzle;

public class Rotation {

    private float[] rotation = new float[2];

    //axis of rotation in the X direction
    private float axisX;

    //axis of rotation in the Y direction
    private float axisY;

    //senstivity of the gyroscope/sensor
    private int senstivity;

    public Rotation(float axisX, float axisY, int senstivity){
        this.axisX = axisX;
        this.axisY = axisY;
        this.senstivity = senstivity;
    }

    /**
     *
     * @return an array storing only the greater rotation from both the axis
     */
    public float[] getRotation(){

        if (Math.abs(axisX) >= senstivity && Math.abs(axisY) >= senstivity) {
            if (Math.abs(axisX) > Math.abs(axisY)) {
                rotation[0] = axisX;
                rotation[1] = 0;
            } else {
                rotation[0] = 0;
                rotation[1] = axisY;
            }
        } else if (Math.abs(axisX) >= senstivity) {
            rotation[0] = axisX;
            rotation[1] = 0;
        } else if (Math.abs(axisY) >= senstivity) {
            rotation[0] = 0;
            rotation[1] = axisY;
        } else {
            rotation[0] = 0;
            rotation[0] = 0;
        }

        return rotation;
    }
}


