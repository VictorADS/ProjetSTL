/**
 * @authors Henri NG && Jason CHUMMUN
 * @version 1.5
 * 
 * Cette classe implémente l'algorithme de YIN.
 * 
 * L'algorithme de YIN a été développé par Alain de Cheveigné et Hideki Kawahara.
 * 
 * Il a été publié dans : 
 * de Cheveigné, A., Kawahara, H. (2002) "YIN, a fundamental frequency 
 * estimator for speech and music", J. Acoust. Soc. Am. 111, 1917-1930.
 * 
 * Référence :
 * http://recherche.ircam.fr/equipes/pcm/pub/people/cheveign.html
 */

package com.mygdx.game.Gtab3.sound;

public class Yin {

	private final double threshold = 0.15; // Valeur du seuil de YIN
	private final int bufferSize;          // Taille des tampons
	private final int overlapSize;         // Taille des chevauchements
	private final float sampleRate;        // Taux d'échantillon
	private final float[] inputBuffer;     // Tampon d'entrée
	private final float[] yinBuffer;       // Tampon de sortie, stockant les
										   // valeurs calculées par l'algorithme

	public static Yin getInstance(float sampleRate) {
		return new Yin(sampleRate);
	}
	
	private Yin(float sampleRate) {
		this.sampleRate = sampleRate;
		bufferSize = 2048;
		overlapSize = bufferSize / 2;
        inputBuffer = new float[bufferSize];
		yinBuffer = new float[bufferSize / 2];
	}

	public double getThreshold() {
		return threshold;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public int getOverlapSize() {
		return overlapSize;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public float[] getInputBuffer() {
		return inputBuffer;
	}

	public float[] getYinBuffer() {
		return yinBuffer;
	}

	private void autocorrelation() {}

	/**
	 * Étape 2 de l'algorithme de YIN
	 * 
	 * Implémente la fonction de différence
	 */
	private void difference() {
		int j, tau;
		float tmp;
		int size = yinBuffer.length;
		for (tau = 0; tau < size; tau++) {
			yinBuffer[tau] = 0;
		}
		for (tau = 1; tau < size; tau++) {
			for (j = 0; j < size; j++) {
				tmp = inputBuffer[j] - inputBuffer[j + tau];
				yinBuffer[tau] += tmp * tmp;
			}
		}
	}

	/**
	 * Étape 3 de l'algorithme de YIN
	 * 
	 * Implémente la fonction de normalisation de la différence
	 */
	private void cumulativeMeanNormalizedDifference() {
		int tau;
		yinBuffer[0] = 1;
        yinBuffer[1]=1;
		float som = 1;
		for (tau = 2; tau < yinBuffer.length; tau++) {
			som += yinBuffer[tau];
			yinBuffer[tau] *= tau / som;
		}
	}

	/**
	 * Étape 4 de l'algorithme de YIN
	 * 
	 * @return -1 si aucun pitch n'a été détecté
	 */
	private int absoluteThreshold() {
		for (int tau = 1; tau < yinBuffer.length; tau++) {
			if (yinBuffer[tau] < threshold) {
				while (tau + 1 < yinBuffer.length
						&& yinBuffer[tau + 1] < yinBuffer[tau])
					tau++;
				return tau;
			}
		}
		return -1;
	}

	/**
	 * Étape 5 de l'algorithme de YIN
	 * 
	 * @param tauEstimate la valeur estimé de tau
	 * 
	 * @return une meilleure valeur de tau plus précise
	 */
	private float parabolicInterpolation(int tauEstimate) {
		float s0, s1, s2;
		int x0, x2;
		x0 = (tauEstimate < 1) ? tauEstimate : tauEstimate - 1;
		x2 = (tauEstimate + 1 < yinBuffer.length) ? tauEstimate + 1
				: tauEstimate;
		if (x0 == tauEstimate)
			return (yinBuffer[tauEstimate] <= yinBuffer[x2]) ? tauEstimate : x2;
		if (x2 == tauEstimate)
			return (yinBuffer[tauEstimate] <= yinBuffer[x0]) ? tauEstimate : x0;
		s0 = yinBuffer[x0];
		s1 = yinBuffer[tauEstimate];
		s2 = yinBuffer[x2];
		return tauEstimate + 0.5f * (s2 - s0) / (2.0f * s1 - s2 - s0);
	}

	private void bestLocalEstimate() {}

	/**
	 * Fonction principale de l'algorithme de Yin
	 * 
	 * @return une valeur du pitch en Hertz ou -1 si aucun pitch détecté
	 */
	public float getPitch() {
		int tauEstimate = -1;
		float pitchInHertz = -1;
		// Étape 1
		autocorrelation();
		// Étape 2
		difference();
		// Étape 3
		cumulativeMeanNormalizedDifference();
		// Étape 4
		tauEstimate = absoluteThreshold();
		// Étape 5
		if (tauEstimate != -1) {
			float betterTau = parabolicInterpolation(tauEstimate);
			// Étape 6
			bestLocalEstimate();
			// Conversion en Hz
			pitchInHertz = sampleRate / betterTau;
		}
		return pitchInHertz;
	}

}
