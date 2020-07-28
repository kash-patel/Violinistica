package com.kashithekash.violinistica;

import android.view.View;
import android.widget.SeekBar;

import java.util.HashMap;

public class PlayModeHelper {

    private float deltaRoll = 0f;
    // Map of notes. SoundPool must access notes via noteMap, because this is where they have been
    // loaded
    HashMap<Integer, Integer> noteMap;

    ViolinString currentViolinString = ViolinString.A;
    View fingerPosition = null;

    float stringTiltRange = 30f;

    String[] gNotes = {"G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#"};
    String[] dNotes = {"D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#"};
    String[] aNotes = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
    String[] eNotes = {"E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#"};

    String[] currentStringNotes = aNotes;

    public void updateDeltaRoll(float deltaRoll) {
        this.deltaRoll = deltaRoll;
    }

    public void updateViolinString() {

        if (deltaRoll <= -stringTiltRange && currentViolinString != ViolinString.E) {
            currentViolinString = ViolinString.E;
            currentStringNotes = eNotes;
        } else if (deltaRoll > -stringTiltRange && deltaRoll <= 0 && currentViolinString != ViolinString.A) {
            currentViolinString = ViolinString.A;
            currentStringNotes = aNotes;
        } else if (deltaRoll > 0 && deltaRoll <= stringTiltRange && currentViolinString != ViolinString.D) {
            currentViolinString = ViolinString.D;
            currentStringNotes = dNotes;
        } else if (deltaRoll > stringTiltRange && currentViolinString != ViolinString.G) {
            currentViolinString = ViolinString.G;
            currentStringNotes = gNotes;
        }
    }

    public void setStringTiltRange(float str) {
        stringTiltRange = str;
    }

    public void updateFingerPosition(View v) {
        fingerPosition = v;
    }

    public void updateTiltIndicator(SeekBar tiltIndicator) {

        if (deltaRoll >= 0 && tiltIndicator.getScaleX() == 1) tiltIndicator.setScaleX(-1);
        else if (deltaRoll < 0 && tiltIndicator.getScaleX() == -1) tiltIndicator.setScaleX(1);

        int progress = (int) ((Math.abs(deltaRoll) % stringTiltRange) / stringTiltRange * 100);

        if (Math.abs(deltaRoll) >= 2 * stringTiltRange) progress = 100;

        tiltIndicator.setProgress(progress);
    }

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

    public void setNoteMap (HashMap<Integer, Integer> noteMap) {
        this.noteMap = noteMap;
    }
}
