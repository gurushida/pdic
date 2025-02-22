package com.gurushida.pdic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.IdentityHashMap;

public class DicSaver {

    private int currentOffset = 0;
    private IdentityHashMap<State, Integer> offsetMap = new IdentityHashMap<>();
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
            BinaryUtil.write(rootOffset, out);
        }
    }

    private void save(State state, OutputStream output) throws IOException {
        if (offsetMap.containsKey(state)) {
            // This state has already been saved
            return;
        }

        // Let's make sure that all the states reachable from this one are
        // saved first
        if (state.getTransitions() != null) {
            for (Transition t : state.getTransitions()) {
                save(t.destination, output);
            }
        }

        // Now we can save the state and the current offset will be its position
        offsetMap.put(state, currentOffset);

        // Let's encode on 4 bytes the number of transitions and whether or not
        // the state is terminal
        int nTransitions = state.getTransitions() == null ? 0 : state.getTransitions().length;
        int stateCode = (nTransitions << 1) + (state.isTerminal() ? 1 : 0);

        currentOffset += BinaryUtil.encode(stateCode, output);
        // Now let's encode the transitions
        if (nTransitions == 0) {
            return;
        }
        for (Transition t : state.getTransitions()) {
            currentOffset += BinaryUtil.encode(t.letter, output);

            int dstOffset = offsetMap.get(t.destination);
            currentOffset += BinaryUtil.encode(dstOffset, output);
        }
    }
}