package com.kashithekash.violinistica;

import android.view.View;

import java.util.HashMap;

public class PlayModeHelper {

    // Map of notes. SoundPool must access notes via noteMap, because this is where they have been
    // loaded
    HashMap<Integer, Integer> noteMap;

    ViolinString currentViolinString = ViolinString.A;
    View fingerPosition = null;

    public void updateViolinString(float deltaRoll) {

        if (deltaRoll <= -30)
            currentViolinString = ViolinString.G;
        else if (deltaRoll > -30 && deltaRoll <= 0)
            currentViolinString = ViolinString.D;
        else if (deltaRoll > 0 && deltaRoll <= 30)
            currentViolinString = ViolinString.A;
        else
            currentViolinString = ViolinString.E;
    }

    public void updateFingerPosition(View v) {
        fingerPosition = v;
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
                    case R.id.noteButton13:
                        note = noteMap.get(R.raw.gsharp4);
                        break;
                    case R.id.noteButton14:
                        note = noteMap.get(R.raw.a4);
                        break;
                    case R.id.noteButton15:
                        note = noteMap.get(R.raw.asharp4);
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
                    case R.id.noteButton13:
                        note = noteMap.get(R.raw.dsharp5);
                        break;
                    case R.id.noteButton14:
                        note = noteMap.get(R.raw.e5);
                        break;
                    case R.id.noteButton15:
                        note = noteMap.get(R.raw.f5);
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
                    case R.id.noteButton13:
                        note = noteMap.get(R.raw.asharp5);
                        break;
                    case R.id.noteButton14:
                        note = noteMap.get(R.raw.b5);
                        break;
                    case R.id.noteButton15:
                        note = noteMap.get(R.raw.c6);
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
                    case R.id.noteButton13:
                        note = noteMap.get(R.raw.f6);
                        break;
                    case R.id.noteButton14:
                        note = noteMap.get(R.raw.fsharp6);
                        break;
                    case R.id.noteButton15:
                        note = noteMap.get(R.raw.g6);
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

    public void setNoteMap (HashMap<Integer, Integer> noteMap) {
        this.noteMap = noteMap;
    }
}
