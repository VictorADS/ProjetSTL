package com.mygdx.game.Gtab3.tablature.interfaces;

import com.mygdx.game.Gtab3.tablature.generation.NoteLoader;

public interface ITablatureGenerator {
	public void notifyTablature();
	public void randomConvert();
	public void optDistConvert();
	public void optDistBorneConvert(int bmin, int bmax);
	public NoteLoader getNoteLoader();
}
