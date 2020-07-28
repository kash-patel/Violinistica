package com.kashithekash.violinistica;

import android.app.Activity;
import android.content.Context;
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

public class CustomiseMode extends Activity {

    Button playOpenStringButton, noteButton1, noteButton2, noteButton3,
            noteButton4, noteButton5, noteButton6, noteButton7,
            noteButton8, noteButton9, noteButton10, noteButton11,
            noteButton12, activitySwitchButton, calibrateButton;
    Button[] buttons;
    SeekBar stringTiltRangeSlider;
    TextView strMinText, strMaxText, currentStringTiltRangeText;

    SensorManager sensorManager;
    Sensor rvSensor;

    float currentRoll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.customise_mode_layout);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rvSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(tiltChangeListener, rvSensor, SensorManager.SENSOR_DELAY_GAME);

        loadGUIElements();
        loadButtonArray();
        registerListeners();
        setStringTiltRangeSliderProgress();
        setStringTiltRangeSliderLimitText();
        setButtonVisibilities();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

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

    private View.OnClickListener visibilityToggleButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Constants.toggleButtonVisibility(v.getId());
            setButtonVisibilities();
        }
    };

    private void calibrate() {
        Constants.setInitialRoll(currentRoll);
    }

    private void setButtonVisibilities() {

        for (Button b : buttons) {

            if (Constants.getButtonVisibility(b.getId()) == View.INVISIBLE) {
                b.setBackgroundColor(getResources().getColor(R.color.background));
                b.setTextColor(getResources().getColor(R.color.text));
                b.setText("Invisible");
            } else if (Constants.getButtonVisibility(b.getId()) == View.GONE) {
                b.setBackgroundColor(getResources().getColor(R.color.background));
                b.setTextColor(getResources().getColor(R.color.textDarker));
                b.setText("Gone");
            } else {
                b.setBackgroundColor(getResources().getColor(R.color.backgroundLight));
                b.setTextColor(getResources().getColor(R.color.text));
                b.setText("Visible");
            }
        }
    }

    private void setStringTiltRangeSliderProgress() {
        stringTiltRangeSlider.setProgress((int)(100 * (Constants.getStringTiltRange() - Constants.STR_MIN) / (Constants.STR_MAX - Constants.STR_MIN)));
    }

    private void updateStringTiltRange(int progress) {
        Constants.setStringTiltRange(progress / 100f * (Constants.STR_MAX - Constants.STR_MIN) + Constants.STR_MIN);
        currentStringTiltRangeText.setText((int)(Math.round(Constants.getStringTiltRange())) + " degrees");
    }

    private void setStringTiltRangeSliderLimitText() {
        strMinText.setText("" + (int)Constants.STR_MIN);
        strMaxText.setText("" + (int)Constants.STR_MAX);
    }

    private void loadButtonArray() {
        buttons = new Button[] { playOpenStringButton, noteButton1, noteButton2, noteButton3,
                noteButton4, noteButton5, noteButton6, noteButton7,
                noteButton8, noteButton9, noteButton10, noteButton11,
                noteButton12 };
    }

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

    private void registerListeners () {

        for (Button b : buttons) {
            b.setOnClickListener(visibilityToggleButtonListener);
        }

        activitySwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
