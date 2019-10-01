package com.gurushida.pdic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import com.gurushida.pdic.BinaryUtil;
import com.gurushida.pdic.BinaryUtil.Offset;

public class BinaryDic {

    private File file;

    public BinaryDic(File file) {
        this.file = file;
    }

    public void decompress(File output) throws IOException {
        try (RandomAccessFile f = new RandomAccessFile(file, "r");
            FileChannel channel = f.getChannel();
            FileOutputStream outputStream = new FileOutputStream(output);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            int rootOffset = BinaryUtil.read(new Offset(0), buffer);

            StringBuilder sb = new StringBuilder();
            decompress(buffer, rootOffset, sb, 0, bufferedWriter);
        }
    }

    private void decompress(MappedByteBuffer buffer, int offset, StringBuilder sb, int wordLength,
            BufferedWriter bufferedWriter) throws IOException {
        Offset off = new Offset(offset);
        int value = BinaryUtil.decode(off, buffer);
        boolean terminal = (value & 1) == 1;
        int nTransitions = value >> 1;
        if (terminal) {
            bufferedWriter.append(sb.substring(0, wordLength));
            bufferedWriter.append('\n');
        }

        for (int i = 0; i < nTransitions; i++) {
            char c = (char) BinaryUtil.decode(off, buffer);
            int dstOffset = BinaryUtil.decode(off, buffer);
            sb.setLength(wordLength);
            sb.append(c);
            decompress(buffer, dstOffset, sb, wordLength + 1, bufferedWriter);
        }
    }

    public boolean contains(String pattern) throws IOException {
        if (pattern.isEmpty()) {
            return false;
        }
        try (RandomAccessFile f = new RandomAccessFile(file, "r");
            FileChannel channel = f.getChannel()) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            int rootOffset = BinaryUtil.read(new Offset(0), buffer);

            return contains(buffer, rootOffset, pattern, 0);
        }
    }

    private boolean contains(MappedByteBuffer buffer, int offset, String pattern, int posInPattern) {
        Offset off = new Offset(offset);
        int value = BinaryUtil.decode(off, buffer);
        boolean terminal = (value & 1) == 1;
        int nTransitions = value >> 1;
        if (posInPattern == pattern.length()) {
            return terminal;
        }

        for (int i = 0 ; i < nTransitions ; i++) {
            char c = (char)BinaryUtil.decode(off, buffer);
            int dstOffset = BinaryUtil.decode(off, buffer);
            if (c == pattern.charAt(posInPattern)) {
                return contains(buffer, dstOffset, pattern, posInPattern + 1);
            }
        }
        return false;
    }

}
