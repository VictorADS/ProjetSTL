package com.mygdx.game.Gtab3.tablature.tablature;

import com.mygdx.game.Gtab3.tablature.exceptions.InvalidPosException;


public class Position{
	public static final int MINCORDE = 1;
	public static final int MAXCORDE = 6;
	public static final int MINCASE = 0;
	public static final int MAXCASE = 12;
	
	private int numCorde;
	private int numCase;
	
	public Position(int nCde, int nCse) throws InvalidPosException{
		if(checkPos(nCde, nCse)){
			this.numCorde = nCde;
			this.numCase = nCse;
		}else{
			throw new InvalidPosException("Position invalide");
		}
	}
	
	public int getNumCorde(){
		return numCorde;
	}
	
	public void setNumCorde(int n){
		this.numCorde = n;
	}
	
	public void setNumCase(int n){
		this.numCase = n;
	}
	
	public int getNumCase(){
		return numCase;
	}
	
	private boolean checkPos(int cde, int cse){
		return ((cde>=MINCORDE && cde<=MAXCORDE) && (cse>=MINCASE && cse<=MAXCASE)
				|| (cde == -1 || cse == -1));
	}

    public String toString(){
        return "("+numCorde+","+numCase+")";
    }

}
