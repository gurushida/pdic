package com.gurushida.pdic;

import java.io.*;
import java.util.*;

/**
 * This class represents an acyclic automaton.
 */
public class Automaton {

	private State root;

	public Automaton() {
		root = new State(false);
	}

	/**
	 * Saves a binary representation of the automaton into the given file.
	 */
	public void save(File file) throws IOException {
		DicSaver saver = new DicSaver(file, root);
		saver.save();
	}

	/**
	 * Adds the given sequence to the automaton.
	 *
	 * Adding multiple sequences to an initially empty automaton
	 * will produce a lexicographical tree.
	 */
	private void addSequence(String sequence) {
		if (sequence == null || sequence.isEmpty()) {
			throw new RuntimeException("Cannot add a null or empty sequence to the automaton");
		}
		root.addPath(sequence, 0);
	}

	/**
	 * Loads the lines of an UTF-8 text file and adds them to the automaton.
	 */
	public void load(File file) throws FileNotFoundException, IOException {
		try {
			FileInputStream stream;
			stream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			String line;
			int n = 0;
			while ((line = reader.readLine()) != null) {
				if (n % 10000 == 0) {
					System.out.print(n + " lines read...               \r");
				}
				if (n == 0 && line.startsWith("\uFEFF")) {
					line = line.substring(1);
				}
				addSequence(line);
				n++;
			}
			System.out.println(n + " lines read.                                                  ");
			reader.close();
			stream.close();
		} catch (UnsupportedEncodingException e) {
			// Cannot happen as UTF-8 is a default supported charset
		}
	}

	/**
	 * This method minimizes the acylic automaton by applying Revuz's algorithm
	 * that consists of sorting states by height and then merging equivalent states of
	 * same height.
	 */
	public void minimize() {
		// We allocate an array of arrays for stocking states by height
		ArrayList<ArrayList<State>> statesSortedByHeight = new ArrayList<ArrayList<State>>();
		System.out.println("Sorting nodes by height...");
		root.sortStatesByHeight(statesSortedByHeight);
		Register register = new Register();
		for (int i = 1; i < statesSortedByHeight.size(); i++) {
			System.out.print("Factorizing nodes of height " + i + "...\r");
			System.out.flush();
			ArrayList<State> array = statesSortedByHeight.get(i);
			int n = array.size();
			for (int j = 0; j < n; j++) {
				for (Transition t : array.get(j).getTransitions()) {
					t.destination = register.getOrPut(t.destination);
				}
			}
		}
		System.out.println();
	}

}
