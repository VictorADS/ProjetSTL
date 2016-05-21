package com.mygdx.game.Gtab3.tablature.ChordsFinder;
public class Note {
	/**
	 * La hauteur de la note, exprimée avec la notation MIDI
	 */
	private int hauteur;
	/**
	 * Tableau pour faire la correspondance MIDI <-> Notation Anglaise
	 */
	private static String[] notationAnglaise = {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
	public Note(int hauteur) {
		super();
		this.hauteur = hauteur;
	}
	public Note(String noteAnglaise){
		this(notationVersMIDI(noteAnglaise));
	}
	public int getHauteur() {
		return hauteur;
	}
	@Override
	public boolean equals(Object o) {
		return (o instanceof Note) &&
				((Note) o).getHauteur() == hauteur;
	}
	/** Renvoie l'écart (exprimé en semi-tons) entre n1 et n2
	 * (Les deux notes sont ramenée a la même octave)
	 */
	public int ecart(Note autre){
		return (12 + (autre.getHauteur() - hauteur)% 12) % 12;
	}
	/**
	 * Note en notation anglaise -> MIDI
	 * Note de la forme A4, C#3 etc.
	 * @param notation
	 * @return La note correspondante écrite en notation anglaise
	 */
	public static int notationVersMIDI(String notation){
		System.out.println("NOTE : " + notation.substring(0, notation.length() - 1));
		for(int i = 0; i < 12; ++i){
			if(notationAnglaise[i].equals(notation.substring(0, notation.length() - 1))){
				return i + 12 * (notation.charAt(notation.length()-1));
			}
		}
		System.err.println("notationVersMIDI -> note inconnue");
		return -1;
	}
	/**
	 * MIDI -> Notation anglaise
	 * @param hauteur
	 * @return La notation anglaise de la note donnée en MIDI
	 */
	public static String MIDIVersNotation(int hauteur){
		return notationAnglaise[hauteur % 12];
	}
	@Override
	public String toString() {
		//TODO Ne marche pas vraiment (l'octave en dessous de C4 est aussi 4..)
		return MIDIVersNotation(hauteur) + "" + (4 + (hauteur - 60) / 12);
	}
}