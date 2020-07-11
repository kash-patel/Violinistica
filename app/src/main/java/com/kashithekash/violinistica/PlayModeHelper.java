package com.kashithekash.violinistica;

import android.content.Context;
import android.media.SoundPool;

import java.util.HashMap;

public class PlayModeHelper {

    // Map of notes. SoundPool must access notes via noteMap, because this is where they have been
    // loaded
    HashMap<Integer, Integer> noteMap;

    public int getNote (int note) {
        return noteMap.get(note);
    }

    public void loadNotes(SoundPool soundPool, Context context) {

        noteMap = new HashMap<Integer, Integer>();

        noteMap.put(R.raw.g3, soundPool.load(context, R.raw.g3, 0));
        noteMap.put(R.raw.gsharp3, soundPool.load(context, R.raw.gsharp3, 0));
        noteMap.put(R.raw.a3, soundPool.load(context, R.raw.a3, 0));
        noteMap.put(R.raw.asharp3, soundPool.load(context, R.raw.asharp3, 0));
        noteMap.put(R.raw.b3, soundPool.load(context, R.raw.b3, 0));
        noteMap.put(R.raw.c4, soundPool.load(context, R.raw.c4, 0));
        noteMap.put(R.raw.csharp4, soundPool.load(context, R.raw.csharp4, 0));
        noteMap.put(R.raw.d4, soundPool.load(context, R.raw.d4, 0));
        noteMap.put(R.raw.dsharp4, soundPool.load(context, R.raw.dsharp4, 0));
        noteMap.put(R.raw.e4, soundPool.load(context, R.raw.e4, 0));
        noteMap.put(R.raw.f4, soundPool.load(context, R.raw.f4, 0));
        noteMap.put(R.raw.fsharp4, soundPool.load(context, R.raw.fsharp4, 0));
        noteMap.put(R.raw.g4, soundPool.load(context, R.raw.g4, 0));
        noteMap.put(R.raw.gsharp4, soundPool.load(context, R.raw.gsharp4, 0));
        noteMap.put(R.raw.a4, soundPool.load(context, R.raw.a4, 0));
        noteMap.put(R.raw.asharp4, soundPool.load(context, R.raw.asharp4, 0));
        noteMap.put(R.raw.b4, soundPool.load(context, R.raw.b4, 0));
        noteMap.put(R.raw.c5, soundPool.load(context, R.raw.c5, 0));
        noteMap.put(R.raw.csharp5, soundPool.load(context, R.raw.csharp5, 0));
        noteMap.put(R.raw.d5, soundPool.load(context, R.raw.d5, 0));
        noteMap.put(R.raw.dsharp5, soundPool.load(context, R.raw.dsharp5, 0));
        noteMap.put(R.raw.e5, soundPool.load(context, R.raw.e5, 0));
        noteMap.put(R.raw.f5, soundPool.load(context, R.raw.f5, 0));
        noteMap.put(R.raw.fsharp5, soundPool.load(context, R.raw.fsharp5, 0));
        noteMap.put(R.raw.g5, soundPool.load(context, R.raw.g5, 0));
        noteMap.put(R.raw.gsharp5, soundPool.load(context, R.raw.gsharp5, 0));
        noteMap.put(R.raw.a5, soundPool.load(context, R.raw.a5, 0));
        noteMap.put(R.raw.asharp5, soundPool.load(context, R.raw.asharp5, 0));
        noteMap.put(R.raw.b5, soundPool.load(context, R.raw.b5, 0));
        noteMap.put(R.raw.c6, soundPool.load(context, R.raw.c6, 0));
        noteMap.put(R.raw.csharp6, soundPool.load(context, R.raw.csharp6, 0));
        noteMap.put(R.raw.d6, soundPool.load(context, R.raw.d6, 0));
        noteMap.put(R.raw.dsharp6, soundPool.load(context, R.raw.dsharp6, 0));
        noteMap.put(R.raw.e6, soundPool.load(context, R.raw.e6, 0));
        noteMap.put(R.raw.f6, soundPool.load(context, R.raw.f6, 0));
        noteMap.put(R.raw.fsharp6, soundPool.load(context, R.raw.fsharp6, 0));
        noteMap.put(R.raw.g6, soundPool.load(context, R.raw.g6, 0));
    }
}
