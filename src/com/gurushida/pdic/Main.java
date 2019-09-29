package com.gurushida.pdic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

	public static void usage() {
		System.out.println("Usage:");
		System.out.println("   compress <in> <out>: given a UTF-8 text file <in>, saves into <out> a binary");
		System.out.println("                        representation of a minimal automton containing all the");
		System.out.println("                        non-empty lines of the input file");
		System.out.println("");
		System.out.println("   decompress <in> <out>: given a minimal automaton <in>, saves into <out> the sorted");
		System.out.println("                          duplicate-free list of the sequences contained in the automaton");
		System.out.println("                          as a UTF-8 text file");
		System.out.println("");
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length != 3) {
			usage();
			return;
		}

		try {
			if (args[0].equals("compress")) {
				compress(args);
			} else if (args[0].equals("decompress")) {
					decompress(args);
			} else {
				System.err.println("Invalid parameter: " + args[0]);
				System.err.println();
				usage();
				return;
			}
		} catch (OutOfMemoryError e) {
			System.err.println();
			System.err.println("OUT OF MEMORY ERROR");
			System.err.println("You must enlarge the heap space used by the java virtual machine");
			System.err.println("using the options -Xms and -Xmx as follows:");
			System.err.println();
			System.err.println("java -XmsMINm -XmxMAXm -jar pdic.jar ...");
			System.err.println();
			System.err.println("where MIN and MAX are respectively the initial and maximum heap");
			System.err.println("sizes in megabytes.");
		}
	}

	private static void compress(String args[]) throws FileNotFoundException, IOException {
		if (args.length < 3) {
			System.err.println("Invalid parameter: "+args[0]);
			System.err.println();
			usage();
			return;
		}
		Automaton automaton = new Automaton();
		automaton.load(new File(args[1]));
		automaton.minimize();
		automaton.save(new File(args[2]));
	}

	private static void decompress(String args[]) throws FileNotFoundException, IOException {
		if (args.length < 3) {
			System.err.println("Invalid parameter: "+args[0]);
			System.err.println();
			usage();
			return;
		}
		BinaryDic dic = new BinaryDic(new File(args[1]));
		dic.decompress(new File(args[2]));
	}
}
