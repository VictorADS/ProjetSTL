/**
 * @authors Henri NG && Jason CHUMMUN
 * @version 1.5
 * 
 * Cette classe permet d'enregistrer un son à partir du micro d'un smartphone 
 * et de le convertir au format wave.
 * 
 * Les caractéristiques du fichier wave sont fixées :
 * - échantillonage : 44.1 kHz
 * - nombre de canaux : 1 (mono)
 * - nombre de bits : 16
 * 
 * Code basé sur "ExtAudioRecorder.java" sur le site de "denis blog"
 * 
 * Référence : http://i-liger.com/article/android-wav-audio-recording
 */

package com.mygdx.game.Gtab3.sound;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class AudioRecorder {

	public static AudioRecorder getInstance() {
		return new AudioRecorder(AudioSource.MIC, 44100,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
	}

	/**
	 * État d'enregistrement
	 * 
	 * INITIALIZING : Le recorder est en cours d'initialisation.
	 * READY : Le recorder a été initialisé et est prêt à enregistrer.
	 * RECORDING : Le recorder est en cours d'enregistrement.
	 * ERROR : Le recorder sort une erreur.
	 * STOPPED: Le recorder stope et se reset. 
	 */
	public enum State {
		INITIALIZING, READY, RECORDING, ERROR, STOPPED
	};

	// L'intervalle de temps où les échantillons sont enregistrés dans
	// le fichier de sortie
	private static final int TIMER_INTERVAL = 120;

	// Recorder utilisée
	private AudioRecord audioRecorder = null;

	// Sauvegarde de l'amplitude courante
	private int cAmplitude = 0;

	// Chemin du fichier de sortie
	private String filePath = null;

	// État d'enregistrement
	private State state;

	// Fichier d'écriture
	private RandomAccessFile randomAccessWriter;

	private int audioSource; // Source audio
	private int audioFormat; // Format audio
	private int bufferSize;	 // Taille du buffer
	private int sampleRate;	 // Taux d'échantillonnage
	private short frameSize; // Taille d'un échantillon (en bit)
	private short nChannels; // Nombre de canaux

	// Nombre de frames écrits dans le fichier à chaque sortie 
	private int framePeriod;

	// Buffer de sortie
	private byte[] buffer;

	// Nombre d'octets écrits dans le fichier après le header
	// Après que la méthode stop() soit appelée, la taille est écrite dans le
	// "header/data chunk" du fichier wave.
	private int payloadSize;

	/**
	 * Retourne l'état du recorder
	 * 
	 * @return l'état du recorder
	 */
	public State getState() {
		return state;
	}

	/**
	 * Méthode utilisée pour enregistrer les données dans le fichier wave
	 * 
	 */
	private AudioRecord.OnRecordPositionUpdateListener updateListener = 
			new AudioRecord.OnRecordPositionUpdateListener() {
		public void onPeriodicNotification(AudioRecord recorder) {
			audioRecorder.read(buffer, 0, buffer.length);
			try {
				randomAccessWriter.write(buffer);
				payloadSize += buffer.length;
				for (int i = 0; i < buffer.length / 2; i++) {
					short curSample = getShort(buffer[i * 2], buffer[i * 2 + 1]);
					if (curSample > cAmplitude)
						cAmplitude = curSample;
				}
			} catch (IOException e) {
				Log.e(AudioRecorder.class.getName(),
						"Problème dans updateListener(), l'enregistrement est stoppé");
				stop();
			}
		}

		public void onMarkerReached(AudioRecord recorder) {
			// Non utilisé
		}
	};

	/**
	 * Constructeur par défaut
	 * 
	 * Instancie un nouveau recorder
	 */
	public AudioRecorder(int audioSource, int sampleRate, int channelConfig,
			int audioFormat) {
		int minBufferSize;
		try {
			this.audioSource = audioSource;
			this.audioFormat = audioFormat;
			this.sampleRate = sampleRate;
			frameSize = 16;
			nChannels = 1;
			framePeriod = sampleRate * TIMER_INTERVAL / 1000;
			bufferSize = framePeriod * 2 * frameSize * nChannels / 8;
			minBufferSize = AudioRecord.getMinBufferSize(sampleRate,
					channelConfig, audioFormat);
			if (bufferSize < minBufferSize) {
				bufferSize = minBufferSize;
				framePeriod = bufferSize / (2 * frameSize * nChannels / 8);
				Log.w(AudioRecorder.class.getName(),
						"Augmentation du bufferSize à "
								+ Integer.toString(bufferSize));
			}
			audioRecorder = new AudioRecord(audioSource, sampleRate,
					channelConfig, audioFormat, bufferSize);
			if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED)
				throw new Exception("Échec de l'initialisation de AudioRecord");
			audioRecorder.setRecordPositionUpdateListener(updateListener);
			audioRecorder.setPositionNotificationPeriod(framePeriod);
			cAmplitude = 0;
			filePath = null;
			state = State.INITIALIZING;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				Log.e(AudioRecorder.class.getName(), e.getMessage());
			} else {
				Log.e(AudioRecorder.class.getName(),
						"Erreur inconnue lors de l'initalisation du recorder");
			}
			state = State.ERROR;
		}
	}

	/**
	 * Initialise le chemin du fichier de sortie
	 * Méthode appelée directement après la construction ou le reset du recorder
	 * 
	 * @param chemin du fichier de sortie
	 */
	public void setOutputFile(String argPath) {
		try {
			if (state == State.INITIALIZING)
				filePath = argPath;
		} catch (Exception e) {
			if (e.getMessage() != null)
				Log.e(AudioRecorder.class.getName(), e.getMessage());
			else
				Log.e(AudioRecorder.class.getName(),
						"Erreur inconnue lors de l'initialisation du chemin du fichier de sortie");
			state = State.ERROR;
		}
	}

	/**
	 * Retourne la plus grande amplitude échantillonée depuis le dernier appel à
	 * cette méthode
	 * 
	 * @return la plus grande amplitude échantillonée depuis le dernier appel, 
	 * ou 0 si non en état RECORDING
	 */
	public int getMaxAmplitude() {
		int result;
		if (state == State.RECORDING) {
			result = cAmplitude;
			cAmplitude = 0;
			return result;
		} else
			return 0;
	}

	/**
	 * Prépare le recorder pour l'enregistrement, dans le cas où le recorder
	 * n'est pas dans l'état INITIALIZING et que le chemin du fichier n'a pas
	 * été initialisé.
	 * Écrit le header du fichier wave ou renvoie l'état ERROR en cas d'exception
	 */
	public void prepare() {
		try {
			if (state == State.INITIALIZING) {
				if ((audioRecorder.getState() == AudioRecord.STATE_INITIALIZED)
						& (filePath != null)) {
					// Écriture du header
					randomAccessWriter = new RandomAccessFile(filePath, "rw");
					randomAccessWriter.setLength(0);
					// La longueur du fichier est mis à 0, pour éviter 
					// un comportement inattendu dans le cas où le fichier existait déjà
					randomAccessWriter.writeBytes("RIFF");
					randomAccessWriter.writeInt(0);
					// Taille du fichier final encore inconnu, mis à 0 ici
					randomAccessWriter.writeBytes("WAVE");
					randomAccessWriter.writeBytes("fmt ");
					randomAccessWriter.writeInt(Integer.reverseBytes(16));
					// Taille d'un échantillon : 16 pour le PCM
					randomAccessWriter
							.writeShort(Short.reverseBytes((short) 1));
					// AudioFormat : 1 pour le PCM
					randomAccessWriter
							.writeShort(Short.reverseBytes(nChannels));
					// Nombre de canaux : mis à 1 ici pour du mono
					randomAccessWriter.writeInt(Integer.reverseBytes(sampleRate));
					// Taux d'échantillonage
					randomAccessWriter.writeInt(Integer.reverseBytes(sampleRate
							* frameSize * nChannels / 8));
					// Byte rate, SampleRate * NbChannel * BitsPerSample / 8
					randomAccessWriter.writeShort(Short
							.reverseBytes((short) (nChannels * frameSize / 8)));
					// Block align, NbChannel * BitsPerSample / 8
					randomAccessWriter.writeShort(Short.reverseBytes(frameSize));
					// Nombre de bits par échantillon
					randomAccessWriter.writeBytes("data");
					randomAccessWriter.writeInt(0);
					// Taille de la data encore inconnue, mis à 0
					buffer = new byte[framePeriod * frameSize / 8 * nChannels];
					state = State.READY;
				} else {
					Log.e(AudioRecorder.class.getName(),
							"méthode prepare() appelé par un recorder non initialisé");
					state = State.ERROR;
				}
			} else {
				Log.e(AudioRecorder.class.getName(),
						"méthode prepare() appelé dans un état incohérent");
				release();
				state = State.ERROR;
			}
		} catch (Exception e) {
			if (e.getMessage() != null) {
				Log.e(AudioRecorder.class.getName(), e.getMessage());
			} else {
				Log.e(AudioRecorder.class.getName(),
						"Erreur inconnue dans la méthode prepare()");
			}
			state = State.ERROR;
		}
	}

	/**
	 * Rend les ressources associées de la classe et supprime les fichiers
	 * inutiles si nécessaire
	 */
	public void release() {
		if (state == State.RECORDING) {
			stop();
		} else {
			if ((state == State.READY)) {
				try {
					randomAccessWriter.close(); // Supprime le fichier préparé
				} catch (IOException e) {
					Log.e(AudioRecorder.class.getName(),
							"I/O exception levée lors de la fermeture du fichier de sortie");
				}
				(new File(filePath)).delete();
			}
		}
		if (audioRecorder != null) {
			audioRecorder.release();
		}
	}

	/**
	 * Remet le recorder à l'état INITIALIZING comme s'il venait d'être créé
	 * Dans le cas où la classe était à l'état RECORDING, l'enregistrement 
	 * est stoppé. Dans le cas d'une exception, l'état ERROR est mis.
	 */
	public void reset() {
		try {
			if (state != State.ERROR) {
				release();
				filePath = null; // Réinitialise le chemin du fichier
				cAmplitude = 0;  // Réinitialise l'amplitude
				audioRecorder = new AudioRecord(audioSource, sampleRate, nChannels + 1,
						audioFormat, bufferSize);
				state = State.INITIALIZING;
			}
		} catch (Exception e) {
			Log.e(AudioRecorder.class.getName(), e.getMessage());
			state = State.ERROR;
		}
	}

	/**
	 * Commence l'enregistrement et met l'état à RECORDING. 
	 * Appelé après la méthode prepare().
	 */
	public void start() {
		if (state == State.READY) {
			payloadSize = 0;
			audioRecorder.startRecording();
			audioRecorder.read(buffer, 0, buffer.length);
			state = State.RECORDING;
		} else {
			Log.e(AudioRecorder.class.getName(),
					"méthode start() appelé dans un état incohérent");
			state = State.ERROR;
		}
	}

	/**
	 * Stop l'enregistrement et met l'état à STOPPED.
	 * Finalise le fichier wave à la fin de l'enregistrement.
	 */
	public void stop() {
		if (state == State.RECORDING) {
			audioRecorder.stop();
			try {
				randomAccessWriter.seek(4); // Écrit la taille dans le RIFF header
				randomAccessWriter.writeInt(Integer
						.reverseBytes(36 + payloadSize));
				randomAccessWriter.seek(40);
				// Écrit la taille dans le champs Subchunk2Size
				randomAccessWriter.writeInt(Integer.reverseBytes(payloadSize));
				randomAccessWriter.close();
			} catch (IOException e) {
				Log.e(AudioRecorder.class.getName(),
						"I/O exception levée lors de la fermeture du fichier de sortie");
				state = State.ERROR;
			}
			state = State.STOPPED;
		} else {
			Log.e(AudioRecorder.class.getName(),
					"méthode stop() appelé dans un état incohérent");
			state = State.ERROR;
		}
	}

	/**
	 * Convertit un Byte[2] vers un Short au format LITTLE_ENDIAN
	 */
	private short getShort(byte argB1, byte argB2) {
		return (short) (argB1 | (argB2 << 8));
	}

}
