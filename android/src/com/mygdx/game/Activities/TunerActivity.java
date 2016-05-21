package com.mygdx.game.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.mygdx.game.IHMManager;
import com.mygdx.game.LibGDXActivity.LibGDXFragment;
import com.mygdx.game.NoteManager.FrequencyFilter;
import com.mygdx.game.NoteManager.Note;
import com.mygdx.game.NoteManager.Tuner;
import com.mygdx.game.R;
import com.mygdx.game.Activities.SettingsActivity;

import java.util.concurrent.Semaphore;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class TunerActivity extends FragmentActivity implements AndroidFragmentApplication.Callbacks{
	private AudioDispatcher dispatcher = null;
	private Thread noteThread;
	private LibGDXFragment fragment;
	private boolean isRunning=false;
	private static final int CODE_RETOUR_PREF = 0;
	private Tuner tuner;
	private Semaphore sem = null;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("TunerActivity", "debut onCreate");
		setContentView(R.layout.tuner_activity);
		fragment = new LibGDXFragment();
		FragmentTransaction tr = getSupportFragmentManager().beginTransaction(); //GDX PART
		tr.replace(R.id.NeedleView, fragment);
		tr.commit();
		getSupportFragmentManager().executePendingTransactions();
		Log.d("Test", "This is an app");
		Log.d("TunerActivity", "onCreate, avant initThread");
		sem = new Semaphore(1);
		tuner=new Tuner();
		initializeThread();

	}
	float max=0;
	float min=Float.MAX_VALUE;
	public void initializeThread(){
		//getPreferences();

		dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(16000, 1024, 0);	// ACTIVER LAUDIO LISTNER
		PitchDetectionHandler pdh = new PitchDetectionHandler() {
			@Override
			public void handlePitch(PitchDetectionResult result,AudioEvent e) {
				try {
					sem.acquire();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				tuner.updateValues(result,e);
				fragment.getManager().update(tuner.getMidimoyenne(), tuner.getCurrentFreqref());
				sem.release();

			}

		};


		AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, 16000, 1024, pdh);
		dispatcher.addAudioProcessor(p);
		noteThread=new Thread(dispatcher,"Audio Dispatcher");
		noteThread.start();
		isRunning=true;
	}
	@Override
	public void onPause(){
		super.onPause();
		Log.d("Pause", "Jai fait pause");
		if(dispatcher!=null && isRunning) {
			dispatcher.stop();
			isRunning=false;
		}
	}
	@Override
	public void onResume(){
		super.onResume();
		Log.d("Resume", "Debut onResume");
		if(dispatcher!=null && !isRunning) {
			Log.d("Resume","Not null1");
			initializeThread();
			isRunning=true;
			Log.d("Resume","Not null");
		}
		Log.d("Resume", "Fin onResume");
	}

	@Override
	public void exit() {
		dispatcher.stop();
		Log.d("exit","Ca sert a exit");
	}

}
