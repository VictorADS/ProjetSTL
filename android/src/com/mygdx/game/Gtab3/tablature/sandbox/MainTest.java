package com.mygdx.game.Gtab3.tablature.sandbox;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainTest {
	public static void main(String[] args) {
		String filename="toto.txt";
		int i;
		System.out.println("toto");
		for (i=0;i<args.length;i++){
			if (args[i].charAt(0)=='-'){
				if (args[i+1].charAt(0)=='-'){
					System.err.println("Option "+args[i]+" expects an argument but received none");
					return;
				}
				if (args[i]=="-filename"){
					try {
						filename = args[i+1];
					} catch (Exception e){
						System.err.println("Invalid argument for option "+args[i]);
						return;
					}
					System.err.println("Unknown option "+args[i]);
					return;
				}
				i++;
			}
		}
		readFile(filename);
	}
	
	public static void readFile(String filename) {
		String line;
		String[] codes_MIDI;
		List<Integer> notes=new ArrayList<Integer>();
		try {
			BufferedReader input = new BufferedReader(
					new InputStreamReader(new FileInputStream(filename))
			);
			try {
				while ((line=input.readLine())!=null ) {
					codes_MIDI=line.split("\\s+");
					for (int i=0;i<codes_MIDI.length;i++){
						notes.add(Integer.parseInt(codes_MIDI[i]));
					}
				}
				//System.out.println(ChordsFinder.ListOfNotes(notes));
				if (notes.isEmpty()){
					System.err.println("Input file empty.");
					return;
				}
				System.out.println("startTime");
				long startTime = System.nanoTime();
				System.out.println(ChordsFinder2.processDnC(notes));
				   // ... the code being measured ...
				long estimatedTime = System.nanoTime() - startTime;
				System.out.println("estimatedTime = " + estimatedTime);
				//ChordsFinder2.PrintNotes(res);
				
				
			} catch (IOException e) {
				System.err.println("Exception: interrupted I/O.");
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("I/O exception: unable to close "+filename);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found.");
		}
	}

}
