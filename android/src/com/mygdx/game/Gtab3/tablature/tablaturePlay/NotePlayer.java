package com.mygdx.game.Gtab3.tablature.tablaturePlay;

import com.mygdx.game.Gtab3.sound.NoteDetection;
import com.mygdx.game.Gtab3.sound.SyntheSound;
import com.mygdx.game.Gtab3.tablature.generation.Note;
import com.mygdx.game.Gtab3.tablature.interfaces.INotePlayer;

public class NotePlayer implements INotePlayer {
	NoteDetection noteDetection;
	public NotePlayer(){
		noteDetection = new NoteDetection();
	}

	@Override
	public void playNote(Note note) {
		double freq = noteDetection.noteToFrequency(note.getValue());
		double dur = 1;//note.getDuree();
		SyntheSound synthSound = new SyntheSound(freq, dur);
		synthSound.genTone();
		synthSound.playSound();
	}

}
