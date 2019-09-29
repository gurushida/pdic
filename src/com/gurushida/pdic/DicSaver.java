package com.gurushida.pdic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class DicSaver {

    private int currentOffset = 0;
    private HashMap<State, Integer> offsetMap = new HashMap<>();
    private File file;
    private final State root;

    public DicSaver(File file, State root) {
        this.file = file;
        this.root = root;
    }

    public void save() throws IOException {
        try (FileOutputStream out = new FileOutputStream(file);
            BufferedOutputStream output = new BufferedOutputStream(out)) {

            // Let's reserve 4 bytes to be able to write the offset of the initial
		    // state when we know it
		    output.write(0);
		    output.write(0);
		    output.write(0);
		    output.write(0);

		    this.currentOffset = 4;
            save(root, output);
        }

        // Now let's write the offset of the initial state at the beginning of the file
        try (RandomAccessFile out = new RandomAccessFile(file, "rw")) {
            int rootOffset = offsetMap.get(root);
            out.seek(0);
            out.write((rootOffset & 0xFF000000) >> 24);
            out.write((rootOffset & 0x00FF0000) >> 16);
            out.write((rootOffset & 0x0000FF00) >> 8);
            out.write(rootOffset & 0x000000FF);
        }
    }

    private void save(State state, OutputStream output) throws IOException {
        if (offsetMap.containsKey(state)) {
            // This state has already been saved
            return;
        }

        // Let's make sure that all the states reachable from this one are
        // saved first
        if (state.transitions != null) {
            for (Transition t : state.transitions) {
                save(t.destination, output);
            }
        }

        // Now we can save the state and the current offset will be its position
        offsetMap.put(state, currentOffset);

        // Let's encode on 4 bytes the number of transitions and whether or not
        // the state is terminal
        int nTransitions = state.transitions == null ? 0 : state.transitions.length;
        int stateCode = (nTransitions << 1) + (state.terminal ? 1 : 0);
        output.write((stateCode & 0xFF000000) >> 24);
        output.write((stateCode & 0x00FF0000) >> 16);
        output.write((stateCode & 0x0000FF00) >> 8);
        output.write(stateCode & 0x000000FF);
        currentOffset += 4;

        // Now let's encode the transitions
        if (nTransitions == 0) {
            return;
        }
        for (Transition t : state.transitions) {
            // 2 bytes for the character
            output.write((t.letter & 0xFF00) >> 8);
            output.write(t.letter & 0x00FF);

            // 3 bytes for the offset of the destination state
            int dstOffset = offsetMap.get(t.destination);
            output.write((dstOffset & 0xFF0000) >> 16);
            output.write((dstOffset & 0x00FF00) >> 8);
            output.write(dstOffset & 0x0000FF);

            currentOffset += 5;
        }
    }
}