package com.kashithekash.violinistica;

import android.view.View;
import android.widget.SeekBar;

import java.util.HashMap;

/**
 * This class is referenced by the PlayMode class. It's purpose is to handle play mode functions
 * that are related to and affect the note playing functionality. In particular, this class is used
 * for:
 *     Determining current violin string based on device tilt,
 *     Determining which note should be played based on current violin string and currently touched
 *         button,
 *     Determining what each button's label should be based on the button and current violin string,
 *     and updating the string tilt indicator.
 */
public class PlayModeHelper {

    private float deltaRoll = 0f;

    private HashMap<Integer, Integer> noteMap;

    private ViolinString currentViolinString = ViolinString.A;
    private ViolinString oldViolinString = ViolinString.A;

    private View currentFingerPosition = null;
    private View oldFingerPosition = null;

    private float stringTiltRange = 30f;

    private String[] gStringNotes = {"G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#"};
    private String[] dStringNotes = {"D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#"};
    private String[] aStringNotes = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    private String[] eStringNotes = {"E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#"};

    private String[] currentStringNotes = aStringNotes;

    private int[] buttonIDs = {
            R.id.playOpenStringButton,
            R.id.noteButton1,
            R.id.noteButton2,
            R.id.noteButton3,
            R.id.noteButton4,
            R.id.noteButton5,
            R.id.noteButton6,
            R.id.noteButton7,
            R.id.noteButton8,
            R.id.noteButton9,
            R.id.noteButton10,
            R.id.noteButton11,
            R.id.noteButton12
    };

    /**
     * Sets local float deltaRoll to the value of the argument newDeltaRoll.
     *
     * @param newDeltaRoll
     */
    public void updateDeltaRoll(float newDeltaRoll) {
        deltaRoll = newDeltaRoll;
    }

    /**
     * Determines what the current violin string should be based on deltaRoll and stringTiltRange.
     * Also sets the local String array currentStringNotes to point to gStringNotes, dStringNotes,
     * aStringNotes, or eStringNotes, depending on what the current violin string is determined to
     * be.
     */
    public void updateViolinString() {

        if (deltaRoll <= -stringTiltRange && currentViolinString != ViolinString.G) {
            currentViolinString = ViolinString.G;
            currentStringNotes = gStringNotes;
        } else if (deltaRoll > -stringTiltRange && deltaRoll <= 0 && currentViolinString != ViolinString.D) {
            currentViolinString = ViolinString.D;
            currentStringNotes = dStringNotes;
        } else if (deltaRoll > 0 && deltaRoll <= stringTiltRange && currentViolinString != ViolinString.A) {
            currentViolinString = ViolinString.A;
            currentStringNotes = aStringNotes;
        } else if (deltaRoll > stringTiltRange && currentViolinString != ViolinString.E) {
            currentViolinString = ViolinString.E;
            currentStringNotes = eStringNotes;
        }

//        if (deltaRoll <= -stringTiltRange && currentViolinString != ViolinString.E) {
//            currentViolinString = ViolinString.E;
//            currentStringNotes = eStringNotes;
//        } else if (deltaRoll > -stringTiltRange && deltaRoll <= 0 && currentViolinString != ViolinString.A) {
//            currentViolinString = ViolinString.A;
//            currentStringNotes = aStringNotes;
//        } else if (deltaRoll > 0 && deltaRoll <= stringTiltRange && currentViolinString != ViolinString.D) {
//            currentViolinString = ViolinString.D;
//            currentStringNotes = dStringNotes;
//        } else if (deltaRoll > stringTiltRange && currentViolinString != ViolinString.G) {
//            currentViolinString = ViolinString.G;
//            currentStringNotes = gStringNotes;
//        }
    }

    /**
     * Sets local float stringTiltRange to the value of argument newStringTiltRange
     *
     * @param newStringTiltRange
     */
    public void setStringTiltRange(float newStringTiltRange) {
        stringTiltRange = newStringTiltRange;
    }

    /**
     * Updates the progress of the tilt indicator to reflect the current value of float deltaRoll with
     * respect to string tilt range. It also flips the SeekBar as needed, since it indicates how far
     * from the centre tilt the device has been rotated.
     *
     * @param tiltIndicator
     */
    public void updateTiltIndicator(SeekBar tiltIndicator) {

        if (deltaRoll >= 0 && tiltIndicator.getScaleX() == -1) tiltIndicator.setScaleX(1);
        else if (deltaRoll < 0 && tiltIndicator.getScaleX() == 1) tiltIndicator.setScaleX(-1);

        int progress = (int) ((Math.abs(deltaRoll) % stringTiltRange) / stringTiltRange * 100);

        if (Math.abs(deltaRoll) >= 2 * stringTiltRange) progress = 100;

        tiltIndicator.setProgress(progress);
    }

