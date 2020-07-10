package com.kashithekash.violinistica;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class TiltSensor {

    SensorEventListener sensorListener = new SensorEventListener() {

        boolean initialRollSet = false;

        float initialRoll;
        float currentRoll;
        float deltaRoll;

        PlayMode.ViolinString prevString = PlayMode.ViolinString.A;   // Again, A is the default for a lot of things

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

                updateRoll(event);
//                updateCurrentString(deltaRoll);

//                if (!stringChanged && currentViolinString != prevString) {
//                    stringChanged = true;
//                    prevString = currentViolinString;
//                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        private void updateRoll (SensorEvent event) {

            float[] rotationMatrix = new float[9];

            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            int worldAxisX = SensorManager.AXIS_X;
            int worldAxisY = SensorManager.AXIS_Y;

            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisY, adjustedRotationMatrix);

            float[] orientation = new float[3];

            SensorManager.getOrientation(adjustedRotationMatrix, orientation);

            currentRoll = orientation[2] * -57;

            if (!initialRollSet) {
                initialRoll = currentRoll;
                initialRollSet = true;
            }

            deltaRoll = currentRoll - initialRoll;
        }

        public float getInitialRoll() {
            return initialRoll;
        }

        public void setInitialRoll (float ir) {
            initialRoll = ir;
        }

//        public float getCurrentRoll() {
//            return currentRoll;
//        }

        public float getDeltaRoll() {
            return deltaRoll;
        }
    };

    public SensorEventListener getSensorListener() {
        return sensorListener;
    }
}
