package com.kashithekash.violinistica;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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
import android.widget.SeekBar;

import java.util.HashMap;

public class PlayMode extends Activity {

    PlayModeHelper playModeHelper;

    // Sensor stuff
//    final int sensorDelay = 1000; // Sensor delay in microseconds; 1000 microsec. is 1/1000th of a second.
    SensorManager sensorManager;
    Sensor rvSensor;        // Rotation Vector sensor; uses accelerometer and magnetic field sensors

    // SoundPool instance
    SoundPool soundPool;
    boolean isLoaded = false;   // Whether the SoundPool instance has loaded

    // Stream id of current soundpool audio
    int streamID = -1;

    // AudioManager instance
    AudioManager audioManager;

    // A HashMap to load the notes for SoundPool
    HashMap<Integer, Integer> noteMap = new HashMap<Integer, Integer>();

    // Current button being touched
    View currentlyTouchedView = null;

    // Detecting when the note to play has changed
    boolean highestFingerChanged = false;

    // GUI stuff; this will all get prettified eventually
    Button playOpenStringButton, noteButton1, noteButton2, noteButton3,
            noteButton4, noteButton5, noteButton6, noteButton7,
            noteButton8, noteButton9, noteButton10, noteButton11,
            noteButton12, activitySwitchButton;
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
//        sensorManager.unregisterListener(tiltChangeListener);
//        if (soundPool != null) soundPool.release();
//        soundPool = null;
    }

    SensorEventListener tiltChangeListener = new SensorEventListener() {

        boolean initialRollSet = false;

        float currentRoll;
        float deltaRoll;

        int notePlaying = -1;
        int noteToPlay = -1;

        ViolinString prevString = null;
        ViolinString currString = null;

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                updateRoll(event);
                playModeHelper.updateDeltaRoll(deltaRoll);
            }

            updateCurrentViolinString();

            if (currString != playModeHelper.currentViolinString) {
                prevString = currString;
                currString = playModeHelper.currentViolinString;
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

    OnTouchListener noteButtonListener = new OnTouchListener() {

        Rect r;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                playModeHelper.updateFingerPosition(v);
                r = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            }

//            if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                if (!r.contains(v.getLeft() + (int)event.getX(), v.getTop() + (int)event.getY()))
//                    playModeHelper.updateFingerPosition(null);
//                else
//                    playModeHelper.updateFingerPosition(v);
//            }

            if (event.getAction() == MotionEvent.ACTION_UP)
                playModeHelper.updateFingerPosition(null);
            
            return false;
        }
    };

    int updateNote() {
        return playModeHelper.getNote();
    }

    void playNote(int note) {
        this.streamID = soundPool.play(note, 1, 1, 0, -1, 1);
    }

    void stopNote(int streamID) {
        if (streamID != -1) soundPool.stop(streamID);
        this.streamID = -1;
    }

    private void setInitialRoll() {
        initialRoll = Constants.getInitialRoll();
    }

    private void setStringTiltRange() {
        playModeHelper.setStringTiltRange(Constants.getStringTiltRange());
    }

    void updateCurrentViolinString() {
        playModeHelper.updateViolinString();
    }

    void updateTiltIndicator() {
        playModeHelper.updateTiltIndicator(tiltIndicator);
    }

    void updateNoteButtonText() {

        playOpenStringButton.setText(playModeHelper.getNoteString(R.id.playOpenStringButton));
        noteButton1.setText(playModeHelper.getNoteString(R.id.noteButton1));
        noteButton2.setText(playModeHelper.getNoteString(R.id.noteButton2));
        noteButton3.setText(playModeHelper.getNoteString(R.id.noteButton3));
        noteButton4.setText(playModeHelper.getNoteString(R.id.noteButton4));
        noteButton5.setText(playModeHelper.getNoteString(R.id.noteButton5));
        noteButton6.setText(playModeHelper.getNoteString(R.id.noteButton6));
        noteButton7.setText(playModeHelper.getNoteString(R.id.noteButton7));
        noteButton8.setText(playModeHelper.getNoteString(R.id.noteButton8));
        noteButton9.setText(playModeHelper.getNoteString(R.id.noteButton9));
        noteButton10.setText(playModeHelper.getNoteString(R.id.noteButton10));
        noteButton11.setText(playModeHelper.getNoteString(R.id.noteButton11));
        noteButton12.setText(playModeHelper.getNoteString(R.id.noteButton12));

    }

    public void setButtonVisibilities() {

        playOpenStringButton.setVisibility(Constants.getButtonVisibility(R.id.playOpenStringButton));
        noteButton1.setVisibility(Constants.getButtonVisibility(R.id.noteButton1));
        noteButton2.setVisibility(Constants.getButtonVisibility(R.id.noteButton2));
        noteButton3.setVisibility(Constants.getButtonVisibility(R.id.noteButton3));
        noteButton4.setVisibility(Constants.getButtonVisibility(R.id.noteButton4));
        noteButton5.setVisibility(Constants.getButtonVisibility(R.id.noteButton5));
        noteButton6.setVisibility(Constants.getButtonVisibility(R.id.noteButton6));
        noteButton7.setVisibility(Constants.getButtonVisibility(R.id.noteButton7));
        noteButton8.setVisibility(Constants.getButtonVisibility(R.id.noteButton8));
        noteButton9.setVisibility(Constants.getButtonVisibility(R.id.noteButton9));
        noteButton10.setVisibility(Constants.getButtonVisibility(R.id.noteButton10));
        noteButton11.setVisibility(Constants.getButtonVisibility(R.id.noteButton11));
        noteButton12.setVisibility(Constants.getButtonVisibility(R.id.noteButton12));
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
        activitySwitchButton = findViewById(R.id.activitySwitchButton);
        tiltIndicator = findViewById(R.id.tiltIndicator);
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

    void registerListeners () {
        playOpenStringButton.setOnTouchListener(noteButtonListener);
        noteButton1.setOnTouchListener(noteButtonListener);
        noteButton2.setOnTouchListener(noteButtonListener);
        noteButton3.setOnTouchListener(noteButtonListener);
        noteButton4.setOnTouchListener(noteButtonListener);
        noteButton5.setOnTouchListener(noteButtonListener);
        noteButton6.setOnTouchListener(noteButtonListener);
        noteButton7.setOnTouchListener(noteButtonListener);
        noteButton8.setOnTouchListener(noteButtonListener);
        noteButton9.setOnTouchListener(noteButtonListener);
        noteButton10.setOnTouchListener(noteButtonListener);
        noteButton11.setOnTouchListener(noteButtonListener);
        noteButton12.setOnTouchListener(noteButtonListener);
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
                return true;
            }
        });
    }
}
