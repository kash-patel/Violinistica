package com.kashithekash.violinistica;

import android.app.Activity;
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
import android.view.View.OnTouchListener;
import android.widget.Button;

import java.util.HashMap;

public class PlayMode extends Activity {

    PlayModeHelper playModeHelper;

    // Sensor stuff
    SensorManager sensorManager;
    Sensor rvSensor;        // Rotation Vector sensor; uses accelerometer and magnetic field sensors

    // SoundPool instance
    SoundPool soundPool;
    boolean isLoaded = false;   // Whether the SoundPool instance has loaded

    // AudioManager instance
    AudioManager audioManager;

    // A is the default for lots of things
    ViolinString currentViolinString = ViolinString.A;

    // A HashMap to load the notes for SoundPool
    HashMap<Integer, Integer> noteMap;

    // Current note
    String currentNote;     // Format dsharp5, e6, etc.
    int currentNoteID;      // R.raw.a3, etc.
    int streamID = -1;
    int notePlaying = 0;

    // Detecting when the note to play has changed
    boolean stringChanged = false;
    boolean highestFingerChanged = false;

    // GUI stuff; this will all get prettified eventually
    Button playOpenStringButton, noteButton1, noteButton2, noteButton3,
            noteButton4, noteButton5, noteButton6, noteButton7,
            noteButton8, noteButton9, noteButton10, noteButton11,
            noteButton12, noteButton13, noteButton14, noteButton15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_mode_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();

        sensorManager.registerListener(sensorEventListener, rvSensor, SensorManager.SENSOR_DELAY_NORMAL);

        playModeHelper = new PlayModeHelper();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rvSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        playOpenStringButton.setOnTouchListener(touchListener);
        noteButton2.setOnTouchListener(touchListener);
        noteButton4.setOnTouchListener(touchListener);
        noteButton5.setOnTouchListener(touchListener);
        noteButton7.setOnTouchListener(touchListener);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundPool = new SoundPool(1, AudioManager.USE_DEFAULT_STREAM_TYPE, AudioManager.ADJUST_SAME);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                isLoaded = true;
            }
        });

        loadGUIElements();
        loadNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, rvSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(sensorEventListener);
        soundPool.release();
        soundPool = null;
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {

        boolean initialRollSet = false;

        float initialRoll;
        float currentRoll;
        float deltaRoll;

        ViolinString prevString = ViolinString.A;   // Again, A is the default for a lot of things

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

                updateRoll(event);
                playModeHelper.getCurrentViolinString(deltaRoll);

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

    OnTouchListener touchListener = new OnTouchListener() {

        boolean isHeld = true;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // Sets a flag so we can change sound when string changes
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isHeld = false;
                stopNote(streamID);
                currentNoteID = 0;
                notePlaying = 0;
                currentNote = "None";
                return false;
            }

            if (currentNoteID != playModeHelper.getNote(currentViolinString, v, noteMap)) {
                if (streamID != -1) stopNote(streamID);
                currentNoteID = playModeHelper.getNote(currentViolinString, v, noteMap);
                streamID = playNote(currentNoteID);
            }

            return false;
        }
    };

    GUIListeners.ButtonListener buttonListener = new GUIListeners.ButtonListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            

            return super.onTouch(v, event);
        }
    };

    int playNote(int note) {
        return soundPool.play(note, 1, 1, 0, -1, 1);
    }

    void stopNote(int streamID) {
        soundPool.stop(streamID);
    }

    public void loadGUIElements () {

        playOpenStringButton = findViewById(R.id.playOpenStringButton);
        noteButton1 = findViewById(R.id.noteButton1);
        noteButton2 = findViewById(R.id.noteButton2);
        noteButton3 = findViewById(R.id.noteButton3);
        noteButton4 = findViewById(R.id.noteButton4);
        noteButton5 = findViewById(R.id.noteButton5);
        noteButton6 = findViewById(R.id.noteButton6);
        noteButton7 = findViewById(R.id.noteButton7);
        noteButton8 = findViewById(R.id.noteButton8);
        noteButton9 = findViewById(R.id.noteButton9);
        noteButton10 = findViewById(R.id.noteButton10);
        noteButton11 = findViewById(R.id.noteButton11);
        noteButton12 = findViewById(R.id.noteButton12);
        noteButton13 = findViewById(R.id.noteButton13);
        noteButton14 = findViewById(R.id.noteButton14);
        noteButton15 = findViewById(R.id.noteButton15);
    }

    void loadNotes() {

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
}
