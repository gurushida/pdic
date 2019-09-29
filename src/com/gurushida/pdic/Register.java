package com.gurushida.pdic;

import java.util.*;

public class Register {

	private ArrayList[] table;
	private int size;
	private int numberOfElements = 0;
	private int criticalSize;
	private float fillingRate = 0.75f;

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
		ArrayList[] oldTable = table;
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

	@SuppressWarnings("unchecked")
	public State getOrPut(State s) {
		/* We want a hash code >0. Note that we need to use a 2^n size */
		int index = s.hashCode2() & (size - 1);
		ArrayList arrayList = table[index];
		if (arrayList == null) {
			table[index] = new ArrayList();
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
		// if we arrive here, we have to insert the element
		list.add(s);
		if (numberOfElements++ >= criticalSize) {
			resize();
		}
		return s;
	}

}
