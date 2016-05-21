/**
 * @authors Henri NG && Jason CHUMMUN
 * @version 1.5
 * 
 * Cette classe permet de convertir une fréquence en note et inversement.
 * Elle gère aussi la détection de la durée des notes.
 */

package com.mygdx.game.Gtab3.sound;


public class NoteDetection {
	
	// Tableau de notes en écriture européenne
	private static String[] NoteS = { "DO", "DO#", "RE", "RE#", "MI", "FA",
			"FA#", "SOL", "SOL#", "La", "La#", "Si" };
	
	// Tableau de notes en écriture américaine
	private static String[] NoteA = { "C", "C#", "D", "D#", "E", "F", "F#",
			"G", "G#", "A", "A#", "B" };
	
	// Tableau des fréquences de notes au format MIDI (LA4 => 440 Hz => 69)
	private static float[] notefrequencies;

	// Détection de la première occurence d'une note au format MIDI
	private static int firstNote = -1;

	// Sauvegarde de l'ancienne note au format MIDI
	private static int lastNote = -1;
	
	// Temps de l'apparition de la première occurence d'une note en seconde
	private static double firstTime = 0.0;
	
	// Sauvegarde du temps de l'apparition de l'ancienne note en seconde
	private static double lastTime = 0.0;
	
	// Durée de la note détectée en seconde
	private static double duration = 0.0;
	
	public int getFirstNote() {
		return firstNote;
	}

	public double getFirstTime() {
		return firstTime;
	}
	
	public int getLastNote() {
		return lastNote;
	}
	
	public double getLastTime() {
		return lastTime;
	}

	public double getDuration() {
		return duration;
	}

	/**
	 * Constructeur initialisant le tableau des fréquences de notes
	 */
	public NoteDetection() {
		initNoteFrequencies();
	}

	/**
	 * Initialise le tableau des fréquences de notes
	 */
	public void initNoteFrequencies() {
		notefrequencies = new float[128];
		for (int i = 0; i < notefrequencies.length; i++) {
			notefrequencies[i] = noteToFrequency(i);
			// System.out.println(i + "\t" + notefrequencies[i]);
		}
	}

	/**
	 * Corrige la note détectée au cas où l'écart d'erreur de la fréquence 
	 * détectée avec la fréquence originale de la note est trop grande
	 * 
	 * @param frequency
	 * @param note
	 * @return note corrigée
	 */
	public int frequencyCorrection(float frequency, int note) {
		float error;
		if (frequency == -1.0)
			return -1;
		error = Math.abs(notefrequencies[note] - frequency);
		if (error <= Math.abs(notefrequencies[note - 1] - frequency))
			if (error <= Math.abs(notefrequencies[note + 1] - frequency))
				return note;
			else
				return note + 1;
		else
			return note;
	}

	/**
	 * Convertit une note au format MIDI vers la fréquence correspondante
	 * Permet de construire le tableau de fréquences de notes
	 * 
	 * @param i
	 * @return fréquence
	 */
	public float noteToFrequency(int i) {
		return (float) (Math.exp((float) (i - 69) / 12 * Math.log(2)) * 440);
	}
	
	/**
	 * Convertit une note en écriture américaine vers la fréquence
	 * correspondante
	 * 
	 * @param s
	 * @return fréquence
	 */
	public float noteToFrequency(String s) {
		String note;
		int indice = 0;
		int octave = 0;
		note = s.substring(0, s.length() - 1);
		octave = Character.digit(s.charAt(s.length() - 1), 10);
		for (int i = 0; i < NoteA.length; i++)
			if (note.equals(NoteA[i])) {
				indice = i;
				break;
			}
		return noteToFrequency((octave + 1) * 12 + indice);
	}

	/**
	 * Convertit une fréquence vers la note correspondante
	 * 
	 * @param frequency
	 * @return note
	 */
	public int frequencyToNote(float frequency) {
		int note = (int) (69 + 12 * Math.log(frequency / 440)
				/ Math.log(2));
		if (note < 0 || note > 128)
			return -1;
		return frequencyCorrection(frequency, note);
		// return (frequency != -1.0) ? (int) (69 + 12 * Math.log(frequency / 440)
		//		/ Math.log(2)) : -1;
	}
	
	/**
	 * Convertit une note du format MIDI en écriture européenne
	 * 
	 * @param note
	 * @return string
	 */
	public String noteSToString(int note) {
		int octave = note / 12 - 1;
		return (note != -1) ? NoteS[note % 12] + octave : "-1";
	}

	/**
	 * Convertit une note du format MIDI en écriture américaine
	 * 
	 * @param note
	 * @return string
	 */
	public String noteAToString(int note) {
		int octave = note / 12 - 1;
		return (note != -1) ? NoteA[note % 12] + octave : "-1";
	}
	
	/**
	 * Modifie la première occurence d'une note détectée et son temps d'apparition
	 * 
	 * @param note
	 * @param time
	 */
	public boolean changeNoteDetection(int note, double time) {
		if (note != firstNote) {
			lastNote = firstNote;
			lastTime = firstTime;
			firstNote = note;
			firstTime = time;
			return true;
		}
		return false;
	}
	
	/**
	 * Incrémente la durée de la note détectée
	 * @param time
	 */
	public void incrementDuration(double time) {
		duration = time - firstTime;
	}
	
}
