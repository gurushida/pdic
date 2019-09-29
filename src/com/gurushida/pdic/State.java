package com.gurushida.pdic;

import java.util.*;

public class State implements Comparable<State> {

	boolean terminal;
	Transition[] transitions;

	public State(boolean terminal) {
		this.terminal = terminal;
	}

	/**
	 * Inserts a sequence from the current state for the substring of the
	 * given sequence that begins at position i.
	 *
	 * @param sequence the whole word to insert
	 * @param i     the current position in the word to insert
	 */
	public void addPath(String sequence, int i) {
		if (i == sequence.length()) {
			// if we are at the end of the input
			terminal = true;
			return;
		}
		Transition t = new Transition(sequence.charAt(i), null);
		if (transitions == null) {
			// if the transitions array was null, we create it and
			// add the transition
			transitions = new Transition[1];
			State destination = new State(false);
			t.destination = destination;
			transitions[0] = t;
			destination.addPath(sequence, i + 1);
			return;
		}
		int z;
		int compare;
		for (z = 0; z < transitions.length && (compare = transitions[z].compareToWithoutDestination(t)) <= 0; z++) {
			if (compare == 0) {
				// if there is already a transition with same letter,
				// we follow it
				State destination = transitions[z].destination;
				destination.addPath(sequence, i + 1);
				return;
			}
		}
		// if there is no matching transition, we
		// create one and add it to the transition array
		Transition[] tmp = new Transition[transitions.length + 1];
		System.arraycopy(transitions, 0, tmp, 0, z);
		tmp[z] = t;
		System.arraycopy(transitions, z, tmp, z + 1, transitions.length - z);
		transitions = tmp;
		t.destination = new State(false);
		t.destination.addPath(sequence, i + 1);
	}

	/**
	 * This method sorts the sons of the current state by height. Then, it computes
	 * the height of the current state and inserts it in the statesSortedByHeight
	 * array of arrays
	 *
	 * @param statesSortedByHeight structure used to stock states by height
	 * @return the height of the current state
	 */
	public int sortStatesByHeight(ArrayList<ArrayList<State>> statesSortedByHeight) {
		int height = 0;
		if (transitions != null) {
			// we recursively add states to the sorted ArrayList of ArrayList
			for (Transition t : transitions) {
				int h = t.destination.sortStatesByHeight(statesSortedByHeight);
				// and we update the height of the current state as follows:
				// if the current state has one son of height 4 and another of
				// height 7, then the height of the current state is 7+1=8
				if (height == 0 || h > height) {
					height = h;
				}
			}
			height = height + 1;
		}
		ArrayList<State> array;
		if (statesSortedByHeight.size() <= height || (array = statesSortedByHeight.get(height)) == null) {
			// if there is currently no array for the index height, we create it
			array = new ArrayList<State>();
			statesSortedByHeight.add(height, array);
		}
		array.add(this);
		return height;
	}

	/**
	 * Comparison method for states.
	 *
	 * @param s the state to compare to
	 * @return 0 if states have same finality and same transitions a non null value
	 *         if states differ
	 */
	public int compareTo(State s) {
		if (s == null) {
			return -1;
		}
		// if the states are the same, no need to verify anything
		if (this == s)
			return 0;
		if (terminal != s.terminal) {
			// a final state is greater than a non final one
			return terminal ? 1 : -1;
		}
		// then, we check if they have the same transitions
		if (transitions == null) {
			if (s.transitions != null) {
				// first has no transition, second has => first is before
				return -1;
			}
			// none has transition => they are identical
			return 0;
		}
		if (s.transitions == null) {
			// first has transition, second has not => first is after
			return 1;
		}
		int l = transitions.length;
		if (l != s.transitions.length) {
			// if the first has less transitions, he is before the second
			return l - s.transitions.length;
		}
		for (int i = 0; i < l; i++) {
			int e = transitions[i].compareTo(s.transitions[i]);
			if (e != 0) {
				return e;
			}
		}
		// if we arrive here, then the two states are supposed to be
		// equivalent
		return 0;
	}

	/**
	 * This is an equals method that is consistent with compareTo
	 */
	public @Override boolean equals(Object o) {
		try {
			State s = (State) o;
			return (compareTo(s) == 0);
		} catch (ClassCastException e) {
			return false;
		}
	}

	public void addTransitionAtEnd(Transition t) {
		if (transitions == null) {
			transitions = new Transition[1];
			transitions[0] = t;
			return;
		}
		Transition[] tmp = new Transition[transitions.length + 1];
		System.arraycopy(transitions, 0, tmp, 0, transitions.length);
		tmp[transitions.length] = t;
		transitions = tmp;
	}

	/**
	 * @return a hash code depending on finality, output and transitions
	 */
	public int hashCode2() {
		int hash = terminal ? 1 : 0;
		if (transitions != null) {
			for (int i = 0; i < transitions.length; i++) {
				Transition t = transitions[i];
				hash = hash + (t.letter * 0xFFFF + (((t.destination.hashCode()) >> 2) * 101)) * (11 + 2 * i);
			}
		}
		return hash;
	}

}
