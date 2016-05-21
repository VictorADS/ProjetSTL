package com.mygdx.game.Gtab3.tablature.sandbox;

import java.util.ArrayList;
import java.util.List;

public class ChordsFinder2 {
	private static String[] standardChords = {"N/A","C","Dm","Em","F","G","Am"};
	private static String[] codeToName = {"C","C#","D","D#","E","F","F#","G","Ab","A","Bb","B"};
	//private static List<List<Integer>> noteslist = new ArrayList<List<Integer>>();
	
	public static Integer nameToCode(String nomNote){
		if(nomNote.equals("C"))
			return Integer.valueOf(0);
		else if(nomNote.equals("C#"))
			return Integer.valueOf(1);
		else if(nomNote.equals("D"))
			return Integer.valueOf(2);
		else if(nomNote.equals("D#"))
			return Integer.valueOf(3);
		else if(nomNote.equals("E"))
			return Integer.valueOf(4);
		else if(nomNote.equals("F"))
			return Integer.valueOf(5);
		else if(nomNote.equals("F#"))
			return Integer.valueOf(6);
		else if(nomNote.equals("G"))
			return Integer.valueOf(7);
		else if(nomNote.equals("G#"))
			return Integer.valueOf(8);
		else if(nomNote.equals("A"))
			return Integer.valueOf(9);
		else if(nomNote.equals("A#"))
			return Integer.valueOf(10);
		else 
			return Integer.valueOf(11);
		
	}
	
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
	
	

	/*public static boolean belongSameChord(List<Integer> notes){
		boolean res = true;
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Integer> currentIndices = noteToIndices(notes.get(0));
		//System.out.println("currentChords: " + PrintChords(currentIndices));
		for(int i=1;i<notes.size();i++){
			indices = noteToIndices(notes.get(i));
			//System.out.println("indices: " + PrintChords(indices));
			for (int j=0;j<currentIndices.size();j++){
				if (!indices.contains(currentIndices.get(j))){
					currentIndices.remove(j);
					j--;
				}
			}
			//System.out.println("currentindices: " + PrintChords(currentIndices));
			if(currentIndices.isEmpty()){
				res = false;
				break;
			}
		}
		
		return res;
	}*/
	
	/*public static String belongSameChord(List<Integer> notes){
        ArrayList<Integer> indices;
        ArrayList<Integer> currentIndices = noteToIndices(notes.get(0));

        for(int i=1;i<notes.size();i++){
                indices = noteToIndices(notes.get(i));
                for (int j=0;j<currentIndices.size();j++){
                        if (!indices.contains(currentIndices.get(j))){
                                currentIndices.remove(j);
                                j--;
                        }
                }
                if(currentIndices.isEmpty()){
                        return null;
                }
        }
        
        String prettyOutput = standardChords[currentIndices.get(0)];
        for (int i =1;i<currentIndices.size();i++){
        	prettyOutput+="/"+standardChords[currentIndices.get(i)];
        }
        return prettyOutput;
    }*/





