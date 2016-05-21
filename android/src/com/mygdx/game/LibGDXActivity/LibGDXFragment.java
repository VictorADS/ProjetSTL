package com.mygdx.game.LibGDXActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.mygdx.game.IHMManager;
import com.mygdx.game.NoteManager.FrequencyFilter;
import com.mygdx.game.NoteManager.Note;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Created by afuri on 31/01/2016.
 */
public class LibGDXFragment extends AndroidFragmentApplication {

    private IHMManager ihm;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        ihm = new IHMManager();

        return initializeForView(ihm);
    }
    public IHMManager getManager() {
        return ihm;
    }









}
