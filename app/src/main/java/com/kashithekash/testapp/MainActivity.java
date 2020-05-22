package com.kashithekash.testapp;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;

import static android.media.SoundPool.*;
import static android.view.View.*;


public class MainActivity extends AppCompatActivity {

    // GUI stuff; this will all get prettified eventually
    Button playOpenString;
    Button firstFinger;
    Button secondFinger;
    Button thirdFinger;
    Button fourthFinger;

    TextView currentStringText;
    TextView currentNoteText;

    // Sensor stuff
    SensorManager sensorManager;
    Sensor rvSensor;        // Rotation Vector sensor; uses accelerometer and magnetic field sensors

    // A is the default for lots of things
    ViolinString currentViolinString = ViolinString.A;

    // Map of notes. SoundPool must access notes via noteMap, because this is where they have been
    // loaded
    HashMap<Integer, Integer> noteMap;

    // Current note
    String currentNote;     // Format dsharp5, e6, etc.
    int currentNoteID;      // R.raw.a3, etc.
    int streamID = 0;
    int notePlaying = 0;

    // Detecting when the note to play has changed
    boolean stringChanged = false;
    boolean highestFingerChanged = false;

    // For actually playing the sound
    SoundPool soundPool;
    AudioManager audioManager;

    // Has it been loaded?
    boolean isLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rvSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        playOpenString = (Button) findViewById(R.id.playOpenString);
        firstFinger = (Button) findViewById(R.id.firstFinger);
        secondFinger = (Button) findViewById(R.id.secondFinger);
        thirdFinger = (Button) findViewById(R.id.thirdFinger);
        fourthFinger = (Button) findViewById(R.id.fourthFinger);

        currentStringText = (TextView) findViewById(R.id.currentViolinString);
        currentNoteText = (TextView) findViewById(R.id.currentNote);

        noteMap = new HashMap<Integer, Integer>();

