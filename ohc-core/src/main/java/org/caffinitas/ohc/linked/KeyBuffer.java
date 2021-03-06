/*
 *      Copyright (C) 2014 Robert Stupp, Koeln, Germany, robert-stupp.de
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.caffinitas.ohc.linked;

import java.util.Arrays;

final class KeyBuffer extends AbstractDataOutput
{
    private final byte[] array;
    private int p;
    private long hash;

    // TODO maybe move 'array' to off-heap - depends on actual use.
    // pro: reduces heap pressure
    // pro: harmonize code for key + value (de)serialization in DataIn/Output implementations
    // con: puts pressure on jemalloc

    KeyBuffer(int size)
    {
        array = new byte[size];
    }

    byte[] array()
    {
        return array;
    }

    int position()
    {
        return p;
    }

    int size()
    {
        return array.length;
    }

    long hash()
    {
        return hash;
    }

    KeyBuffer finish(Hasher hasher)
    {
        hash = hasher.hash(array);

        return this;
    }

    public void write(int b)
    {
        array[p++] = (byte) b;
    }

    public void write(byte[] b, int off, int len)
    {
        System.arraycopy(b, off, array, p, len);
        p += len;
    }

    public void writeShort(int v)
    {
        write((v >>> 8) & 0xFF);
        write(v & 0xFF);
    }

    public void writeChar(int v)
    {
        write((v >>> 8) & 0xFF);
        write(v & 0xFF);
    }

    public void writeInt(int v)
    {
        write((v >>> 24) & 0xFF);
        write((v >>> 16) & 0xFF);
        write((v >>> 8) & 0xFF);
        write(v & 0xFF);
    }

    public void writeLong(long v)
    {
        write((int) ((v >>> 56) & 0xFF));
        write((int) ((v >>> 48) & 0xFF));
        write((int) ((v >>> 40) & 0xFF));
        write((int) ((v >>> 32) & 0xFF));
        write((int) ((v >>> 24) & 0xFF));
        write((int) ((v >>> 16) & 0xFF));
        write((int) ((v >>> 8) & 0xFF));
        write((int) (v & 0xFF));
    }

    public void writeFloat(float v)
    {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeDouble(double v)
    {
        writeLong(Double.doubleToLongBits(v));
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyBuffer keyBuffer = (KeyBuffer) o;

        return Arrays.equals(array, keyBuffer.array);
    }

    public int hashCode()
    {
        return (int) hash;
    }
}
