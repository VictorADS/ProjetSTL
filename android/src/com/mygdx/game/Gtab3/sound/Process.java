/**
 * @authors Henri NG && Jason CHUMMUN
 * @version 1.5
 * 
 * Cette classe permet d'appliquer un algorithme de détection de pitch sur un
 * fichier son enregistré.
 */

package com.mygdx.game.Gtab3.sound;

import com.mygdx.game.Gtab3.Yin2.Yin2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;


public class Process {
	
	/**
	 * Convertit un tableau de bytes en un tableau de float
	 * 
	 * Basé sur "AudioFloatConverter.java" du package "com.sun.media.sound"
	 * d'Oracle
	 * 
	 * @param in_buff
	 * @param in_offset
	 * @param out_buff
	 * @param out_offset
	 * @param out_len
	 */
	public void toFloatArray(byte[] in_buff, int in_offset, float[] out_buff,
			int out_offset, int out_len) {
		int ix = in_offset;
		int len = out_offset + out_len;
		for (int ox = out_offset; ox < len; ox++) {
			out_buff[ox] = ((short) ((in_buff[ix++] & 0xFF) | (in_buff[ix++] << 8)))
					* (1.0f / 32767.0f);
		}
	}

	/**
	 * Applique l'algorithme de détection de pitch sur un fichier son au format
	 * wave et écrit dans un fichier de sortie au format texte le résultat
	 * 
	 * @param fileIn
	 * @param fileOut
	 * @throws IOException
	 */
	public void process(String fileIn, String fileOut) throws IOException {

		int bufferStepSize;   // Nombre de buffers lus
		int bytesRead;        // Nombre de flottants lus
		int frameSize;        // Taille d'un échantillon
		int note;             // Note au format MIDI
		int sampleRate;       // Taux d'échantillonage
		long bytesProcessed;  // Nombre de flottants traités
		long frameRate;       // Nombre d'échantillons
		float pitch;          // Fréquence fondamentale de l'échantillon
		double audioLength;   // Longueur de l'échantillon en seconde
		double time;          // Temps en seconde d'une échantillon
		boolean noteChanged;  // Changement de note détectée
		byte[] buffer;        // Buffer de bytes en entrée
		NoteDetection nd;     // Détecteur de note

		// Ouverture et lecture du fichier d'entrée
		File fi = new File(fileIn);
		RandomAccessFile reader = new RandomAccessFile(fi, "rw");

		// Récupération des données du header
		reader.seek(24);
		sampleRate = Integer.reverseBytes(reader.readInt());
		reader.seek(16);
		frameSize = Integer.reverseBytes(reader.readInt());
		reader.seek(40);
		frameRate = Integer.reverseBytes(reader.readInt());

		// Le nombre de secondes est donné par la formule suivante :
		// frameRate / (sampleRate * frameSize / 8).
		audioLength = (float) (frameRate / (sampleRate * frameSize / 8));

		/*System.out.println("samplerate: " + sampleRate + "\n" + "framesize: "
				+ frameSize + "\n" + "framerate: " + frameRate + "\n"
				+ "length : " + audioLength);*/

		// Création de l'instance de Yin
		Yin2 yin2 = Yin2.getInstance(sampleRate,2048);

		// Initialisation des variables
		bufferStepSize = yin2.getBufferSize() - yin2.getOverlapSize();
		bytesRead = 0;
		note = 0;
		bytesProcessed = 0;
		pitch = 0;
		time = 0;
		noteChanged = false;
		
		// Création du tableau de bytes ou du buffer d'entrée
		buffer = new byte[yin2.getBufferSize() * 4];
		nd = new NoteDetection();

		// Ouverture et écriture du fichier de sortie
		FileWriter fwo = new FileWriter(fileOut);
		BufferedWriter out = new BufferedWriter(fwo);

		// Positionnement sur le champs "Data" (après le header)
		reader.seek(44);

		// Lecture du buffer de bytes en entrée
		bytesRead = reader.read(buffer, 0, yin2.getBufferSize() * 4);

		// Incrément du nombre de données lues dans le fichier d'entrée
		bytesProcessed += bytesRead;

		// Conversion du tableau de bytes en tableau de floats
		toFloatArray(buffer, 0, yin2.getInputBuffer(), 0, yin2.getBufferSize());

		// Début de lecture et écriture des buffers
		while (bytesRead != -1) {
			
			// Algorithme de Yin
			pitch = yin2.getPitch(null).getPitch();

			// Calcul du temps approximatif de la capture de la note
			//time = bytesProcessed / (float) (sampleRate * frameSize / 8);
			time = bytesProcessed * audioLength / frameRate;
			
			// Conversion de la fréquence détectée en la note au format MIDI
			note = nd.frequencyToNote(pitch);

			// Incrément de la durée de la note détectée
			nd.incrementDuration(time);

			// Détection du changement de note
			noteChanged = nd.changeNoteDetection(note, time);
			if (noteChanged) {
				if (note == -1) {
					// Écriture du fichier de sortie
					out.write(nd.noteAToString(nd.getLastNote()) + "/"
						+ nd.getLastTime() + "/" 
						+ nd.getDuration());
					out.newLine();
					/*System.out.println("Note = " + nd.getLastNote() + "\t"
							+ nd.noteSToString(nd.getLastNote()) + "\t"
							+ "Début = " + nd.getLastTime() + "\t" + "Durée = "
							+ nd.getDuration());*/
					noteChanged = false;
				}
			}

			/*System.out.println("Données lus  = " + bytesProcessed + "\t"
					+ "Temps = " + time + "\t" + "Pitch = " + pitch + "\t"
					+ "Note = " + note + " / " + nd.noteAToString(note) + " / "
					+ nd.noteSToString(note) + "\t");*/

			// Translate le buffer d'entrée avec l'overlapSize prédéfini dans
			// la classe Yin
			for (int i = 0; i < bufferStepSize; i++)
				yin2.getInputBuffer()[i] = yin2.getInputBuffer()[i
						+ yin2.getOverlapSize()];

			// Lecture de bufferStepSize données dans le InputBuffer
			bytesRead = reader.read(buffer, 0, yin2.getOverlapSize() * 4);

			toFloatArray(buffer, 0, yin2.getInputBuffer(), yin2.getOverlapSize(),
					bufferStepSize);

			// Incrément du nombre de données lues dans le fichier d'entrée
			bytesProcessed += bytesRead;

		}

		// Écriture de la dernière note détectée dans le fichier de sortie
		/*out.write(nd.noteAToString(nd.getFirstNote()) + "/"
			+ nd.getFirstTime() + "/"
			+ nd.getDuration());*/

		// Fermeture des fichiers
		reader.close();
		out.close();

	}

}
