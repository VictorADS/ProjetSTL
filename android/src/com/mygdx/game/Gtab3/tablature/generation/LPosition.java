package com.mygdx.game.Gtab3.tablature.generation;

import java.util.ArrayList;

import com.mygdx.game.Gtab3.tablature.tablature.Position;


public class LPosition {
	ArrayList<Position> positions;
	
	public LPosition(){
		this.positions = new ArrayList<Position>();
	}
	
	
	public void add(Position p){
		positions.add(p);
	}
	
	public Position getPos(int i){
		return positions.get(i);
	}

	public int getNbPos(){
		return positions.size();
	}

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("LISTE POSITION : ");
        for(Position p : positions){
            sb.append(p + " ");
        }
        return sb.toString();
    }
}