    /**
     * Based on local View fingerPosition and ViolinString currentViolinString, this function determines
     * which note must be played and return the hash map in which the notes' sound file IDs are
     * loaded. Note that it MUST return the IDs from the hash map, since SoundPool can only
     * play sound files that have been loaded first.
     *
     * Internal variables:
     *     note: int; represents the ID of the sound file of the corresponding note.
     * @return note
     */
    public int getNote(int buttonID) {

        int note = -1;

//        if (currentFingerPosition == null) return note;

        switch (currentViolinString) {
            case G:
                switch (buttonID) {
                    case R.id.playOpenStringButton:
                        note = noteMap.get(R.raw.g3);
                        break;
                    case R.id.noteButton1:
                        note = noteMap.get(R.raw.gsharp3);
                        break;
                    case R.id.noteButton2:
                        note = noteMap.get(R.raw.a3);
                        break;
                    case R.id.noteButton3:
                        note = noteMap.get(R.raw.asharp3);
                        break;
                    case R.id.noteButton4:
                        note = noteMap.get(R.raw.b3);
                        break;
                    case R.id.noteButton5:
                        note = noteMap.get(R.raw.c4);
                        break;
                    case R.id.noteButton6:
                        note = noteMap.get(R.raw.csharp4);
                        break;
                    case R.id.noteButton7:
                        note = noteMap.get(R.raw.d4);
                        break;
                    case R.id.noteButton8:
                        note = noteMap.get(R.raw.dsharp4);
                        break;
                    case R.id.noteButton9:
                        note = noteMap.get(R.raw.e4);
                        break;
                    case R.id.noteButton10:
                        note = noteMap.get(R.raw.f4);
                        break;
                    case R.id.noteButton11:
                        note = noteMap.get(R.raw.fsharp4);
                        break;
                    case R.id.noteButton12:
                        note = noteMap.get(R.raw.g4);
                        break;
                    default:
                        note = -1;
                        break;
                } break;
            case D:
                switch (buttonID) {
                    case R.id.playOpenStringButton:
                        note = noteMap.get(R.raw.d4);
                        break;
                    case R.id.noteButton1:
                        note = noteMap.get(R.raw.dsharp4);
                        break;
                    case R.id.noteButton2:
                        note = noteMap.get(R.raw.e4);
                        break;
                    case R.id.noteButton3:
                        note = noteMap.get(R.raw.f4);
                        break;
                    case R.id.noteButton4:
                        note = noteMap.get(R.raw.fsharp4);
                        break;
                    case R.id.noteButton5:
                        note = noteMap.get(R.raw.g4);
                        break;
                    case R.id.noteButton6:
                        note = noteMap.get(R.raw.gsharp4);
                        break;
                    case R.id.noteButton7:
                        note = noteMap.get(R.raw.a4);
                        break;
                    case R.id.noteButton8:
                        note = noteMap.get(R.raw.asharp4);
                        break;
                    case R.id.noteButton9:
                        note = noteMap.get(R.raw.b4);
                        break;
                    case R.id.noteButton10:
                        note = noteMap.get(R.raw.c5);
                        break;
                    case R.id.noteButton11:
                        note = noteMap.get(R.raw.csharp5);
                        break;
                    case R.id.noteButton12:
                        note = noteMap.get(R.raw.d5);
                        break;
                    default:
                        note = -1;
                        break;
                } break;
            case A:
                switch (buttonID) {
                    case R.id.playOpenStringButton:
                        note = noteMap.get(R.raw.a4);
                        break;
                    case R.id.noteButton1:
                        note = noteMap.get(R.raw.asharp4);
                        break;
                    case R.id.noteButton2:
                        note = noteMap.get(R.raw.b4);
                        break;
                    case R.id.noteButton3:
                        note = noteMap.get(R.raw.c5);
                        break;
                    case R.id.noteButton4:
                        note = noteMap.get(R.raw.csharp5);
                        break;
                    case R.id.noteButton5:
                        note = noteMap.get(R.raw.d5);
                        break;
                    case R.id.noteButton6:
                        note = noteMap.get(R.raw.dsharp5);
                        break;
                    case R.id.noteButton7:
                        note = noteMap.get(R.raw.e5);
                        break;
                    case R.id.noteButton8:
                        note = noteMap.get(R.raw.f5);
                        break;
                    case R.id.noteButton9:
                        note = noteMap.get(R.raw.fsharp5);
                        break;
                    case R.id.noteButton10:
                        note = noteMap.get(R.raw.g5);
                        break;
                    case R.id.noteButton11:
                        note = noteMap.get(R.raw.gsharp5);
                        break;
                    case R.id.noteButton12:
                        note = noteMap.get(R.raw.a5);
                        break;
                    default:
                        note = -1;
                        break;
                } break;
            case E:
                switch (buttonID) {
                    case R.id.playOpenStringButton:
                        note = noteMap.get(R.raw.e5);
                        break;
                    case R.id.noteButton1:
                        note = noteMap.get(R.raw.f5);
                        break;
                    case R.id.noteButton2:
                        note = noteMap.get(R.raw.fsharp5);
                        break;
                    case R.id.noteButton3:
                        note = noteMap.get(R.raw.g5);
                        break;
                    case R.id.noteButton4:
                        note = noteMap.get(R.raw.gsharp5);
                        break;
                    case R.id.noteButton5:
                        note = noteMap.get(R.raw.a5);
                        break;
                    case R.id.noteButton6:
                        note = noteMap.get(R.raw.asharp5);
                        break;
                    case R.id.noteButton7:
                        note = noteMap.get(R.raw.b5);
                        break;
                    case R.id.noteButton8:
                        note = noteMap.get(R.raw.c6);
                        break;
                    case R.id.noteButton9:
                        note = noteMap.get(R.raw.csharp6);
                        break;
                    case R.id.noteButton10:
                        note = noteMap.get(R.raw.d6);
                        break;
                    case R.id.noteButton11:
                        note = noteMap.get(R.raw.dsharp6);
                        break;
                    case R.id.noteButton12:
                        note = noteMap.get(R.raw.e6);
                        break;
                    default:
                        note = -1;
                        break;
                } break;
            default:
                break;
        }

        return note;
    }