	/*public static boolean belongSameChord(List<Integer> notes){
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
	}*/


	
	public static int whichChord(List<Integer> notes){
		
		//System.out.println(ListOfNotes(notes));
		
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
	
	public static String PrintChords(List<Integer> chords){
		String output = "List of Chords: [";
		for(int i=0;i<chords.size();i++){
			if(i!=chords.size()-1)
				output = output + standardChords[chords.get(i)] + ",";
			else
				output = output + standardChords[chords.get(i)];
		}
		
		output += "]";
		
		return output;
	}
	
	/*public static List<Integer> backtrackrec(List<Integer> notes, int stop, int end, List<List<Integer>> res){
		
		if(stop==0)
			return notes;
		
		else{
			List<Integer> sub = notes.subList(0, end);
			if(belongSameChord(sub)) {
				res.add(sub);
				return backtrack(notes.subList(end + 1, notes.size()),stop-1,3,res);	
			}
			else{
				//if(end<5){
					return backtrack(notes,stop,end + 1,res);
				//}
				else
					return backtrack(notes.subList(end + 1, notes.size()),stop-1,3,res);
				
			} 
		}
		
		
	}*/
	
	/*public static String PrintArray(int [] a){
		String res = "[";
		for(int i=0;i<a.length();i++){
			if(i!=chords.size()-1)
				output = output + standardChords[chords.get(i)] + ",";
			else
				output = output + standardChords[chords.get(i)];
		}
		
	}*/
	
	/*public static List<List<Integer>> backtrack(List<Integer> notes){
		
		 int [] beginings = {0,0,0,0};
		 int [] ends = {2,0,0,0};
		 List<List<Integer>> res = new ArrayList<List<Integer>>();
		
		for(int i=0;i<4;i++){
			//System.out.println("i = " + i);
			
			//System.out.println("start = " + beginings[i]);
			//System.out.println("end = " + ends[i]);
			if(belongSameChord(notes.subList(beginings[i], ends[i]+1))){
				System.out.println("if");
				//System.out.println("sublist start:" + beginings[i] + ", end: " + ends[i] +
					//	 ListOfNotes(notes.subList(beginings[i], ends[i] + 1)));
				res.add(notes.subList(beginings[i], ends[i] + 1));
				beginings[i+1] = ends[i] + 1;
				ends[i+1] = ends[i] + 3;
			}
			
			else{
				if(i!=0){
					i--;
					while(ends[i]-beginings[i]==4){
						//System.out.println("while");
						beginings[i] = 0;
						ends[i] = 0;
						res.remove(i);
						i--;
					}
						res.remove(i);
						ends[i]++;
						//System.out.println("start = " + beginings[i]);
						//System.out.println("end = " + ends[i]);
										
					i--;
				}
			}
			System.out.println(res);
			System.out.println("i = " + i);	
		}
		return res;
	}*/
	
	public static List<List<Integer>> union(List<List<Integer>> left, List<List<Integer>> right){
		for (List<Integer> e : right){
			left.add(e);
		}
		return left;
	}
	
	private static int recursionCount;
	
	public static List<Integer> belongSameChord2(List<Integer> notes){
		//ArrayList<List<Integer>> res = new ArrayList<List<Integer>>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        ArrayList<Integer> currentIndices = noteToIndices(notes.get(0));

        for(int i=1;i<notes.size();i++){
                indices = noteToIndices(notes.get(i));
                for (int j=0;j<currentIndices.size();j++){
                        if (!indices.contains(currentIndices.get(j))){
                                currentIndices.remove(j);
                                j--;
                        }
                }
        }
                //res.add(indices);
                if(currentIndices.isEmpty()){
                        return null;
                }
                else
                	return notes;
        }
	
        
        public static List<List<Integer>> processDnC(List<Integer> notes1){
    		recursionCount++;
    		//System.out.println("recursionCount = " + recursionCount);
    		
    		//if (recursionCount%5000000==1000000){System.out.print("/");}
    		//if (recursionCount%5000000==2000000){System.out.print("\b-");}
    		//if (recursionCount%5000000==3000000){System.out.print("\b\\");}
    		//if (recursionCount%5000000==4000000){System.out.print("\b|");}
    		//if (recursionCount%5000000==0){System.out.print("\b*");}
    		if (recursionCount%5000000==0){System.out.print(".");}
    		
    		if (notes1.size()<3) return null;
    		
    		if (notes1.size()<6){
    			//List<String> result = new ArrayList<String>();
    			List<Integer> tmp = belongSameChord2(notes1);
    			List<List<Integer>> res = new ArrayList<List<Integer>>();
    			if (tmp==null){
    				return null;
    			} else {
    				res.add(tmp);
    				return res;
    			}
    		}
    		
    		int n=notes1.size();
    		List<List<Integer>> left;
    		List<List<Integer>> right;
    		left=processDnC(notes1.subList(0,n/2-2));
    		right=processDnC(notes1.subList(n/2-2,n));
    		if (left!=null && right!=null){
    			
    			return union(left,right); 
    		}
    		
    		left=processDnC(notes1.subList(0,n/2-1));
    		right=processDnC(notes1.subList(n/2-1,n));
    		if (left!=null && right!=null){
    			return union(left,right); 
    		}
    		
    		left=processDnC(notes1.subList(0,n/2));
    		right=processDnC(notes1.subList(n/2,n));
    		if (left!=null && right!=null){
    			return union(left,right); 
    		}
    		
    		left=processDnC(notes1.subList(0,n/2+1));
    		right=processDnC(notes1.subList(n/2+1,n));
    		if (left!=null && right!=null){
    			return union(left,right); 
    		}
    		
    		left=processDnC(notes1.subList(0,n/2+2));
    		right=processDnC(notes1.subList(n/2+2,n));
    		if (left!=null && right!=null){
    			return union(left,right); 
    		}
    		
    		return null;
    	}
	
	/*public static List<String> union(List<String> left, List<String> right){
		for (String e : right){
			left.add(e);
		}
		return left;
	}*/
	
	//private static int recursionCount;
	
	public static String belongSameChord(List<Integer> notes){
        ArrayList<Integer> indices;
        ArrayList<Integer> currentIndices = noteToIndices(notes.get(0));

        for(int i=1;i<notes.size();i++){
                indices = noteToIndices(notes.get(i));
                for (int j=0;j<currentIndices.size();j++){
                        if (!indices.contains(currentIndices.get(j))){
                                currentIndices.remove(j);
                                j--;
                        }
                }
                if(currentIndices.isEmpty()){
                        return null;
                }
        }
        
        String prettyOutput = standardChords[currentIndices.get(0)];
        for (int i =1;i<currentIndices.size();i++){
        	prettyOutput+="/"+standardChords[currentIndices.get(i)];
        }
        return prettyOutput;
    }

	
	/*public static List<String> processDnC(List<Integer> notes){
		recursionCount++;
		
		//if (recursionCount%5000000==1000000){System.out.print("/");}
		//if (recursionCount%5000000==2000000){System.out.print("\b-");}
		//if (recursionCount%5000000==3000000){System.out.print("\b\\");}
		//if (recursionCount%5000000==4000000){System.out.print("\b|");}
		//if (recursionCount%5000000==0){System.out.print("\b*");}
		if (recursionCount%5000000==0){System.out.print(".");}
		
		if (notes.size()<3) return null;
		
		if (notes.size()<6){
			List<String> result = new ArrayList<String>();
			String tmp = belongSameChord2(notes);
			if (tmp==null){
				return null;
			} else {
				System.out.println("tmp: " + tmp);
				result.add(tmp);			
				// modif pour obtenir les listes 
				return result;
			}
		}
		
		int n=notes.size();
		List<String> left;
		List<String> right;
		
		left=processDnC(notes.subList(0,n/2-2));
		right=processDnC(notes.subList(n/2-2,n));
		if (left!=null && right!=null){
			System.out.println("left: " + left);
			System.out.println("right: " + right);
			return union(left,right); 
		}
		
		left=processDnC(notes.subList(0,n/2-1));
		right=processDnC(notes.subList(n/2-1,n));
		if (left!=null && right!=null){
			System.out.println("left: " + left);
			System.out.println("right: " + right);
			return union(left,right); 
		}
		
		left=processDnC(notes.subList(0,n/2));
		right=processDnC(notes.subList(n/2,n));
		if (left!=null && right!=null){
			System.out.println("left: " + left);
			System.out.println("right: " + right);
			return union(left,right); 
		}
		
		left=processDnC(notes.subList(0,n/2+1));
		right=processDnC(notes.subList(n/2+1,n));
		if (left!=null && right!=null){
			System.out.println("left: " + left);
			System.out.println("right: " + right);
			return union(left,right); 
		}
		
		left=processDnC(notes.subList(0,n/2+2));
		right=processDnC(notes.subList(n/2+2,n));
		if (left!=null && right!=null){
			System.out.println("left: " + left);
			System.out.println("right: " + right);
			return union(left,right); 
		}
		
		return null;
	}*/

	
	/*public static void naif(List<Integer> notes){
		
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
								fourth = notes.subList(k,n);
								//System.out.println("Processing "+fourth);
								//System.out.println("belong to same "+belongSameChord(fourth));
								
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
	*/

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
