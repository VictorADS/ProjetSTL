package com.mygdx.game.Gtab3.tablature.sandbox;

import java.util.ArrayList;
import java.util.List;

public class ChordsFinder {
	private static String[] standardChords = {"N/A","C","Dm","Em","F","G","Am"};
	private static String[] codeToName = {"C","C#","D","D#","E","F","F#","G","Ab","A","Bb","B"};
	//private static Int[] chordsCode = 
	public static void notesToChords(ArrayList<Integer> notes) {
		ArrayList<String> chords = new ArrayList<String>();
		String currentChord;
		ArrayList<Integer> currentPossibleIndices;
		ArrayList<Integer> currentIndices;
		String prettyOutput;
		
		System.out.println("===========");
		System.out.println("Processing MIDI code " + notes.get(0));
		System.out.println("===========");
		System.out.println("");
		currentPossibleIndices = noteToIndices(notes.get(0));
		System.out.println("Estimated indices "+currentPossibleIndices);
		currentChord = standardChords[currentPossibleIndices.get(0)];
		System.out.println("Estimated chord "+currentChord);
		
		prettyOutput="[ ["+codeToName[notes.get(0)%12];
		
		for (int i=1;i<notes.size();i++){
			System.out.println("");
			System.out.println("===========");
			System.out.println("Processing MIDI code "+notes.get(i));
			System.out.println("===========");
			System.out.println("");
			
			currentIndices = noteToIndices(notes.get(i));
			
			for (int j=0;j<currentPossibleIndices.size();j++){
				if (!currentIndices.contains(currentPossibleIndices.get(j))){
					currentPossibleIndices.remove(j);
					j--;
				}
			}
			
			if (currentPossibleIndices.isEmpty()){
				prettyOutput+="], ["+codeToName[notes.get(i)%12];
				
				chords.add(currentChord);
				System.out.println("Output chord "+currentChord);
				
				System.out.println("Switching chord");
				
				currentPossibleIndices = noteToIndices(notes.get(i));
				System.out.println("New estimated indices "+currentPossibleIndices);
				
				currentChord = standardChords[currentPossibleIndices.get(0)];
				System.out.println("New estimated chord "+currentChord);
			} else {
				prettyOutput+=", "+codeToName[notes.get(i)%12];
				
				System.out.println("Estimated indices "+currentPossibleIndices);
				currentChord = standardChords[currentPossibleIndices.get(0)];
				System.out.println("Estimated chord "+currentChord);
			}
		}
		chords.add(currentChord);
		System.out.println("Output chord "+currentChord);
		System.out.println("");
		System.out.println("===========");
		System.out.println("");
		System.out.println("End of procedure");
		System.out.println("");
		System.out.println("=================================================");
		System.out.println("");
		System.out.println("Notes are grouping as:");
		System.out.println(prettyOutput+"] ]");		
		System.out.println("");
		System.out.println("Final output:");
		System.out.println("  Number of chords: "+chords.size());
		System.out.println("  Chord list: "+chords);
		return;
	}
	
	public static boolean belongSameChord(List<Integer> notes){
		boolean res = true;
		ArrayList<Integer> indices = new ArrayList<Integer>();
		int currentindice = noteToIndices(notes.get(0)).get(0);
		
		for(int i=1;i<notes.size();i++){
			indices = noteToIndices(notes.get(i));
			if(!indices.contains((int)currentindice)){
				res = false;
				break;
			}
		}
		
		return res;
	}
	
	public static int whichChord(List<Integer> notes){
		
		return noteToIndices(notes.get(0)).get(0);
	}
	
	public static String ListOfNotes(List<Integer> notes){
		String output = "[";
		for(int i=0;i<notes.size();i++){
			if(i!=notes.size()-1)
				output = output + codeToName[notes.get(i)%12] + ",";
			else
				output = output + codeToName[notes.get(i)%12];
		}
		output += "]";
		return output;
	}
	
	
	
	public static void PrintNotes(List<List<Integer>> groupOfNotes){

		String output = "List of Notes: [";
		for(int i=0;i<groupOfNotes.size();i++){
			if(i!=groupOfNotes.size()-1)
				output = output + ListOfNotes(groupOfNotes.get(i)) + ",";
			else
				output = output + ListOfNotes(groupOfNotes.get(i));
		}
		output += "]";
		System.out.println(output);
	}
	
	public static void PrintChords(List<Integer> chords){
		String output = "List of Chords: [";
		for(int i=0;i<chords.size();i++){
			if(i!=chords.size()-1)
				output = output + standardChords[chords.get(i)] + ",";
			else
				output = output + standardChords[chords.get(i)];
		}
		
		output += "]";
		System.out.println(output);
	}
	
	
	public static void naif(List<Integer> notes){
		
		int n = notes.size();
		List<Integer> first = new ArrayList<Integer>();
		List<Integer> second = new ArrayList<Integer>();
		List<Integer> third = new ArrayList<Integer>();
		List<Integer> fourth = new ArrayList<Integer>();
		Integer Chord1;
		Integer Chord2;
		Integer Chord3;
		Integer Chord4;
		List<List<Integer>> currentList;
		List<Integer> chords;

		for(int i=1;i<n;i++){
			
			first = notes.subList(0, i);
			
			if(belongSameChord(first)){
				
				Chord1 = whichChord(first);
				for(int j=i+1;j<n;j++){
					
					second = notes.subList(i,j);
					if(belongSameChord(second)){
					Chord2 = whichChord(second);
						for(int k=j+1;k<n;k++){
							
							third = notes.subList(j,k);
							if(belongSameChord(third)){
							Chord3 = whichChord(third);
								for(int l=k;l<n;l++){
									
									fourth = notes.subList(l,n);
									
									if(belongSameChord(fourth)){
										currentList = new ArrayList<List<Integer>>();
										chords = new ArrayList<Integer>();
											int size = first.size() + second.size() + third.size() + fourth.size();
											currentList.add(first);
											currentList.add(second);
											currentList.add(third);
											currentList.add(fourth);
											Chord4 = whichChord(fourth);
											chords.add(Chord1);
											chords.add(Chord2);
											chords.add(Chord3);
											chords.add(Chord4);
											if(notes.size()== size){
												PrintNotes(currentList);
												PrintChords(chords);
												
											}
											else 
												break;
									}
									else
										break;
								}
							}
							else
								break;
							
						}
					}
					else
						break;
				}
			}
			else
				break;
		}
		
		//return results;
	}

	// standardChords indices:
	// 0 = Am/C ----notes----> 0, 4, 7, 9
	// 1 = Dm/F ----notes----> 5, 9, 0, 2
	// 2 = Em/G ----notes----> 7, 11, 2, 4
	// 3 = N/A  ----notes----> 1, 3, 6, 8, 10
	private static ArrayList<Integer> noteToIndices(int note){
		ArrayList<Integer> chords = new ArrayList<Integer>();
		int n=note%12;
		
		if (n==1||n==3||n==6||n==8||n==10){
			chords.add(0);
			return chords;
		}
		
		if (n==0||n==4||n==7){chords.add(1);}
		if (n==5||n==9||n==2){chords.add(2);}
		if (n==7||n==11||n==4){chords.add(3);}
		if (n==5||n==9||n==0){chords.add(4);}
		if (n==7||n==11||n==2){chords.add(5);}
		if (n==0||n==4||n==9){chords.add(6);}
		
		return chords;
	}
}