        playOpenString.setOnTouchListener(touchListener);
        firstFinger.setOnTouchListener(touchListener);
        secondFinger.setOnTouchListener(touchListener);
        thirdFinger.setOnTouchListener(touchListener);
        fourthFinger.setOnTouchListener(touchListener);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundPool = new SoundPool(1, AudioManager.USE_DEFAULT_STREAM_TYPE, AudioManager.ADJUST_SAME);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                isLoaded = true;
            }
        });

        loadNotes();
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
        soundPool.release();
        soundPool = null;
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

    private void updateCurrentString(float deltaRoll) {

        if (deltaRoll <= -30)
            currentViolinString = ViolinString.G;
        else if (deltaRoll > -30 && deltaRoll <= -10)
            currentViolinString = ViolinString.D;
        else if (deltaRoll > -10 && deltaRoll <= 10)
            currentViolinString = ViolinString.A;
        else
            currentViolinString = ViolinString.E;

        currentStringText.setText(currentViolinString.toString());
    }

    OnTouchListener touchListener = new OnTouchListener() {

        boolean isHeld = true;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // Sets a flag so we can change sound when string changes
            if (event.getAction() == event.ACTION_UP) {
                isHeld = false;
                soundPool.stop(streamID);
                currentNoteID = 0;
                notePlaying = 0;
                currentNote = "None";
                return false;
            }

            if (currentNoteID != getNote(currentViolinString, v)) {
                soundPool.stop(streamID);
                currentNoteID = getNote(currentViolinString, v);
                streamID = playNote(currentNoteID);
            }

            return false;
        }
    };

    private int getNote(ViolinString currentViolinString, View v) {

        int note = 0;

        switch (currentViolinString) {
            case G:
                switch (v.getId()) {
                    case R.id.playOpenString:
                        note = R.raw.g3;
                        break;
                    case R.id.firstFinger:
                        note = R.raw.a3;
                        break;
                    case R.id.secondFinger:
                        note = R.raw.b3;
                        break;
                    case R.id.thirdFinger:
                        note = R.raw.c4;
                        break;
                    case R.id.fourthFinger:
                        note = R.raw.d4;
                        break;
                    default:
                        break;
                } break;
            case D:
                switch (v.getId()) {
                    case R.id.playOpenString:
                        note = R.raw.d4;
                        break;
                    case R.id.firstFinger:
                        note = R.raw.e4;
                        break;
                    case R.id.secondFinger:
                        note = R.raw.fsharp4;
                        break;
                    case R.id.thirdFinger:
                        note = R.raw.g4;
                        break;
                    case R.id.fourthFinger:
                        note = R.raw.a4;
                        break;
                    default:
                        break;
                } break;
            case A:
                switch (v.getId()) {
                    case R.id.playOpenString:
                        note = R.raw.a4;
                        break;
                    case R.id.firstFinger:
                        note = R.raw.b4;
                        break;
                    case R.id.secondFinger:
                        note = R.raw.csharp5;
                        break;
                    case R.id.thirdFinger:
                        note = R.raw.d5;
                        break;
                    case R.id.fourthFinger:
                        note = R.raw.e5;
                        break;
                    default:
                        break;
                } break;
            case E:
                switch (v.getId()) {
                    case R.id.playOpenString:
                        note = R.raw.e5;
                        break;
                    case R.id.firstFinger:
                        note = R.raw.fsharp5;
                        break;
                    case R.id.secondFinger:
                        note = R.raw.gsharp5;
                        break;
                    case R.id.thirdFinger:
                        note = R.raw.a5;
                        break;
                    case R.id.fourthFinger:
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

    private int playNote(int note) {
        return soundPool.play(noteMap.get(note), 1, 1, 0, -1, 1);
    }

    private void loadNotes() {

        noteMap.put(R.raw.g3, soundPool.load(this, R.raw.g3, 0));
        noteMap.put(R.raw.gsharp3, soundPool.load(this, R.raw.gsharp3, 0));
        noteMap.put(R.raw.a3, soundPool.load(this, R.raw.a3, 0));
        noteMap.put(R.raw.asharp3, soundPool.load(this, R.raw.asharp3, 0));
        noteMap.put(R.raw.b3, soundPool.load(this, R.raw.b3, 0));
        noteMap.put(R.raw.c4, soundPool.load(this, R.raw.c4, 0));
        noteMap.put(R.raw.csharp4, soundPool.load(this, R.raw.csharp4, 0));
        noteMap.put(R.raw.d4, soundPool.load(this, R.raw.d4, 0));
        noteMap.put(R.raw.dsharp4, soundPool.load(this, R.raw.dsharp4, 0));
        noteMap.put(R.raw.e4, soundPool.load(this, R.raw.e4, 0));
        noteMap.put(R.raw.f4, soundPool.load(this, R.raw.f4, 0));
        noteMap.put(R.raw.fsharp4, soundPool.load(this, R.raw.fsharp4, 0));
        noteMap.put(R.raw.g4, soundPool.load(this, R.raw.g4, 0));
        noteMap.put(R.raw.gsharp4, soundPool.load(this, R.raw.gsharp4, 0));
        noteMap.put(R.raw.a4, soundPool.load(this, R.raw.a4, 0));
        noteMap.put(R.raw.asharp4, soundPool.load(this, R.raw.asharp4, 0));
        noteMap.put(R.raw.b4, soundPool.load(this, R.raw.b4, 0));
        noteMap.put(R.raw.c5, soundPool.load(this, R.raw.c5, 0));
        noteMap.put(R.raw.csharp5, soundPool.load(this, R.raw.csharp5, 0));
        noteMap.put(R.raw.d5, soundPool.load(this, R.raw.d5, 0));
        noteMap.put(R.raw.dsharp5, soundPool.load(this, R.raw.dsharp5, 0));
        noteMap.put(R.raw.e5, soundPool.load(this, R.raw.e5, 0));
        noteMap.put(R.raw.f5, soundPool.load(this, R.raw.f5, 0));
        noteMap.put(R.raw.fsharp5, soundPool.load(this, R.raw.fsharp5, 0));
        noteMap.put(R.raw.g5, soundPool.load(this, R.raw.g5, 0));
        noteMap.put(R.raw.gsharp5, soundPool.load(this, R.raw.gsharp5, 0));
        noteMap.put(R.raw.a5, soundPool.load(this, R.raw.a5, 0));
        noteMap.put(R.raw.asharp5, soundPool.load(this, R.raw.asharp5, 0));
        noteMap.put(R.raw.b5, soundPool.load(this, R.raw.b5, 0));
        noteMap.put(R.raw.c6, soundPool.load(this, R.raw.c6, 0));
        noteMap.put(R.raw.csharp6, soundPool.load(this, R.raw.csharp6, 0));
        noteMap.put(R.raw.d6, soundPool.load(this, R.raw.d6, 0));
        noteMap.put(R.raw.dsharp6, soundPool.load(this, R.raw.dsharp6, 0));
        noteMap.put(R.raw.e6, soundPool.load(this, R.raw.e6, 0));
        noteMap.put(R.raw.f6, soundPool.load(this, R.raw.f6, 0));
        noteMap.put(R.raw.fsharp6, soundPool.load(this, R.raw.fsharp6, 0));
        noteMap.put(R.raw.g6, soundPool.load(this, R.raw.g6, 0));
    }

    enum ViolinString { G, D, A, E }
}
