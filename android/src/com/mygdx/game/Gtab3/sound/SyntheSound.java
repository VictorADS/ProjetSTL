/**
 * @authors Henri NG && Jason CHUMMUN
 * @version 1.5
 * 
 * Cette classe permet de synthétiser un son à partir d'une fréquence.
 */

package com.mygdx.game.Gtab3.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class SyntheSound {

	private int numSamples;      // Nombre d'échantillons
	private int sampleRate;      // Taux d'échantillonnage
	private double duration;     // Durée de la note en seconde
	private double freqOfTone;   // Fréquence de la note en Hz
	
	private static double sample[] = new double[44100];   // Échantillon entier
	private static byte generatedSnd[] = new byte[88200]; // Son synthétisé

	public SyntheSound(double freq, double dur) {
		duration = dur;
		freqOfTone = freq;
		sampleRate = 44100;
		numSamples = (int) Math.ceil(duration * sampleRate);
		// sample = new double[numSamples];
		// generatedSnd = new byte[2 * numSamples];
	}

	public void genTone() {
		// Remplissage du tableau d'échantillons
		for (int i = 0; i < numSamples; i++)
			sample[i] = Math.sin(freqOfTone * 2 * Math.PI * i / (sampleRate));
		// Conversion vers un tableau PCM de son de 16 bits
		// On suppose que le tableau est normalisé.
		int idx = 0;
		int i = 0;
		int j = 0;
		int ramp = numSamples / 20; 
		// Augmente l'amplitude avec un certain pourcentage d'échantillons compté
		for (i = 0; i < ramp; ++i) {
			// Augmente l'amplitude (pour adoucir les transitions de son)
			double dVal = sample[i];
			// Augmente au maximum
			final short val = (short) ((dVal * 32767 * i / ramp));
			// Dans un PCM de 16 bits au format wave, 
			// le premier octet est le plus petit octet.
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
		j = i;
		for (i = j; i < numSamples - ramp; ++i) {
			// Amplitude maximale pour la plupart des échantillons
			double dVal = sample[i];
			// Mis à l'amplitude maximale
			final short val = (short) ((dVal * 32767));
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
		j = i;
		for (i = j; i < numSamples; ++i) {
			// Diminue l'amplitude
			double dVal = sample[i];
			// Diminue l'amplitude à 0
			final short val = (short) ((dVal * 32767 * (numSamples - i) / ramp));
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
	}

	public void playSound() {
		AudioTrack audio = null;
		try {
			audio = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
					AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT, numSamples * 2,
					AudioTrack.MODE_STATIC);
		} catch (Exception e) {
			Log.e(SyntheSound.class.getName(), "Échec de l'initialisation d'AudioTrack");
		}
		try {
			audio.write(generatedSnd, 0, generatedSnd.length);
		} catch (Exception e) {
			Log.e(SyntheSound.class.getName(), "Échec du chargement d'AudioTrack");
		}
		try {
			audio.play();
		} catch (Exception e) {
			Log.e(SyntheSound.class.getName(), "Échec de la lecture d'AudioTrack");
		}
		int x = 0;
		do { 
			// Contrôle la lecture pour détecter la fin
			if (audio != null)
				x = audio.getPlaybackHeadPosition();
			else
				x = numSamples;
		} while (x < numSamples);
		if (audio != null)
			audio.release();
	}

}
