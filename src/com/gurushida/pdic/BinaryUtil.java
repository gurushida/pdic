package com.gurushida.pdic;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;

public class BinaryUtil {

    public static class Offset {
        public int value;

        public Offset(int value) {
            this.value = value;
        }
    }

    /**
     * Writes the 4 bytes representing the given long to the given output.
     */
    public static void write(int n, DataOutput output) throws IOException {
        output.write((int)((n & 0xFF000000) >> 24));
        output.write((int)((n & 0xFF0000) >> 16));
        output.write((int)((n & 0xFF00) >> 8));
        output.write((int)(n  & 0xFF));
    }

    /**
     * Reads a 4-bytes value from the given buffer at the given offset.
     * The offset is updated by the number of bytes read.
     */
    public static int read(Offset offset, MappedByteBuffer buffer) {
        int n = ((buffer.get(offset.value) & 0xFF) << 24)
            | ((buffer.get(offset.value + 1) & 0xFF) << 16)
            | ((buffer.get(offset.value + 2) & 0xFF) << 8)
            | (buffer.get(offset.value + 3) & 0xFF);
        offset.value += 4;
        return n;
    }

    /**
     * Encodes the given value on one or more bytes and writes it to the given
     * output stream.
     * @param n The value to encode
     * @param output The output stream to write the encoded value into
     * @return The number of bytes written in the output stream
     */
    public static int encode(int n, OutputStream output) throws IOException {
        if (n < (1 << 7)) {
            output.write((int)n);
            return 1;
        } else if (n < (1 << 14)) {
            int a = (int)((n >> 7) | 128);
            int b = (int)(n & 127);
            output.write(a);
            output.write(b);
            return 2;
        } else if (n < (1 << 21)) {
            int a = (int)((n >> 14) | 128);
            int b = (int)(((n >> 7) & 0XFF) | 128);
            int c = (int)(n & 127);
            output.write(a);
            output.write(b);
            output.write(c);
            return 3;
        } else if (n < (1 << 28)) {
            int a = (int)((n >> 21) | 128);
            int b = (int)(((n >> 14) & 0XFF) | 128);
            int c = (int)(((n >> 7) & 0XFF) | 128);
            int d = (int)(n & 127);
            output.write(a);
            output.write(b);
            output.write(c);
            output.write(d);
            return 4;
        } else {
            int a = (int)((n >> 28) | 128);
            int b = (int)(((n >> 21) & 0XFF) | 128);
            int c = (int)(((n >> 14) & 0XFF) | 128);
            int d = (int)(((n >> 7) & 0XFF) | 128);
            int e = (int)(n & 127);
            output.write(a);
            output.write(b);
            output.write(c);
            output.write(d);
            output.write(e);
            return 5;
        }
    }

    /**
     * Decodes a value from the given buffer at the given position.
     * The offset is updated by the number of bytes read.
     */
    public static int decode(Offset offset, MappedByteBuffer buffer) {
        int n = 0;
        int v;

        do {
            v = buffer.get(offset.value++) & 0xFF;
            n = (n << 7) | (v & 127);
        } while (v >= 128);
        return n;
    }

}