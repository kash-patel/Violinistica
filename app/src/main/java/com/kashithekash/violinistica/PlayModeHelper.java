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
    private View fingerPosition = null;

    private float stringTiltRange = 30f;

    private String[] gStringNotes = {"G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#"};
    private String[] dStringNotes = {"D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#"};
    private String[] aStringNotes = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    private String[] eStringNotes = {"E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#"};
    private String[] currentStringNotes = null;

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

        if (deltaRoll <= -stringTiltRange && currentViolinString != ViolinString.E) {
            currentViolinString = ViolinString.E;
            currentStringNotes = eStringNotes;
        } else if (deltaRoll > -stringTiltRange && deltaRoll <= 0 && currentViolinString != ViolinString.A) {
            currentViolinString = ViolinString.A;
            currentStringNotes = aStringNotes;
        } else if (deltaRoll > 0 && deltaRoll <= stringTiltRange && currentViolinString != ViolinString.D) {
            currentViolinString = ViolinString.D;
            currentStringNotes = dStringNotes;
        } else if (deltaRoll > stringTiltRange && currentViolinString != ViolinString.G) {
            currentViolinString = ViolinString.G;
            currentStringNotes = gStringNotes;
        }
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
     * Sets local View fingerPosition to point to argument currentlyTouchedButton. This
     * represents where on the string the finger is currently placed, and therefore is used to
     * determine which note needs to be played.
     *
     * @param currentlyTouchedButton
     */
    public void updateFingerPosition(View currentlyTouchedButton) {
        fingerPosition = currentlyTouchedButton;
    }

    /**
     * Updates the progress of the tilt indicator to reflect the current value of float deltaRoll with
     * respect to string tilt range. It also flips the SeekBar as needed, since it indicates how far
     * from the centre tilt the device has been rotated.
     *
     * @param tiltIndicator
     */
    public void updateTiltIndicator(SeekBar tiltIndicator) {

        if (deltaRoll >= 0 && tiltIndicator.getScaleX() == 1) tiltIndicator.setScaleX(-1);
        else if (deltaRoll < 0 && tiltIndicator.getScaleX() == -1) tiltIndicator.setScaleX(1);

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
    public int getNote() {

        int note = -1;

        if (fingerPosition == null) return note;

        switch (currentViolinString) {
            case G:
                switch (fingerPosition.getId()) {
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
                switch (fingerPosition.getId()) {
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
                switch (fingerPosition.getId()) {
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
                switch (fingerPosition.getId()) {
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
     * Sets local HashMap noteMap to point to argument noteMap.
     *
     * @param noteMap Hash map in which SoundPool places loaded sound files.
     */
    public void setNoteMap (HashMap<Integer, Integer> noteMap) {
        this.noteMap = noteMap;
    }
}