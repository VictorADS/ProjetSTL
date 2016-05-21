package com.mygdx.game.Gtab3.tablature.generation;


public class Note {
	private String value;
	private double debut;
	private double duree;
	
	public Note(String value,double debut,double duree){
		
		this.value=value;
		this.debut=debut;
		this.duree=duree;
	}

	public String getValue(){
		return this.value;
	}
	
	public double getDebut(){
		return this.debut;
	}
	
	public double getDuree(){
		return this.duree;
	}
	
	public void setValue(String value){
		this.value=value;
	}
	
	public void setDebut(double debut){
		this.debut=debut;
	}
	
	public void setDuree(double duree){
		this.duree=duree;
	}
}
