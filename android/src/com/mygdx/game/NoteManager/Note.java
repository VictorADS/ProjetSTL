package com.mygdx.game.NoteManager;

/**
 * Created by afuri on 30/01/2016.
 */
public class Note {
    private float freq;
    public Note(Float f){
        this.freq=f;
}
   public double getMidi(){
       if(freq!=-1) {
           double log2 = log2(freq / 440);
           return 69 + 12 * log2;
       }else{
           return -1.0;
       }
}
	public double log2(double f)
    {
        return Math.log10(f)/Math.log10(2);
    }
    public float getFreq() {
        return freq;
    }
}
