package com.kashithekash.violinistica;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * This class is used to store app-wide constants including maximum and minimum values for
 * string tilt range, as well as settings changed in CustomiseMode.
 */
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

    /**
     * Sets local float initialRoll to the value of argument newInitialRoll
     *
     * @param newInitialRoll
     */
    public static void setInitialRoll(float newInitialRoll) {
        initialRoll = newInitialRoll;
    }

    /**
     * Returns value of local float initialRoll.
     *
     * @return initialRoll
     */
    public static float getInitialRoll() {
        return initialRoll;
    }

    /**
     * Returns value of local float stringTiltRange.
     *
     * @return stringTiltRange
     */
    public static float getStringTiltRange() {
        return stringTiltRange;
    }

    /**
     * Sets value of local float stringTiltRange to the value of argument newStringTiltRange.
     *
     * @param newStringTiltRange
     */
    public static void setStringTiltRange(float newStringTiltRange) {
        stringTiltRange = newStringTiltRange;
    }

    /**
     * @param viewID ID of a View
     * @return int value stored in the local int array buttonVisibilities, representing what the
     * visibility of the corresponding View should be.
     */
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
