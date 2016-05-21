package com.mygdx.game.Gtab3.Algorithmes;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Kevin Lorant on 08/04/2015.
 */
public class Filtrage {

    /**
     * Pour chaque listes de notes entre deux silences on obtient la valeur moyenne de la fréquence mesurée sans les harmoniques
     * @param l liste de toutes les fréquences enregistrés
     * @return Renvoie la liste des fréquences de chaque note jouée.
     */
    public static ArrayList<String> filtre(ArrayList<String> l){
        ArrayList<String> buffer = new ArrayList<String>();
        ArrayList<String> result = new ArrayList<String>();
        boolean danslebuffer = false; //Sers à savoir si on est entre deux silences.
        for(String s:l){
            System.out.println(s);
            if(!s.contains("-1.0")){ //Si ce n'est pas un silence
                danslebuffer = true;
                buffer.add(s);
            }
            else {
                if(danslebuffer) {
                    if (buffer.size() > 5) { //Si on a un échantillon de fréquence suffisant
                        result.add(getPitch(buffer));
                        buffer = new ArrayList<String>();
                    }
                    danslebuffer = false;
                }
            }
        }
        return result;
    }

    /**
     *
     * @param l liste de toutes les fréquences captées lorsqu'une note est jouée
     * @return la valeur moyenne des fréquences mesurées sans les harmoniques
     */
    public static String getPitch(ArrayList<String> l){
        ArrayList<Float> list = new ArrayList<Float>();
        ArrayList<Float> listSansHarmo = new ArrayList<Float>();
        for(String s:l){
            list.add(Float.parseFloat(s));
        }
        Collections.sort(list);
        float total = 0;
        int index_debut = 0;
        while(list.get(index_debut) < 80) index_debut++; //Permet de passer les fréquences parasites impossibles à obtenir sur une guitare.
        listSansHarmo.add(list.get(index_debut));
        total += list.get(index_debut);
        for(int i =index_debut + 1;i<list.size();i++){
            if(list.get(i)> ((list.get(i-1)*0.10) + list.get(i-1)) //Si la fréquence dépasse d'un certain seuil la précédente,
             ||list.get(i)< (list.get(i-1) - (list.get(i-1)*0.10)) //on break pour ne pas fausser la moyenne.
                    ) {
                break;
            }
            else {
                listSansHarmo.add(list.get(i));
                System.out.println("Ajoute a la liste" + list.get(i));
                total+= list.get(i);
            }
        }
        return Float.toString(total/listSansHarmo.size());
    }

}
