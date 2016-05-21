package com.mygdx.game.Gtab3.tablature.generation;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import com.mygdx.game.Gtab3.tablature.exceptions.InvalidPosException;
import com.mygdx.game.Gtab3.tablature.interfaces.ITablatureGenerator;
import com.mygdx.game.Gtab3.tablature.interfaces.ITablatureView;
import com.mygdx.game.Gtab3.tablature.tablature.Position;
import com.mygdx.game.Gtab3.tablature.tablature.Tablature;
import com.mygdx.game.Gtab3.tablature.views.TabView;

public class TablatureGenerator implements ITablatureGenerator {
	private ITablatureView tablatureView; //la vue de la tablature générée
	private Tablature tablature; //tablature à générer
	private File dirFile; //le dossier contenant les notes
	String notesName ;//le nom du fichier contenant les notes (sortie de yinn)
	private static Hashtable<String, LPosition> notesPos; //liste de positions pour une note donnée
	private NoteLoader noteLoader; //charge les notes à partir d'un fichier
	private List<String> chords;
	
	
	public TablatureGenerator(File pathFile, ITablatureView tabView){
		tablatureView = tabView;
		((TabView) tabView).setGenerator(this);
		init();
		noteLoader = new NoteLoader();
		setDirFile(pathFile);

	}
	
	public NoteLoader getNoteLoader() {
		return noteLoader;
	}
	
	private void setDirFile(File path) {
		this.dirFile = path;
		noteLoader.setPath(dirFile);
	}
	
	public void setNotesName(String name){
		this.notesName = name;
		//noteLoader.parserFichier();
		noteLoader.parserFichier(name);
		
	}
	
	public void setChords(){
		
		
	}
	
	private void setTablature(Tablature t){
		tablature = t;
		notifyTablature();
	}
	
	public Note getNote(int pos){
		if(noteLoader.size()>pos){
			return  noteLoader.getNote(pos);
		}
		return null;
	}
	
	@Override
	public void notifyTablature() {
		tablatureView.updateTablature(tablature);
	}

	@Override
	public void randomConvert() {
		if(noteLoader.size()>0){
			Tablature t = new Tablature();
			for(int i=0; i<noteLoader.size(); i++){
				t.addPos(randPos(noteLoader.getNote(i).getValue()));
			}
			setTablature(t);
		}
	}

	@Override
	public void optDistConvert() {
		System.out.println("TabGen -> optDistConvert");
		if(noteLoader.size()>0){
			System.out.println("optDistConvert size = " + noteLoader.size());
			Tablature t = new Tablature();
			int min, max;
			t.addPos(randPos(noteLoader.getNote(0).getValue()));
			min = max = t.getPosition(0).getNumCase();
			for(int i=1; i<noteLoader.size(); i++){
				t.addPos(optDistPos(noteLoader.getNote(i).getValue(), min, max));
				Position p = t.getPosition(i);
				min = Math.min(min, p.getNumCase());
				max = Math.max(p.getNumCase(), max);
			}
			setTablature(t);
		}
	}

	@Override
	public void optDistBorneConvert(int bmin, int bmax) {
		if(noteLoader.size()>0){
			Tablature t = new Tablature();
			for(int i=0; i<noteLoader.size(); i++){
				t.addPos(optDistBorne(noteLoader.getNote(i).getValue(), bmin, bmax));
			}
			setTablature(t);
		}
	}
	
	
	private Position randPos(String note){
        System.out.println("Note randPos " + note);
		LPosition ps = getLPosition(note);
		return ps.getPos((int)(Math.random()*ps.getNbPos()));
	}
	
	private Position optDistPos(String note, int min, int max){
		LPosition ps = getLPosition(note);
		int ind = 0;
		int distp = dist(min, max, ps.getPos(ind).getNumCase()); 
		int dtmp;
		for(int i = 1; i<ps.getNbPos(); i++){
			dtmp = dist(min, max, ps.getPos(i).getNumCase());
			if(dtmp < distp){
				ind = i;
				distp = dtmp;
			}
		}
		return ps.getPos(ind);
	}
	
	private Position optDistBorne(String note, int bmin, int bmax){
		return optDistPos(note, bmin, bmax);
	}
	
	private int dist(int x1, int x2){
		return Math.abs(x2 - x1);
	}
	
	private int dist(int x1, int x2, int x3){
		int min = Math.min(x1, Math.min(x2, x3));
		int max = Math.max(x1, Math.max(x2, x3));
		return dist(min, max);
	}
	
	private LPosition getLPosition(String note){
		LPosition liste = new LPosition();
		switch (note.length()) {
		case 1: case 2:{
           //ICI LA LISTE EST NULLE, note = 60
                System.err.println(note);

			liste = notesPos.get(note);
                System.out.println("La liste que je veux voir c'est ca : " + liste);
			if(liste == null) liste = new LPosition();
			break;
		}
		case 3:{

			LPosition ltemp = notesPos.get(""+note.charAt(0)+note.charAt(2));
            System.out.println("CHARAT 0 " + note.charAt(0));
            System.out.println("CHARAT 2 " + note.charAt(2));
            System.out.println("La liste que je veux voir c'est ca 2 : " + ltemp);


            if(ltemp!=null){
				try{
					Position p;
					if(note.charAt(1) == '#'){
						for(int i=0; i<ltemp.getNbPos(); i++){
							p = ltemp.getPos(i);
							if(p.getNumCase()!=Position.MAXCASE){
								liste.add(new Position(p.getNumCorde(), p.getNumCase()+1));
							}
						}
					}else if(note.charAt(1) == 'b'){
						for(int i=0; i<ltemp.getNbPos(); i++){
							p = ltemp.getPos(i);
							if(p.getNumCase()!=Position.MINCASE){
								liste.add(new Position(p.getNumCorde(), p.getNumCase()-1));
							}
						}
					}
				}catch(InvalidPosException e){}
				break;
			}
			
		}
		default:
			break;
		}
		System.out.println("TabGenerator -> getLPosition : liste[0] = " + liste.getPos(0).getNumCase() + " " + liste.getPos(0).getNumCorde());
		return liste;
	}
	
