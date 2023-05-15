package com.kashithekash.violinistica;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;

import java.util.HashMap;

/**
 * The PlayMode Activity is the app's default activity, and is where the user will spend most of
 * their time. It handles tilt detection and playing the notes' sound files.
 */
public class PlayMode extends Activity {

    PlayModeHelper playModeHelper;

    SensorManager sensorManager;
    Sensor rvSensor;        // Rotation Vector sensor; uses accelerometer and magnetic field sensors

    Vibrator v;

    SoundPool soundPool;
    boolean isLoaded = false;   // Whether the SoundPool instance has loaded

    // Stream id of current soundpool audio
    int streamID = -1;

    AudioManager audioManager;

    // A HashMap to load the notes for SoundPool
    HashMap<Integer, Integer> noteMap = new HashMap<Integer, Integer>();

    Button playOpenStringButton, noteButton1, noteButton2, noteButton3,
            noteButton4, noteButton5, noteButton6, noteButton7,
            noteButton8, noteButton9, noteButton10, noteButton11,
            noteButton12, activitySwitchButton;
    Button[] noteButtons;
    SeekBar tiltIndicator;
    float initialRoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.play_mode_layout);

        playModeHelper = new PlayModeHelper();
        playModeHelper.setNoteMap(noteMap);

        playModeHelper.setStringTiltRange(Constants.getStringTiltRange());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rvSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(tiltChangeListener, rvSensor, SensorManager.SENSOR_DELAY_GAME);

         v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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
        loadButtonArray();
        loadNotes();        // SoundPool requires sound files to be loaded before they can be played
        registerListeners();
        setButtonVisibilities();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This happens after we return from the CustomiseMode ability
        setButtonVisibilities();
        setStringTiltRange();
        setInitialRoll();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Handles all events related to changes in device tilt.
     */
    private SensorEventListener tiltChangeListener = new SensorEventListener() {

        boolean initialRollSet = false;

        float currentRoll;
        float deltaRoll;

        int notePlaying = -1;
        int noteToPlay = -1;

        ViolinString prevString = null;
        ViolinString currString = null;

        /**
         * Updates value of deltaRoll stored in the local instance of PlayModeHelper, detects when
         * the current violin string changes, and causes device vibration when it does. If a note
         * is playing when the string changes, this function stops the sound file and starts the new
         * correct one.
         *
         * @param event any event detected by the device's many sensors.
         */
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                updateRoll(event);
                playModeHelper.updateDeltaRoll(deltaRoll);
            }

            updateCurrentViolinString();

            if (currString != playModeHelper.getCurrentViolinString()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(5);
                }

                prevString = currString;
                currString = playModeHelper.getCurrentViolinString();
                updateNoteButtonText();
            }

            updateTiltIndicator();

            noteToPlay = updateNote();

            if (noteToPlay != notePlaying) {

                stopNote(streamID);
                playNote(noteToPlay);

                notePlaying = noteToPlay;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        /**
         * Isolates device tilt along y axis (the axis parallel to the long side) and sets the value
         * of local float deltaRoll to that value.
         *
         * @param event a device rotation event
         */
        private void updateRoll (SensorEvent event) {

            float[] rotationMatrix = new float[9];

            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            int worldAxisX = SensorManager.AXIS_X;
            int worldAxisY = SensorManager.AXIS_Y;

            float[] adjustedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisY, adjustedRotationMatrix);

            float[] orientation = new float[3];

            SensorManager.getOrientation(adjustedRotationMatrix, orientation);

            currentRoll = orientation[2] * -180 / (float)Math.PI;

            if (!initialRollSet) {
                initialRoll = currentRoll;
                Constants.setInitialRoll(initialRoll);
                initialRollSet = true;
            }

            deltaRoll = currentRoll - initialRoll;
        }
    };

    /**
     * This is the OnTouchListener attached to the buttons used to play notes. When touched it tells
     * the local instance of PlayModeHelper which button was touched, and updates the button's
     * background and text colours to indicate that it is being touched.
     */
    private OnTouchListener noteButtonListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundColor(getResources().getColor(R.color.background));
                ((Button) v).setTextColor(getResources().getColor(R.color.textAlt));
                playModeHelper.updateFingerPosition(v);
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setBackgroundColor(getResources().getColor(R.color.backgroundAlt));
                ((Button) v).setTextColor(getResources().getColor(R.color.text));
                playModeHelper.updateFingerPosition(null);
            }
            return false;
        }
    };

    /**
     * @return note sound file ID from the HashMap in the local instance of PlayModeHelper.
     */
    private int updateNote() {
        return playModeHelper.getNote();
    }

    /**
     * Sets the local int streamID to the value returned when SoundPool plays the sound file
     * whose resource ID is the value of the argument note.
     * @param note int ID of the note sound file.
     */
    private void playNote(int note) {
        streamID = soundPool.play(note, 1, 1, 0, -1, 1);
    }

    /**
     * Stops the sound whose stream ID is argument streamID and sets local int streamID to -1.
     *
     * @param streamID int ID of the currently playing note.
     */
    private void stopNote(int streamID) {
        if (streamID != -1) soundPool.stop(streamID);
        this.streamID = -1;
    }

    /**
     * Sets value of local float initialRoll to the value provided by the Constants class.
     */
    private void setInitialRoll() {
        initialRoll = Constants.getInitialRoll();
    }

    /**
     * Tells local instance of PlayModeHelper to set the value of string tilt range to the value
     * provided by the Constants class.
     */
    private void setStringTiltRange() {
        playModeHelper.setStringTiltRange(Constants.getStringTiltRange());
    }

    /**
     * Tells local instance of PlayModeHelper to update its value of current violin string.
     */
    private void updateCurrentViolinString() {
        playModeHelper.updateViolinString();
    }

    /**
     * Tells local instance of PlayModeHelper to update the tilt indicator SeekBar.
     */
    private void updateTiltIndicator() {
        playModeHelper.updateTiltIndicator(tiltIndicator);
    }

    /**
     * Sets the text of each note button to match the note it will play if touched.
     */
    private void updateNoteButtonText() {

        for (Button button : noteButtons) {
            button.setText(playModeHelper.getNoteString(button.getId()));
        }
    }

    /**
     * Sets the visibility of each note button to the values provided by the Constants class.
     */
    private void setButtonVisibilities() {

        for (Button button : noteButtons) {
            button.setVisibility(Constants.getButtonVisibility(button.getId()));
        }
    }

    /**
     * Initialises all play mode GUI elements.
     */
    private void loadGUIElements () {

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
        activitySwitchButton = findViewById(R.id.activitySwitchButton);
        tiltIndicator = findViewById(R.id.tiltIndicator);
    }

    /**
     * Stores initialised note button variables into an array for easy iteration.
     */
    private void loadButtonArray() {

        noteButtons = new Button[] { playOpenStringButton, noteButton1, noteButton2, noteButton3,
                noteButton4, noteButton5, noteButton6, noteButton7,
                noteButton8, noteButton9, noteButton10, noteButton11,
                noteButton12 };
    }

    /**
     * Loads each note's sound file using the local SoundPool instance and places it (using its
     * resource ID) in the local HashMap noteMap.
     */
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

    /**
     * Attaches appropriate touch listeners to all interactive GUI elements.
     */
    private void registerListeners () {

        for (Button button : noteButtons) {
            button.setOnTouchListener(noteButtonListener);
        }

        activitySwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CustomiseMode.class);
                startActivity(intent);
            }
        });

        tiltIndicator.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // This is necessary because without it, the user would be able to change the tilt
                // indicator SeekBar's progress interactively. We want to do it prorammatically.
                // Returning true redirects the touch input and so the SeekBar is unaffected by it.
                return true;
            }
        });
    }
}
