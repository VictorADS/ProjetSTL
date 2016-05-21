package com.mygdx.game.NoteManager;

import android.util.Log;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionResult;

/**
 * Created by ppti on 20/04/2016.
 */
public class Tuner {
    private FrequencyFilter filter = null;
    private double midiValues[] = new double[] {40.0,45.0,50.0,55.0,59.0,64.0};
    private double midimoyenne;
    private int currentFreqref;
    private double  seuilBruit = -70.0;
    private int filterSize = 15;

    public Tuner(){
        midimoyenne=-1;
        currentFreqref=-1;
        filter=new FrequencyFilter(filterSize);
    }
    public double getMidimoyenne(){
        return midimoyenne;
    }
    public int getCurrentFreqref(){
        return currentFreqref;
    }
    public void updateValues(PitchDetectionResult result, AudioEvent e){
        Note n = null;
        float pitch;
        if (e.getdBSPL() > seuilBruit && result.getProbability()>0.88 && result.getPitch()<700 && result.getPitch()>60)
            pitch = result.getPitch();
        else
            pitch = new Float(-1.0);
        n = new Note(pitch);

        midimoyenne = detectNote(n);
        if (pitch!=-1)
            Log.d("TestValue",""+midimoyenne+" et "+n.getFreq()+" et "+filter.toString());

    }
    public double getUpperHarmoniqueIfIsOne(double midivalue){
        midivalue+=12;
        for(int i=0;i<midiValues.length;i++){
            double toCompare=midiValues[i];
            if(midivalue<toCompare+0.5 && midivalue>toCompare-0.5){
                Log.d("upper",""+midivalue);
                return midivalue;
            }
        }
        midivalue-=12;

        return midivalue;
    }
    public double getLowerHarmoniqueIfIsOne(double midivalue){
        midivalue-=12;
        for(int i=0;i<midiValues.length;i++){
            double toCompare=midiValues[i];
            if(midivalue<toCompare+0.5 && midivalue>toCompare-0.5){
                Log.d("lower",""+midivalue);
                return midivalue;
            }
        }
        midivalue+=12;
        return midivalue;
    }
    public double detectNote(Note note) {
        double midiValue=note.getMidi();

        if (midiValue == -1.0) {		//Si silence prolonge alors reset
            if (isSilence()) {
                currentFreqref = -1;
                filter.reset();
            }
        }
        if(midiValue!=-1.0){
            midiValue=getLowerHarmoniqueIfIsOne(midiValue);
            midiValue=getUpperHarmoniqueIfIsOne(midiValue);

        }

        filter.addMidi(midiValue);
        midiValue=filter.getMoyenne(); //On affiche la valeur de la moyenne
        updateFreqref(midiValue);
        return midiValue;
    }
    public void updateFreqref(Double midiValue){
        currentFreqref=-1;
        for(int i = 0; i<midiValues.length ; i++) {
            if (Math.abs(midiValue - midiValues[i]) < 0.5) { // < 0.5 OK
                currentFreqref = i;
            }
        }
    }
    public boolean isSilence(){
        int size=filter.getSize();
        boolean answer=true;
        if(size>=4){
            for(int i=size-1;i>size-8;i--){
                if(filter.getVal(i)!=-1.0)
                    answer=false;
            }
        }
        return answer;
    }
}
