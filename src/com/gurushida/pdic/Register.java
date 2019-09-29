package com.gurushida.pdic;

import java.util.*;

/**
 * An ad hoc hash table implementation whose purpose is to find
 * states considered equivalent because they have the same finality
 * and the same transitions.
 */
public class Register {

	private ArrayList<State>[] table;
	private int size;
	private int numberOfElements = 0;
	private int criticalSize;
	private float fillingRate = 0.75f;

	@SuppressWarnings("unchecked")
	public Register() {
		size = 16;
		criticalSize = (int) (size * fillingRate);
		table = new ArrayList[size];
	}

	/**
	 * This method resizes the hash table multiplying its size by 4. It rehashs all
	 * the elements.
	 */
	@SuppressWarnings("unchecked")
	private void resize() {
		int oldSize = size;
		size = size * 4;
		criticalSize = (int) (size * fillingRate);
		ArrayList<State>[] oldTable = table;
		table = new ArrayList[size];
		for (int i = 0; i < oldSize; i++) {
			if (oldTable[i] != null) {
				ArrayList<State> list = oldTable[i];
				for (State s : list) {
					getOrPut(s);
				}
			}
			oldTable[i] = null;
		}
		oldTable = null;
	}

	/**
	 * If the hash table contains an object o so that (s.equals(o)==true), it is
	 * returned. Otherwise, s is inserted in the hash table and returned
	 *
	 * @param s the state we want to find an equivalent in the hash table
	 * @return s or a state o so that (s.equals(o)==true)
	 */
	public State getOrPut(State s) {
		// We want a hash code >= 0. Note that we need to use a 2^n size
		int index = getSignature(s) & (size - 1);
		ArrayList<State> arrayList = table[index];
		if (arrayList == null) {
			table[index] = new ArrayList<>();
			table[index].add(s);
			if (numberOfElements++ >= criticalSize) {
				resize();
			}
			return s;
		}
		ArrayList<State> list = arrayList;
		for (State element : list) {
			if (element.equals(s)) {
				return element;
			}
		}
		// If we arrive here, we have to insert the element
		list.add(s);
		if (numberOfElements++ >= criticalSize) {
			resize();
		}
		return s;
	}

	/**
	 * Returns a hash code for the given state depending on finality and transitions.
	 * We don't want to make this code the regular hashCode() of the State class
	 * as it would slow down other operations.
	 */
	private int getSignature(State s) {
		int hash = s.isTerminal() ? 1 : 0;
		if (s.transitions != null) {
			for (int i = 0; i < s.transitions.length; i++) {
				Transition t = s.transitions[i];
				hash = hash + (t.letter * 0xFFFF + (((t.destination.hashCode()) >> 2) * 101)) * (11 + 2 * i);
			}
		}
		return hash;
	}

}
