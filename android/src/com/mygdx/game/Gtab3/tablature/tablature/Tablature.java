package com.mygdx.game.Gtab3.tablature.tablature;

import java.util.ArrayList;


public class Tablature{
	private ArrayList<Position> positions;
	
	public Tablature() {
		this(new ArrayList<Position>());
	}
	
	public Tablature(ArrayList<Position> pos){
		this.positions = pos;
	}
	
	public void setPos(ArrayList<Position> pos){
		this.positions = pos;
	}
	
	public void addPos(Position p){
		positions.add(p);
	}
	
	public Position getPosition(int i){
		return positions.get(i);
	}
	
	public int getNbPos(){
		return positions.size();
	}
	
}
