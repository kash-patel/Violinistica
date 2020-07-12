package com.kashithekash.violinistica;

import android.view.View;

import java.util.HashMap;

public class PlayModeHelper {

    // Map of notes. SoundPool must access notes via noteMap, because this is where they have been
    // loaded
    HashMap<Integer, Integer> noteMap;

    ViolinString currentViolinString;

    public ViolinString getCurrentViolinString(float deltaRoll) {

        if (deltaRoll <= -30)
            currentViolinString = ViolinString.G;
        else if (deltaRoll > -30 && deltaRoll <= -10)
            currentViolinString = ViolinString.D;
        else if (deltaRoll > -10 && deltaRoll <= 10)
            currentViolinString = ViolinString.A;
        else
            currentViolinString = ViolinString.E;

        return currentViolinString;
    }

    public int getNote(ViolinString currentViolinString, View v, HashMap<Integer, Integer> noteMap) {

        int note = 0;

        switch (currentViolinString) {
            case G:
                switch (v.getId()) {
                    case R.id.playOpenStringButton:
                        note = noteMap.get(R.raw.g3);
                        break;
                    case R.id.noteButton2:
                        note = noteMap.get(R.raw.a3);
                        break;
                    case R.id.noteButton4:
                        note = noteMap.get(R.raw.b3);
                        break;
                    case R.id.noteButton5:
                        note = R.raw.c4;
                        break;
                    case R.id.noteButton7:
                        note = R.raw.d4;
                        break;
                    default:
                        break;
                } break;
            case D:
                switch (v.getId()) {
                    case R.id.playOpenStringButton:
                        note = R.raw.d4;
                        break;
                    case R.id.noteButton2:
                        note = R.raw.e4;
                        break;
                    case R.id.noteButton4:
                        note = R.raw.fsharp4;
                        break;
                    case R.id.noteButton5:
                        note = R.raw.g4;
                        break;
                    case R.id.noteButton7:
                        note = R.raw.a4;
                        break;
                    default:
                        break;
                } break;
            case A:
                switch (v.getId()) {
                    case R.id.playOpenStringButton:
                        note = R.raw.a4;
                        break;
                    case R.id.noteButton2:
                        note = R.raw.b4;
                        break;
                    case R.id.noteButton4:
                        note = R.raw.csharp5;
                        break;
                    case R.id.noteButton5:
                        note = R.raw.d5;
                        break;
                    case R.id.noteButton7:
                        note = R.raw.e5;
                        break;
                    default:
                        break;
                } break;
            case E:
                switch (v.getId()) {
                    case R.id.playOpenStringButton:
                        note = R.raw.e5;
                        break;
                    case R.id.noteButton2:
                        note = R.raw.fsharp5;
                        break;
                    case R.id.noteButton4:
                        note = R.raw.gsharp5;
                        break;
                    case R.id.noteButton5:
                        note = R.raw.a5;
                        break;
                    case R.id.noteButton7:
                        note = R.raw.b5;
                        break;
                    default:
                        break;
                } break;
            default:
                break;
        }

        return note;
    }
}
