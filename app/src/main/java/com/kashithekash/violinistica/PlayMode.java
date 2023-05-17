package com.kashithekash.violinistica;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import androidx.annotation.RequiresApi;

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

    // Array containing active streamID of the audio file playing for each of the thirteen note buttons;
    // -1 means no stream playing
    int[] buttonStreamIDs = new int[13];

    // Array representing state of each of the thirteen note buttons
    // 0 = not pressed, 1 = pressed
    int[] noteButtonStates = new int[13];

    float[] streamVolumes = {
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f
    };

    float[] blendedInStreamVolumes = {
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            0.0f
    };

    // Values from 0 to 12; -1 indicates no button pressed.
    int highestActiveButton = -1;

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

    // We will average roll to make it smoother
    float[] previousRolls = {0, 0, 0};

    static final float ONE_THIRD = 1 / 3.0f;
    static final int MAX_VOLUME = 1;
    static final int FADE_IN_TIME_MS = 30;
    static final int FADE_OUT_TIME_MS = 100;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

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

        soundPool = new SoundPool(2, AudioManager.USE_DEFAULT_STREAM_TYPE, AudioManager.ADJUST_SAME);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                isLoaded = true;
            }
        });

        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        loadGUIElements();
        loadButtonArray();
        loadNotes();        // SoundPool requires sound files to be loaded before they can be played
        registerListeners();

        loadButtonVisibilities();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This happens after we return from the CustomiseMode ability
        loadButtonVisibilities();
        loadStringTiltRange();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
    }

    /**
     * Handles all events related to changes in device tilt.
     */
    private final SensorEventListener tiltChangeListener = new SensorEventListener() {

        boolean initialRollSet = false;

        float currentRoll;
        float deltaRoll;

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
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                updateRoll(event);
                playModeHelper.updateDeltaRoll(deltaRoll);
            }

            playModeHelper.updateViolinString();

            if (currString != playModeHelper.getCurrentViolinString()) {

                // This requires API >= 26; since the Google Play store requires all apps to target
                // API >= 33, I decided not add an alternative for pre-26 SDKs.
                v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

                prevString = currString;
                currString = playModeHelper.getCurrentViolinString();
                updateNoteButtonText();

                if (highestActiveButton > -1)
                    blendNotes(playModeHelper.getButtonID(highestActiveButton));
            }

            updateTiltIndicator();
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

            previousRolls[0] = previousRolls[1];
            previousRolls[1] = previousRolls[2];
            previousRolls[2] = deltaRoll;

            deltaRoll = (previousRolls[0] + previousRolls[1] + previousRolls[2]) * ONE_THIRD;
        }
    };

    /**
     * This is the OnTouchListener attached to the buttons used to play notes. When touched it tells
     * the local instance of PlayModeHelper which button was touched, and updates the button's
     * background and text colours to indicate that it is being touched.
     */
    private final OnTouchListener noteButtonListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int buttonNum = playModeHelper.getButtonNum(v.getId());

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                noteButtonStates[buttonNum] = 1;

                if (buttonNum > highestActiveButton) {

                    if (highestActiveButton > -1)
                        stopNote(playModeHelper.getButtonID(highestActiveButton));

                    highestActiveButton = buttonNum;

                    if (streamVolumes[buttonNum] <= 0.1)
                        playNote(v.getId());
                    else
                        blendNotes(v.getId());
                }

                v.setBackgroundColor(getResources().getColor(R.color.buttonActive));
                ((Button) v).setTextColor(getResources().getColor(R.color.textActive));
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {

                noteButtonStates[buttonNum] = 0;

                if (buttonNum == highestActiveButton) {

                    stopNote(v.getId());

                    highestActiveButton = playModeHelper.intArrayMaxIndex(noteButtonStates);

                    if (highestActiveButton > -1) {

                        if (streamVolumes[buttonNum] <= 0.1)
                            playNote(playModeHelper.getButtonID(highestActiveButton));
                        else
                            blendNotes(playModeHelper.getButtonID(highestActiveButton));
                    }
                }

                v.setBackgroundColor(getResources().getColor(R.color.button));
                ((Button) v).setTextColor(getResources().getColor(R.color.textButton));
            }

            return false;
        }
    };

    /**
     * Sets the local int streamID to the value returned when SoundPool plays the sound file
     * whose resource ID is the value of the argument note.
     * @param buttonID : ID of Button View used to determine what note to play.
     */
    private void playNote(int buttonID) {
        int note = playModeHelper.getNote(buttonID);
        int streamNum = playModeHelper.getButtonNum(buttonID);
        int streamID = soundPool.play(note, streamVolumes[streamNum], streamVolumes[streamNum], 0, -1, 1);
        buttonStreamIDs[playModeHelper.getButtonNum(buttonID)] = streamID;
        fadeInSound(streamID, streamNum);
    }

    /**
     * Stops the sound whose stream ID is argument streamID and sets local int streamID to -1.
     */
    private void stopNote(int buttonID) {
        int streamID = buttonStreamIDs[playModeHelper.getButtonNum(buttonID)];
        int streamNum = playModeHelper.getButtonNum(buttonID);
        buttonStreamIDs[playModeHelper.getButtonNum(buttonID)] = -1;
        fadeOutSound(streamID, streamNum);
    }

    /**
     * For the specific case of transitioning from one string to another, or repeatedly tapping
     * the same button in quick succession.
     * @param buttonID : ID of Button View used to determine what note to blend in.
     */
    private void blendNotes(int buttonID) {

        int note = playModeHelper.getNote(buttonID);
        int streamNum = playModeHelper.getButtonNum(buttonID);
        int inStreamID = buttonStreamIDs[playModeHelper.getButtonNum(buttonID)];
        int outStreamID = soundPool.play(note, blendedInStreamVolumes[streamNum], blendedInStreamVolumes[streamNum], 0, -1, 1);

        blendSound(inStreamID, outStreamID, streamNum);

        buttonStreamIDs[playModeHelper.getButtonNum(buttonID)] = outStreamID;
    }

    private void fadeInSound (final int streamID, final int streamNum) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (highestActiveButton > -1 && streamVolumes[streamNum] <= MAX_VOLUME) {

                    streamVolumes[streamNum] += (MAX_VOLUME / (FADE_IN_TIME_MS / 10.0f));

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    soundPool.setVolume(streamID, streamVolumes[streamNum], streamVolumes[streamNum]);
                }
            }
        }).start();
    }

    private void fadeOutSound (final int streamID, final int streamNum) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (streamVolumes[streamNum] > 0) {

                    streamVolumes[streamNum] -= (MAX_VOLUME / (FADE_OUT_TIME_MS / 10.0f));

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    soundPool.setVolume(streamID, streamVolumes[streamNum], streamVolumes[streamNum]);
                }

                soundPool.stop(streamID);
                streamVolumes[streamNum] = 0.0f;
            }
        }).start();
    }

    private void blendSound (final int outStreamID, final int inStreamID, final int streamNum) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (streamVolumes[streamNum] > 0 || blendedInStreamVolumes[streamNum] < MAX_VOLUME) {

                    streamVolumes[streamNum] -= (MAX_VOLUME / (FADE_OUT_TIME_MS / 10.0f));
                    blendedInStreamVolumes[streamNum] += (MAX_VOLUME / (FADE_OUT_TIME_MS / 10.0f));

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    soundPool.setVolume(inStreamID, blendedInStreamVolumes[streamNum], blendedInStreamVolumes[streamNum]);
                    soundPool.setVolume(outStreamID, streamVolumes[streamNum], streamVolumes[streamNum]);
                }

                soundPool.stop(outStreamID);
                soundPool.setVolume(outStreamID, 0.0f, 0.0f);
                soundPool.setVolume(inStreamID, MAX_VOLUME, MAX_VOLUME);

                streamVolumes[streamNum] = MAX_VOLUME;
                blendedInStreamVolumes[streamNum] = 0.0f;
            }
        }).start();
    }

    /**
     * Sets value of local float initialRoll to the value provided by the Constants class.
     */
    private void setInitialRoll() {
        initialRoll = Constants.getInitialRoll();
    }

    /**
     * Loads the value of string tilt range to the value stored in SharedPreferences.
     */
    private void loadStringTiltRange() {

        float stringTiltRange = sharedPreferences.getFloat("tilt_range", 30f);

        playModeHelper.setStringTiltRange(stringTiltRange);
        Constants.setStringTiltRange(stringTiltRange);
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
     * Loads the visibility of each note button to the values in SharedPreferences.
     */
    private void loadButtonVisibilities() {

        for (Button button : noteButtons) {
            button.setVisibility(sharedPreferences.getInt(button.getId() + "_visibility", 0));
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
                // indicator SeekBar's progress interactively. We want to do it programmatically.
                // Returning true redirects the touch input and so the SeekBar is unaffected by it.
                return true;
            }
        });
    }
}
