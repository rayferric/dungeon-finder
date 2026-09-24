package io.xol.enklume.nbt;

import java.io.DataInputStream;
import java.io.IOException;

public class NBTLongArray extends NBTNamed {

    int size;
    public long[] data;

    @Override
    void feed(DataInputStream is) throws IOException {
        super.feed(is);
        size = is.read() << 24;
        size += is.read() << 16;
        size += is.read() << 8;
        size += is.read();

        data = new long[size];
        for (int i = 0; i < size; i++) {
            long v = (long) is.read() << 56;
            v += (long) is.read() << 48;
            v += (long) is.read() << 40;
            v += (long) is.read() << 32;
            v += (long) is.read() << 24;
            v += is.read() << 16;
            v += is.read() << 8;
            v += is.read();
            data[i] = v;
        }
    }
}
