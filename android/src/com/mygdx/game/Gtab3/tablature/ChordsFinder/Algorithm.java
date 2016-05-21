package com.mygdx.game.Gtab3.tablature.ChordsFinder;
import java.util.ArrayList;
public class Algorithm {
	
	/**
	 * Renvoie la liste d'accords correspondant à la liste de notes
	 * passée en paramètre.
	 * @param notes
	 * @return La liste d'accords correspondant à notes
	 */
	public ArrayList<Accord>compute(ArrayList<Note> notes){
		/* La liste des accords trouvée */
		ArrayList<Accord> accords = new ArrayList<Accord>();
		/* Notes actuellement déterminées
		 * (On en détermine 2 à la fois)
		 */
		Note noteA = notes.get(0);
		Note noteB;
		/* L'écart entre noteA et noteB */
		int ecart;
		/* Couple de possibilités que l'on a pour l'accord actuel */
		Accord[] possibilites = new Accord[2];
		for(int i = 1; i < notes.size(); ++i){
			/* On récupère la deuxième note et on calcule l'écart avec la première*/
			noteB = notes.get(i);
			ecart = noteA.ecart(noteB);
			if(ecart == 0) {
				/* Passage a la prochaine note */
				noteA = noteB;
				continue;
			}
			/* Si les possibilités sont vides, il faut en produire */
			if(possibilites[0] == null){
				ajouterPossibilites(possibilites, noteA, noteB, ecart);
			}
			else{
				/* On verifie que l'écart correspond au moins à une des possibilités */
				switch(ecartCorrespondant(possibilites, noteA, noteB, ecart)){
				/* Seulement la premiere possiblité correspond */
				case 0 :
					break;
					/* Seulement la deuxième possibilité correspond */
				case 1 :
					possibilites[0] = possibilites[1];
					possibilites[1] = null;
					break;
					/* Les deux possibilités correspondent */
				case 2 :
					break;
					/* Aucune des deux possibilités correspond */
				default :
					/* Alors il faut rajouter une possibilité à 0 (la première est toujours non vide) */
					accords.add(possibilites[0]);
					/* On vide les possibilités */
					possibilites[0] = null;
					possibilites[1] = null;
				}
			}
			/* Passage a la prochaine note */
			noteA = noteB;
		}
		if(possibilites[0] != null)
			accords.add(possibilites[0]);
		return accords;
	}
	/**
	 * Initialise le tableau possibilites avec les couples de notes
	 * possibles suivant l'écart donné.
	 * @param possibilites
	 * @param noteA
	 * @param noteB
	 * @param ecart
	 */
	private void ajouterPossibilites(Accord[] possibilites, Note noteA,
			Note noteB, int ecart) {
		switch(ecart){
		case 3:
			possibilites[0] = new Accord(noteA, noteB, null);
			possibilites[1] = new Accord(null, noteA, noteB);
			break;
		case 4:
			possibilites[0] = new Accord(noteA, noteB, null);
			possibilites[1] = new Accord(null, noteA, noteB);
			break;
		case 5 :
			possibilites[0] = new Accord(noteB, null, noteA);
			break;
		case 7 :
			possibilites[0] = new Accord(noteA, null, noteB);
			break;
		case 8 :
			possibilites[0] = new Accord(null, noteB, noteA);
			possibilites[1] = new Accord(noteB, noteA, null);
			break;
		case 9 :
			possibilites[0] = new Accord(null, noteB, noteA);
			possibilites[1] = new Accord(noteB, noteA, null);
			break;
		default :
			//System.err.println("Ecart ne devrait pas etre permis : "+ ecart);
		}
	}
	/**
	 * Retourne -1 si aucune des possibilites ne correspond
	 * 0 si la premiere correspond
	 * 1 si la deuxieme correspond
	 * 2 si les deux correspondent
	 * @param possibilites
	 * @param noteA
	 * @param noteB
	 * @param ecart
	 * @return
	 */
	private int ecartCorrespondant(Accord[] possibilites, Note noteA,
			Note noteB, int ecart) {
		if(mettreAJourAccord(possibilites[0],noteA, noteB, ecart)){
			if(mettreAJourAccord(possibilites[1], noteA, noteB, ecart)){
				return 2;
			}
			return 0;
		}
		if(mettreAJourAccord(possibilites[1], noteA, noteB, ecart)){
			return 1;
		}
		return -1;
	}
	/**
	 * Met a jour la possibilité avec noteA et noteB si elles sont compatibles
	 * avec les notes déjà trouvées.
	 * @param possibilite
	 * @param noteA
	 * @param noteB
	 * @param ecart
	 * @return Vrai si on a pu mettre a jour, faux sinon
	 */
	private boolean mettreAJourAccord(Accord possibilite, Note noteA, Note noteB, int ecart){
		if(possibilite == null) return false;
		switch(ecart){
		case 3:
			if(possibilite.getTonique() != null && possibilite.getTonique().equals(noteA)){
				if(possibilite.getTierce() == null || possibilite.getTierce().equals(noteB)){
					possibilite.setTierce(noteB);
					return true;
				}
				return false;
			}
			else if(possibilite.getTierce() != null && possibilite.getTierce().equals(noteA)){
				if(possibilite.getQuinte() == null || possibilite.getQuinte().equals(noteB)){
					possibilite.setQuinte(noteB);
					return true;
				}
				return false;
			}
			return false;
		case 4:
			if(possibilite.getTonique() != null && possibilite.getTonique().equals(noteA)){
				if(possibilite.getTierce() == null || possibilite.getTierce().equals(noteB)){
					possibilite.setTierce(noteB);
					return true;
				}
				return false;
			}
			else if(possibilite.getTierce() != null && possibilite.getTierce().equals(noteA)){
				if(possibilite.getQuinte() == null || possibilite.getQuinte().equals(noteB)){
					possibilite.setQuinte(noteB);
					return true;
				}
				return false;
			}
			return false;
		case 5 :
			if(possibilite.getQuinte() != null && possibilite.getQuinte().equals(noteA)){
				if(possibilite.getTonique() == null || possibilite.getTonique().equals(noteB)){
					possibilite.setTonique(noteB);
					return true;
				}
				return false;
			}
			return false;
		case 7 :
			if(possibilite.getTonique() != null && possibilite.getTonique().equals(noteA)){
				if(possibilite.getQuinte() == null || possibilite.getQuinte().equals(noteB)){
					possibilite.setQuinte(noteB);
					return true;
				}
				return false;
			}
			return false;
		case 8 :
			if(possibilite.getQuinte() != null && possibilite.getQuinte().equals(noteA)){
				if(possibilite.getTierce() == null || possibilite.getTierce().equals(noteB)){
					possibilite.setTierce(noteB);
					return true;
				}
				return false;
			}else if(possibilite.getTierce() != null && possibilite.getTierce().equals(noteA)){
				if(possibilite.getTonique() == null || possibilite.getTonique().equals(noteB)){
					possibilite.setTonique(noteB);
					return true;
				}
				return false;
			}
			return false;
		case 9 :
			if(possibilite.getQuinte() != null && possibilite.getQuinte().equals(noteA)){
				if(possibilite.getTierce() == null || possibilite.getTierce().equals(noteB)){
					possibilite.setTierce(noteB);
					return true;
				}
				return false;
			}else if(possibilite.getTierce() != null && possibilite.getTierce().equals(noteA)){
				if(possibilite.getTonique() == null || possibilite.getTonique().equals(noteB)){
					possibilite.setTonique(noteB);
					return true;
				}
				return false;
			}
			return false;
		default :
			return false;
		}
	}

	public ArrayList<Accord> computeInteger(ArrayList<Integer> notes){
		
		ArrayList<Note> notes2 = new ArrayList<Note>();
		for(Integer i : notes){
			notes2.add(new Note(i));
		}
		return compute(notes2);
	}
	
}
