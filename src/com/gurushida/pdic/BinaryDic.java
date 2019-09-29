package com.gurushida.pdic;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class BinaryDic {

    private File file;

    public BinaryDic(File file) {
        this.file = file;
    }

    private int readInt32Value(int offset, MappedByteBuffer buffer) {
        return ((buffer.get(offset) & 0xFF) << 24)
            | ((buffer.get(offset + 1) & 0xFF) << 16)
            | ((buffer.get(offset + 2) & 0xFF) << 8)
            | (buffer.get(offset + 3) & 0xFF);
    }

    private char readcharValue(int offset, MappedByteBuffer buffer) {
        return (char) (((buffer.get(offset) & 0xFF) << 8) | (buffer.get(offset + 1) & 0xFF));
    }

    private int read3BytesOffsetValue(int offset, MappedByteBuffer buffer) {
        return ((buffer.get(offset) & 0xFF) << 16)
            | ((buffer.get(offset + 1) & 0xFF) << 8)
            | (buffer.get(offset + 2) & 0xFF);
    }

    public void decompress(File output) throws IOException {
        try (RandomAccessFile f = new RandomAccessFile(file, "r");
            FileChannel channel = f.getChannel();
            FileOutputStream outputStream = new FileOutputStream(output);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            int rootOffset = readInt32Value(0, buffer);

            StringBuilder sb = new StringBuilder();
            uncompress(buffer, rootOffset, sb, 0, bufferedWriter);
        }
    }

    private void uncompress(MappedByteBuffer buffer, int offset, StringBuilder sb, int wordLength, BufferedWriter bufferedWriter) throws IOException {
        int value = readInt32Value(offset, buffer);
        boolean terminal = (value & 1) == 1;
        int nTransitions = value >> 1;
        if (terminal) {
            bufferedWriter.append(sb.substring(0, wordLength));
            bufferedWriter.append('\n');
        }

        offset += 4;
        for (int i = 0 ; i < nTransitions ; i++) {
            char c = readcharValue(offset, buffer);
            offset += 2;

            int dstOffset = read3BytesOffsetValue(offset, buffer);
            offset += 3;

            sb.setLength(wordLength);
            sb.append(c);
            uncompress(buffer, dstOffset, sb, wordLength + 1, bufferedWriter);
        }
    }
}