package com.gurushida.pdic;

import java.io.BufferedWriter;
import java.io.File;
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

            Offset offset = new Offset(0);

            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            int rootOffset = BinaryUtil.read(offset, buffer);

            StringBuilder sb = new StringBuilder();
            decompress(buffer, rootOffset, sb, 0, bufferedWriter);
        }
    }

    private void decompress(MappedByteBuffer buffer, int offset, StringBuilder sb, int wordLength, BufferedWriter bufferedWriter) throws IOException {
        Offset off = new Offset(offset);
        int value = BinaryUtil.decode(off, buffer);
        boolean terminal = (value & 1) == 1;
        int nTransitions = value >> 1;
        if (terminal) {
            bufferedWriter.append(sb.substring(0, wordLength));
            bufferedWriter.append('\n');
        }

        for (int i = 0 ; i < nTransitions ; i++) {
            char c = (char)BinaryUtil.decode(off, buffer);
            int dstOffset = BinaryUtil.decode(off, buffer);
            sb.setLength(wordLength);
            sb.append(c);
            decompress(buffer, dstOffset, sb, wordLength + 1, bufferedWriter);
        }
    }
}