package com.mygdx.game.Gtab3.tablature.ChordsFinder;
public class Accord {
	private Note tonique, tierce, quinte;
	public Accord(Note tonique, Note tierce, Note quinte) {
		super();
		this.tonique = tonique;
		this.tierce = tierce;
		this.quinte = quinte;
	}
	public Note getTonique() {
		return tonique;
	}
	public Note getTierce() {
		return tierce;
	}
	public Note getQuinte() {
		return quinte;
	}
	public void setTonique(Note tonique) {
		this.tonique = tonique;
	}
	public void setTierce(Note tierce) {
		this.tierce = tierce;
	}
	public void setQuinte(Note quinte) {
		this.quinte = quinte;
	}
	@Override
	public String toString() {
		if (tierce == null) {
			return Note.MIDIVersNotation(tonique.getHauteur()) + "5";
		} else {
			if (tonique != null){
				if( tonique.ecart(tierce) == 3) {
					return Note.MIDIVersNotation(tonique.getHauteur()) + "m";
				}
				else {
					return Note.MIDIVersNotation(tonique.getHauteur()) + "M";
				}
			} else {
				if(tierce.ecart(quinte) == 3){
					return Note.MIDIVersNotation(tierce.getHauteur()) + "m";
				}
				else{
					return Note.MIDIVersNotation(tierce.getHauteur()) + "M";
				}
			}
		}
	}
}