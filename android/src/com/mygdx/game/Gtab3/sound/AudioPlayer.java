/**
 * @authors Henri NG && Jason CHUMMUN
 * @version 1.5
 * 
 * Cette classe permet de jouer un son enregistré précédemment.
 * 
 * Référence : ???
 */

package com.mygdx.game.Gtab3.sound;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class AudioPlayer {

	private MediaPlayer mPlayer;
	private boolean status = true;
	private String nomfic;
	private static final String LOG_TAG = "AudioPlayer";

	public AudioPlayer(String n) {
		nomfic = n;
	}

	public void startPlaying() {
		try {
			mPlayer = new MediaPlayer();
			mPlayer.setDataSource(nomfic);
			mPlayer.prepare();
			mPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
					status = false;
				}
			});
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	public boolean getStatus() {
		return status;
	}

	public void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
		status = false;
	}

}