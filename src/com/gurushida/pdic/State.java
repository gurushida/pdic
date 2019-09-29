package com.gurushida.pdic;

import java.util.*;

public class State {

	private boolean terminal;
	private Transition[] transitions;

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

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof State)) {
			return false;
		}

		State other = (State) o;
		if (other == this) {
			return true;
		}

		if (terminal != other.terminal) {
			return false;
		}

		if (transitions == null && other.transitions == null) {
			return true;
		}

		if ((transitions == null && other.transitions != null)
			|| (transitions != null && other.transitions == null)
			|| (transitions.length != other.transitions.length)) {
				return false;
		}

		int l = transitions.length;
		for (int i = 0; i < l; i++) {
			int e = transitions[i].compareTo(other.transitions[i]);
			if (e != 0) {
				return false;
			}
		}
		return true;
	}

	public boolean isTerminal() {
		return terminal;
	}

	public Transition[] getTransitions() {
		return transitions;
	}

}