	public static Hashtable<String, LPosition> getTableGen(){
		return notesPos;
	}
	
	private void init(){
		notesPos = new Hashtable<String, LPosition>();
		try{
			//silence
			notesPos.put("-1", new LPosition());
			notesPos.get("-1").add(new Position(-1, -1));
			/*coords du A3*/
			notesPos.put("A2", new LPosition());
			notesPos.get("A2").add(new Position(5, 0));
			notesPos.get("A2").add(new Position(6, 5));
			/*coords du A4*/
			notesPos.put("A3", new LPosition());
			notesPos.get("A3").add(new Position(3, 2));
			notesPos.get("A3").add(new Position(4, 7));
			notesPos.get("A3").add(new Position(5, 12));
			/*coords du A5*/
			notesPos.put("A4", new LPosition());
			notesPos.get("A4").add(new Position(1, 5));
			notesPos.get("A4").add(new Position(2, 10));
			/*coords du B3*/
			notesPos.put("B2", new LPosition());
			notesPos.get("B2").add(new Position(5, 2));
			notesPos.get("B2").add(new Position(6, 7));
			/*coords du B4*/
			notesPos.put("B3", new LPosition());
			notesPos.get("B3").add(new Position(2, 0));
			notesPos.get("B3").add(new Position(3, 4));
			notesPos.get("B3").add(new Position(4, 9));
			/*coords du B5*/
			notesPos.put("B4", new LPosition());
			notesPos.get("B4").add(new Position(1, 7));
			notesPos.get("B4").add(new Position(2, 12));
			/*coords du C4*/
			notesPos.put("C3", new LPosition());
			notesPos.get("C3").add(new Position(5, 3));
			notesPos.get("C3").add(new Position(6, 8));
			/*coords du C5*/
			notesPos.put("C4", new LPosition());
			notesPos.get("C4").add(new Position(2, 1));
			notesPos.get("C4").add(new Position(3, 5));
			notesPos.get("C4").add(new Position(4, 10));
			/*coords du D4*/
			notesPos.put("D3", new LPosition());
			notesPos.get("D3").add(new Position(4, 0));
			notesPos.get("D3").add(new Position(5, 5));
			notesPos.get("D3").add(new Position(6, 10));
			/*coords du D5*/
			notesPos.put("D4", new LPosition());
			notesPos.get("D4").add(new Position(2, 3));
			notesPos.get("D4").add(new Position(3, 7));
			notesPos.get("D4").add(new Position(4, 12));
			/*coords du D6*/
			notesPos.put("D5", new LPosition());
			notesPos.get("D5").add(new Position(1, 10));
			/*coords du E3*/
			notesPos.put("E2", new LPosition());
			notesPos.get("E2").add(new Position(6, 0));
			/*coords du E4*/
			notesPos.put("E3", new LPosition());
			notesPos.get("E3").add(new Position(4, 2));
			notesPos.get("E3").add(new Position(5, 7));
			notesPos.get("E3").add(new Position(6, 12));
			/*coords du E5*/
			notesPos.put("E4", new LPosition());
			notesPos.get("E4").add(new Position(1, 0));
			notesPos.get("E4").add(new Position(2, 5));
			notesPos.get("E4").add(new Position(3, 9));
			/*coords du E6*/
			notesPos.put("E5", new LPosition());
			notesPos.get("E5").add(new Position(1, 12));
			/*coords du F3*/
			notesPos.put("F2", new LPosition());
			notesPos.get("F2").add(new Position(6, 1));
			/*coords du F4*/
			notesPos.put("F3", new LPosition());
			notesPos.get("F3").add(new Position(4, 3));
			notesPos.get("F3").add(new Position(5, 8));
			/*coords du F5*/
			notesPos.put("F4", new LPosition());
			notesPos.get("F4").add(new Position(1, 1));
			notesPos.get("F4").add(new Position(2, 6));
			notesPos.get("F4").add(new Position(3, 10));

			/*coords du G3*/
			notesPos.put("G2", new LPosition());
			notesPos.get("G2").add(new Position(6, 3));
			/*coords du G4*/
			notesPos.put("G3", new LPosition());
			notesPos.get("G3").add(new Position(3, 0));
			notesPos.get("G3").add(new Position(4, 5));
			notesPos.get("G3").add(new Position(5, 10));
			/*coords du G5*/
			notesPos.put("G4", new LPosition());
			notesPos.get("G4").add(new Position(1, 3));
			notesPos.get("G4").add(new Position(2, 8));
			notesPos.get("G4").add(new Position(3, 12));
		}catch(InvalidPosException e){
			//To complete
		}
		
	}

}
