package com.gurushida.pdic;

public class Transition implements Comparable<Transition> {

	public final char letter;
	public State destination;

	public Transition(char letter, State destination) {
		this.letter = letter;
		this.destination = destination;
	}

	/**
	 * Comparison method for transitions.
	 *
	 * @param t the transition to compare to
	 * @return 0 if transitions have same letterand same destination
	 *         a non 0 value otherwise
	 */
	public int compareTo(Transition t) {
		if (t == null) {
			return -1;
		}
		if (letter != t.letter) {
			return letter - t.letter;
		}
		return destination.hashCode() - t.destination.hashCode();
	}

	/**
	 * Comparison method for transitions that does not take destination states into
	 * account.
	 *
	 * @param t the transition to compare to
	 * @return 0 if transitions have same letter and same output; a non null value
	 *         otherwise
	 */
	public int compareToWithoutDestination(Transition t) {
		if (t == null) {
			return -1;
		}
		return letter - t.letter;
	}

}
