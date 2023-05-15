package com.kashithekash.violinistica;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * This Activity handles customisation of Violinistica. In particular, it:
 *     Changes which buttons are visible and take up screen space,
 *     Sets the device's baseline tilt,
 *     Changes the app's string tilt range
 */
public class CustomiseMode extends Activity {

    private Button playOpenStringButton, noteButton1, noteButton2, noteButton3,
            noteButton4, noteButton5, noteButton6, noteButton7,
            noteButton8, noteButton9, noteButton10, noteButton11,
            noteButton12, activitySwitchButton, calibrateButton;
    private Button[] noteButtons;
    private SeekBar stringTiltRangeSlider;
    private TextView strMinText, strMaxText, currentStringTiltRangeText;

    private SensorManager sensorManager;
    private Sensor rvSensor;

    private float currentRoll;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.customise_mode_layout);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rvSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(tiltChangeListener, rvSensor, SensorManager.SENSOR_DELAY_GAME);

        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        loadGUIElements();
        loadButtonArray();
        registerListeners();
        loadStringTiltRangeSliderProgress();
        loadStringTiltRangeSliderLimitText();
    }

    @Override
    protected void onStart() { super.onStart(); }

    @Override
    protected void onResume() {

        loadButtonVisibilities();
        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
        sharedPreferencesEditor.putFloat("tilt_range", Constants.getStringTiltRange());
        sharedPreferencesEditor.apply();
    }

    @Override
    protected void onStop() { super.onStop(); }

    /**
     * This SensorEventListener detects changes in device rotation on the x, y, and z axes.
     * In particular, it isolates tilt along the device's y axis (the axis that is parallel to
     * the device's long side) and stores the value in the local variable currentRoll.
     */
    private SensorEventListener tiltChangeListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                updateRoll(event);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        /**
         * Reads data from the device's tilt sensor and stores the value of tilt along the device's
         * y axis in the local variable currentRoll.
         *
         * @param event an event (e.g. movement, rotation, change in magnetic field) detected by
         *              the device's many sensors.
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
        }
    };

    /**
     * This OnClickListener is used to cycle between the View visibilities of the View to which it
     * is attached, and store the new visibility in the Constants class.
     */
    private View.OnClickListener visibilityToggleButtonListener = new View.OnClickListener() {

        public void onClick(View v) {

            switch (sharedPreferences.getInt(v.getId() + "_visibility", 0)) {

                case View.VISIBLE:
                    ((Button) v).setBackgroundColor(getResources().getColor(R.color.background));
                    ((Button) v).setTextColor(getResources().getColor(R.color.text));
                    ((Button) v).setText("Invisible");
                    sharedPreferencesEditor.putInt(v.getId() + "_visibility", View.INVISIBLE);
                    sharedPreferencesEditor.apply();
                    break;
                case View.INVISIBLE:
                    ((Button) v).setBackgroundColor(getResources().getColor(R.color.background));
                    ((Button) v).setTextColor(getResources().getColor(R.color.textAlt));
                    ((Button) v).setText("Gone");
                    sharedPreferencesEditor.putInt(v.getId() + "_visibility", View.GONE);
                    sharedPreferencesEditor.apply();
                    break;
                case View.GONE:
                    ((Button) v).setBackgroundColor(getResources().getColor(R.color.button));
                    ((Button) v).setTextColor(getResources().getColor(R.color.textButton));
                    ((Button) v).setText("Visible");
                    sharedPreferencesEditor.putInt(v.getId() + "_visibility", View.VISIBLE);
                    sharedPreferencesEditor.apply();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Sets float in the Constants class to the device's current tilt.
     */
    private void calibrate() {
        Constants.setInitialRoll(currentRoll);
    }

    /**
     * Loads the state of each note button from SharedPreferences.
     */
    private void loadButtonVisibilities() {

        for (Button button : noteButtons) {

            switch (sharedPreferences.getInt(button.getId() + "_visibility", 0)) {
                case 0:
                    button.setBackgroundColor(getResources().getColor(R.color.button));
                    button.setTextColor(getResources().getColor(R.color.textButton));
                    button.setText("Visible");
                    break;
                case 4:
                    button.setBackgroundColor(getResources().getColor(R.color.background));
                    button.setTextColor(getResources().getColor(R.color.text));
                    button.setText("Invisible");
                    break;
                case 8:
                    button.setBackgroundColor(getResources().getColor(R.color.background));
                    button.setTextColor(getResources().getColor(R.color.textAlt));
                    button.setText("Gone");
                    break;
                default:
                    System.out.println("Something inexplicable has happened. See CustomiseMode.java, void loadButtonVisibilities().");
                    break;
            }
        }
    }

    /**
     * Sets the string tilt range slider SeekBar's progress to reflect the value stored in
     * the local instance of Constants.
     */
    private void loadStringTiltRangeSliderProgress() {
        stringTiltRangeSlider.setProgress((int)(100 * (Constants.getStringTiltRange() - Constants.STR_MIN) / (Constants.STR_MAX - Constants.STR_MIN)));
    }

    /**
     * Calculates string tilt range based on progress and passes the value to the Constants class,
     * and sets the text in the play mode view to reflect the newly set string tilt range.
     *
     * @param progress integer representing the position of the SeekBar
     */
    private void updateStringTiltRange(int progress) {
        Constants.setStringTiltRange(progress / 100f * (Constants.STR_MAX - Constants.STR_MIN) + Constants.STR_MIN);
        currentStringTiltRangeText.setText((int)(Math.round(Constants.getStringTiltRange())) + " degrees");
    }

    /**
     * Gets minimum and maximum possible string tilt range values from the Constants class
     * and sets the corresponding play mode view text to reflect them.
     */
    private void loadStringTiltRangeSliderLimitText() {
        strMinText.setText("" + (int)Constants.STR_MIN);
        strMaxText.setText("" + (int)Constants.STR_MAX);
    }

    /**
     * Places initialised View variables representing note buttons into an array for easy iteration.
     */
    private void loadButtonArray() {
        noteButtons = new Button[] { playOpenStringButton, noteButton1, noteButton2, noteButton3,
                noteButton4, noteButton5, noteButton6, noteButton7,
                noteButton8, noteButton9, noteButton10, noteButton11,
                noteButton12 };
    }

    /**
     * Initialises GUI elements.
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
        stringTiltRangeSlider = findViewById(R.id.stringTiltRangeSlider);
        calibrateButton = findViewById(R.id.calibrateButton);

        strMinText = findViewById(R.id.str_min);
        strMaxText = findViewById(R.id.str_max);
        currentStringTiltRangeText = findViewById(R.id.currentStringTiltRange);
    }

    /**
     * Attaches touch listeners to interactive GUI elements.
     */
    private void registerListeners () {

        for (Button b : noteButtons) {
            b.setOnClickListener(visibilityToggleButtonListener);
        }

        activitySwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We end the CustomiseMode activity and return to the paused PlayMode activity
                // when the activitySwitchButton is clicked; this ensures that at most one instance
                // of this activity is running.
                finish();
            }
        });

        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibrate();
            }
        });

        stringTiltRangeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // progress is determined automatically by the SeekBar based on the position of
                // the SeekBar thumb.
                updateStringTiltRange(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