    /**
     * Returns an integer representing the 'number' of the button;
     * e.g. playOpenStringButton -> 0, noteButton4 -> 4, etc.
     * @param b: ID of the button
     * @return an integer corresponding to the button's 'number'
     */
    public int getButtonNum(int b) {
        switch (b) {
            case R.id.playOpenStringButton:
                return 0;
            case R.id.noteButton1:
                return 1;
            case R.id.noteButton2:
                return 2;
            case R.id.noteButton3:
                return 3;
            case R.id.noteButton4:
                return 4;
            case R.id.noteButton5:
                return 5;
            case R.id.noteButton6:
                return 6;
            case R.id.noteButton7:
                return 7;
            case R.id.noteButton8:
                return 8;
            case R.id.noteButton9:
                return 9;
            case R.id.noteButton10:
                return 10;
            case R.id.noteButton11:
                return 11;
            case R.id.noteButton12:
                return 12;
            default:
                return -1;
        }
    }

    public int getButtonID (int num) {
        return buttonIDs[num];
    }

    /**
     * Returns value of local ViolinString currentViolinString
     * @return currentViolinString
     */
    public ViolinString getCurrentViolinString() {
        return currentViolinString;
    }

    /**
     * Based on the given view ID, determines and returns what the button's label should be.
     *
     * @param viewID
     * @return the string (from local String array currentStringNotes) representing the note that
     * each button must play
     */
    public String getNoteString(int viewID) {

        switch (viewID) {
            case R.id.playOpenStringButton:
            case R.id.noteButton12:
                return currentStringNotes[0];
            case R.id.noteButton1:
                return currentStringNotes[1];
            case R.id.noteButton2:
                return currentStringNotes[2];
            case R.id.noteButton3:
                return currentStringNotes[3];
            case R.id.noteButton4:
                return currentStringNotes[4];
            case R.id.noteButton5:
                return currentStringNotes[5];
            case R.id.noteButton6:
                return currentStringNotes[6];
            case R.id.noteButton7:
                return currentStringNotes[7];
            case R.id.noteButton8:
                return currentStringNotes[8];
            case R.id.noteButton9:
                return currentStringNotes[9];
            case R.id.noteButton10:
                return currentStringNotes[10];
            case R.id.noteButton11:
                return currentStringNotes[11];
            default:
                return "";
        }
    }

    /**
     * Returns highest index with non-zero value.
     * @param arr
     * @return
     */
    public int intArrayMaxIndex (int[] arr) {

        int maxIndex = -1;

        for (int i = 0; i < arr.length; i++)
            if (arr[i] > 0) maxIndex = i;

        return maxIndex;
    }

    /**
     * Sets local HashMap noteMap to point to argument noteMap.
     *
     * @param noteMap Hash map in which SoundPool places loaded sound files.
     */
    public void setNoteMap (HashMap<Integer, Integer> noteMap) {
        this.noteMap = noteMap;
    }
}
