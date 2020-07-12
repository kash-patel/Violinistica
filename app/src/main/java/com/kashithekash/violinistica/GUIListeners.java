package com.kashithekash.violinistica;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;

public class GUIListeners {

    public static class ButtonListener implements OnTouchListener {

        boolean isTouched = true;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_UP) {
                isTouched = false;
            }

            return false;
        }

        public boolean isTouched() {
            return isTouched;
        }
    }

    public static class StringTiltRangeSeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

//    View.OnTouchListener touchListener = new View.OnTouchListener() {
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                isTouched = true;
//            } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                isTouched = false;
//            }
//
//            return false;
//        }
//    };
}
