package com.kashithekash.violinistica;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class PlayMode extends AppCompatActivity {

    PlayNotes playNotes;

    // GUI stuff; this will all get prettified eventually
    Button playOpenStringButton, noteButton1, noteButton2, noteButton3,
            noteButton4, noteButton5, noteButton6, noteButton7,
            noteButton8, noteButton9, noteButton10, noteButton11,
            noteButton12, noteButton13, noteButton14, noteButton15;

    // Sensor stuff
    SensorManager sensorManager;
    Sensor rvSensor;        // Rotation Vector sensor; uses accelerometer and magnetic field sensors

    // A is the default for lots of things
    ViolinString currentViolinString = ViolinString.A;

    // Current note
    String currentNote;     // Format dsharp5, e6, etc.
    int currentNoteID;      // R.raw.a3, etc.
    int streamID = -1;
    int notePlaying = 0;

    // Detecting when the note to play has changed
    boolean stringChanged = false;
    boolean highestFingerChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playNotes = new PlayNotes();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rvSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        playOpenStringButton = (Button) findViewById(R.id.playOpenStringButton);
        noteButton1 = (Button) findViewById(R.id.noteButton1);
        noteButton2 = (Button) findViewById(R.id.noteButton2);
        noteButton3 = (Button) findViewById(R.id.noteButton3);
        noteButton4 = (Button) findViewById(R.id.noteButton4);
        noteButton5 = (Button) findViewById(R.id.noteButton5);
        noteButton6 = (Button) findViewById(R.id.noteButton6);
        noteButton7 = (Button) findViewById(R.id.noteButton7);
        noteButton8 = (Button) findViewById(R.id.noteButton8);
        noteButton9 = (Button) findViewById(R.id.noteButton9);
        noteButton10 = (Button) findViewById(R.id.noteButton10);
        noteButton11 = (Button) findViewById(R.id.noteButton11);
        noteButton12 = (Button) findViewById(R.id.noteButton12);
        noteButton13 = (Button) findViewById(R.id.noteButton13);
        noteButton14 = (Button) findViewById(R.id.noteButton14);
        noteButton15 = (Button) findViewById(R.id.noteButton15);

        playOpenStringButton.setOnTouchListener(touchListener);
        noteButton2.setOnTouchListener(touchListener);
        noteButton4.setOnTouchListener(touchListener);
        noteButton5.setOnTouchListener(touchListener);
        noteButton7.setOnTouchListener(touchListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(sensorListener, rvSensor, sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener, rvSensor, sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(sensorListener);
    }

    SensorEventListener sensorListener = new SensorEventListener() {

        boolean initialRollSet = false;

        float initialRoll;
        float currentRoll;
        float deltaRoll;

        ViolinString prevString = ViolinString.A;   // Again, A is the default for a lot of things

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

                updateRoll(event);
                updateCurrentString(deltaRoll);

                if (!stringChanged && currentViolinString != prevString) {
                    stringChanged = true;
                    prevString = currentViolinString;
                }
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
    };

    View.OnTouchListener touchListener = new View.OnTouchListener() {

        boolean isHeld = true;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // Sets a flag so we can change sound when string changes
            if (event.getAction() == event.ACTION_UP) {
                isHeld = false;
                playNotes.stopNote(streamID);
                currentNoteID = 0;
                notePlaying = 0;
                currentNote = "None";
                return false;
            }

            if (currentNoteID != getNote(currentViolinString, v)) {
                if (streamID != -1) playNotes.stopNote(streamID);
                currentNoteID = getNote(currentViolinString, v);
                streamID = playNotes.playNote(currentNoteID);
            }

            return false;
        }
    };

    private int getNote(ViolinString currentViolinString, View v) {

        int note = 0;

        switch (currentViolinString) {
            case G:
                switch (v.getId()) {
                    case R.id.playOpenStringButton:
                        note = R.raw.g3;
                        break;
                    case R.id.noteButton2:
                        note = R.raw.a3;
                        break;
                    case R.id.noteButton4:
                        note = R.raw.b3;
                        break;
                    case R.id.noteButton5:
                        note = R.raw.c4;
                        break;
                    case R.id.noteButton7:
                        note = R.raw.d4;
                        break;
                    default:
                        break;
                } break;
            case D:
                switch (v.getId()) {
                    case R.id.playOpenStringButton:
                        note = R.raw.d4;
                        break;
                    case R.id.noteButton2:
                        note = R.raw.e4;
                        break;
                    case R.id.noteButton4:
                        note = R.raw.fsharp4;
                        break;
                    case R.id.noteButton5:
                        note = R.raw.g4;
                        break;
                    case R.id.noteButton7:
                        note = R.raw.a4;
                        break;
                    default:
                        break;
                } break;
            case A:
                switch (v.getId()) {
                    case R.id.playOpenStringButton:
                        note = R.raw.a4;
                        break;
                    case R.id.noteButton2:
                        note = R.raw.b4;
                        break;
                    case R.id.noteButton4:
                        note = R.raw.csharp5;
                        break;
                    case R.id.noteButton5:
                        note = R.raw.d5;
                        break;
                    case R.id.noteButton7:
                        note = R.raw.e5;
                        break;
                    default:
                        break;
                } break;
            case E:
                switch (v.getId()) {
                    case R.id.playOpenStringButton:
                        note = R.raw.e5;
                        break;
                    case R.id.noteButton2:
                        note = R.raw.fsharp5;
                        break;
                    case R.id.noteButton4:
                        note = R.raw.gsharp5;
                        break;
                    case R.id.noteButton5:
                        note = R.raw.a5;
                        break;
                    case R.id.noteButton7:
                        note = R.raw.b5;
                        break;
                    default:
                        break;
                } break;
            default:
                break;
        }

        return note;
    }

    private void updateCurrentString(float deltaRoll) {

        if (deltaRoll <= -30)
            currentViolinString = ViolinString.G;
        else if (deltaRoll > -30 && deltaRoll <= -10)
            currentViolinString = ViolinString.D;
        else if (deltaRoll > -10 && deltaRoll <= 10)
            currentViolinString = ViolinString.A;
        else
            currentViolinString = ViolinString.E;
    }

    enum ViolinString { G, D, A, E }
}
