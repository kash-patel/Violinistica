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
import android.widget.Button;

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

        playModeHelper.loadNotes(soundPool, this);
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
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isHeld = false;
                stopNote(streamID);
                currentNoteID = 0;
                notePlaying = 0;
                currentNote = "None";
                return false;
            }

            if (currentNoteID != getNote(currentViolinString, v)) {
                if (streamID != -1) stopNote(streamID);
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

    int playNote(int note) {
        return soundPool.play(playModeHelper.getNote(note), 1, 1, 0, -1, 1);
    }

    void stopNote(int streamID) {
        soundPool.stop(streamID);
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
}
