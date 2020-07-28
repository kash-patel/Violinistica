package com.kashithekash.violinistica;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Constants {

    private static float stringTiltRange = 30f;
    public static final float STR_MIN = 10f;
    public static final float STR_MAX = 45f;

    private static int[] buttonVisibilities = {
            VISIBLE, VISIBLE, VISIBLE, VISIBLE,
            VISIBLE, VISIBLE, VISIBLE, VISIBLE,
            VISIBLE, VISIBLE, VISIBLE, VISIBLE,
            VISIBLE
    };

    private static float initialRoll;

    public static void setInitialRoll(float i) {
        initialRoll = i;
    }

    public static float getInitialRoll() {
        return initialRoll;
    }

    public static float getStringTiltRange() {
        return stringTiltRange;
    }

    public static void setStringTiltRange(float str) {
        stringTiltRange = str;
    }

    public static void toggleButtonVisibility(int viewID) {

        int index = 0;
        int visibility = 0;

        switch (viewID) {
            case R.id.noteButton1:
                index = 1;
                break;
            case R.id.noteButton2:
                index = 2;
                break;
            case R.id.noteButton3:
                index = 3;
                break;
            case R.id.noteButton4:
                index = 4;
                break;
            case R.id.noteButton5:
                index = 5;
                break;
            case R.id.noteButton6:
                index = 6;
                break;
            case R.id.noteButton7:
                index = 7;
                break;
            case R.id.noteButton8:
                index = 8;
                break;
            case R.id.noteButton9:
                index = 9;
                break;
            case R.id.noteButton10:
                index = 10;
                break;
            case R.id.noteButton11:
                index = 11;
                break;
            case R.id.noteButton12:
                index = 12;
                break;
            default:
                index = 0;
                break;
        }

        switch (buttonVisibilities[index]) {
            case VISIBLE:
                visibility = INVISIBLE;
                break;
            case INVISIBLE:
                visibility = GONE;
                break;
            default:
                visibility = VISIBLE;
                break;
        }

        buttonVisibilities[index] = visibility;
    }

    public static int getButtonVisibility(int viewID) {
        switch (viewID) {
            case R.id.noteButton1:
                return buttonVisibilities[1];
            case R.id.noteButton2:
                return buttonVisibilities[2];
            case R.id.noteButton3:
                return buttonVisibilities[3];
            case R.id.noteButton4:
                return buttonVisibilities[4];
            case R.id.noteButton5:
                return buttonVisibilities[5];
            case R.id.noteButton6:
                return buttonVisibilities[6];
            case R.id.noteButton7:
                return buttonVisibilities[7];
            case R.id.noteButton8:
                return buttonVisibilities[8];
            case R.id.noteButton9:
                return buttonVisibilities[9];
            case R.id.noteButton10:
                return buttonVisibilities[10];
            case R.id.noteButton11:
                return buttonVisibilities[11];
            case R.id.noteButton12:
                return buttonVisibilities[12];
            default:
                return buttonVisibilities[0];
        }
    }
}
