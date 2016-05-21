package com.mygdx.game.NoteManager;

import java.util.ArrayList;

/**
 * Created by afuri on 30/01/2016.
 */
public class FrequencyFilter {

    private ArrayList<Double> array;
    private int MAXSIZE;

    public FrequencyFilter(int n){
        this.array=new ArrayList<Double>(n);
        this.MAXSIZE=n;
    }
    public int getSize(){
        return array.size();
    }
    public void addMidi(Double midi){
        if(array.size()<MAXSIZE)
            array.add(midi);
        else{
            array.remove(0);
            array.add(midi);
        }
    }
    public double getVal(int index){
        return array.get(index);
    }
    public double getMoyenne(){
        double moyenne=0;
        int size=0;
        for (Double n : array) {
            if(n!=-1.0) {
                moyenne += n;
                size++;
            }
        }
        if(size>0)
            moyenne=moyenne/size;
        else
            moyenne=-1;

        return moyenne;
    }

    public String toString() {
        String ret = "[";
        for (int i = 0; i<array.size(); i++) {
            ret += " "+array.get(i)+",";
        }
        return ret +" ]";
    }

    public void reset(){
        array.clear();
        for(int i=0; i<MAXSIZE; i++)
            array.add(-1.0);
    }
}
