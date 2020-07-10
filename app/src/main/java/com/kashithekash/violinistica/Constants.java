package com.kashithekash.violinistica;

public class Constants {

    float stringTiltRange = 5f;
    public static final float STR_MIN = 5f;
    public static final float STR_MAX = 15f;

    ViolinString currentViolinString = ViolinString.A;

    public float getStringTiltRange() {
        return stringTiltRange;
    }

    public void setStringTiltRange(float stringTiltRange) {
        this.stringTiltRange = stringTiltRange;
    }

    public ViolinString getCurrentViolinString() {
        return currentViolinString;
    }

    public void setCurrentViolinString(ViolinString currentViolinString) {
        this.currentViolinString = currentViolinString;
    }
}
