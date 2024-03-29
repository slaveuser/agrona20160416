/*
 * Copyright 2014-2015 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agrona;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.agrona.BitUtil.*;
import static org.agrona.UnsafeAccess.UNSAFE;
import static org.agrona.BufferUtil.*;

/**
 * Expandable {@link MutableDirectBuffer} that is backed by an array. When values are put into the buffer beyond its
 * current length, then it will be expanded to accommodate the resulting position for the end of the value.
 *
 * Put operations will expand the capacity as necessary up to {@link #MAX_ARRAY_LENGTH}. Get operations will throw
 * a {@link IndexOutOfBoundsException} if past current capacity.
 *
 * Note: this class has a natural ordering that is inconsistent with equals.
 * Types my be different but equal on buffer contents.
 */
public class ExpandableArrayBuffer implements MutableDirectBuffer
{
    /**
     * Maximum length to which the underlying array can grow. Some JVMs set bits in the last few bytes.
     */
    public static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    /**
     * Initial capacity of the array from which it will double in size on each expansion.
     */
    public static final int INITIAL_CAPACITY = 128;

    private byte[] byteArray;

    /**
     * Create an {@link ExpandableArrayBuffer} with an initial length of {@link #INITIAL_CAPACITY}.
     */
    public ExpandableArrayBuffer()
    {
        this(INITIAL_CAPACITY);
    }

    /**
     * Create an {@link ExpandableArrayBuffer} with a provided initial length.
     *
     * @param initialCapacity of the backing array.
     */
    public ExpandableArrayBuffer(final int initialCapacity)
    {
        byteArray = new byte[initialCapacity];
    }

    public void wrap(final byte[] buffer)
    {
        throw new UnsupportedOperationException();
    }

    public void wrap(final byte[] buffer, final int offset, final int length)
    {
        throw new UnsupportedOperationException();
    }

    public void wrap(final ByteBuffer buffer)
    {
        throw new UnsupportedOperationException();
    }

    public void wrap(final ByteBuffer buffer, final int offset, final int length)
    {
        throw new UnsupportedOperationException();
    }

    public void wrap(final DirectBuffer buffer)
    {
        throw new UnsupportedOperationException();
    }

    public void wrap(final DirectBuffer buffer, final int offset, final int length)
    {
        throw new UnsupportedOperationException();
    }

    public void wrap(final long address, final int length)
    {
        throw new UnsupportedOperationException();
    }

    public long addressOffset()
    {
        return ARRAY_BASE_OFFSET;
    }

    public byte[] byteArray()
    {
        return byteArray;
    }

    public ByteBuffer byteBuffer()
    {
        return null;
    }

    public void setMemory(final int index, final int length, final byte value)
    {
        Arrays.fill(byteArray, index, index + length, value);
    }

    public int capacity()
    {
        return byteArray.length;
    }

    public boolean isExpandable()
    {
        return true;
    }

    public void checkLimit(final int limit)
    {
        ensureCapacity(limit, SIZE_OF_BYTE);
    }

    ///////////////////////////////////////////////////////////////////////////

    public long getLong(final int index, final ByteOrder byteOrder)
    {
        boundsCheck0(index, SIZE_OF_LONG);

        long bits = UNSAFE.getLong(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            bits = Long.reverseBytes(bits);
        }

        return bits;
    }

    public void putLong(final int index, final long value, final ByteOrder byteOrder)
    {
        ensureCapacity(index, SIZE_OF_LONG);

        long bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            bits = Long.reverseBytes(bits);
        }

        UNSAFE.putLong(byteArray, ARRAY_BASE_OFFSET + index, bits);
    }

    public long getLong(final int index)
    {
        boundsCheck0(index, SIZE_OF_LONG);

        return UNSAFE.getLong(byteArray, ARRAY_BASE_OFFSET + index);
    }

    public void putLong(final int index, final long value)
    {
        ensureCapacity(index, SIZE_OF_LONG);

        UNSAFE.putLong(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    public int getInt(final int index, final ByteOrder byteOrder)
    {
        boundsCheck0(index, SIZE_OF_INT);

        int bits = UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            bits = Integer.reverseBytes(bits);
        }

        return bits;
    }

    public void putInt(final int index, final int value, final ByteOrder byteOrder)
    {
        ensureCapacity(index, SIZE_OF_INT);

        int bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            bits = Integer.reverseBytes(bits);
        }

        UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, bits);
    }

    public int getInt(final int index)
    {
        boundsCheck0(index, SIZE_OF_INT);

        return UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);
    }

    public void putInt(final int index, final int value)
    {
        ensureCapacity(index, SIZE_OF_INT);

        UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    public double getDouble(final int index, final ByteOrder byteOrder)
    {
        boundsCheck0(index, SIZE_OF_DOUBLE);

        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            final long bits = UNSAFE.getLong(byteArray, ARRAY_BASE_OFFSET + index);
            return Double.longBitsToDouble(Long.reverseBytes(bits));
        }
        else
        {
            return UNSAFE.getDouble(byteArray, ARRAY_BASE_OFFSET + index);
        }
    }

    public void putDouble(final int index, final double value, final ByteOrder byteOrder)
    {
        ensureCapacity(index, SIZE_OF_DOUBLE);

        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            final long bits = Long.reverseBytes(Double.doubleToRawLongBits(value));
            UNSAFE.putLong(byteArray, ARRAY_BASE_OFFSET + index, bits);
        }
        else
        {
            UNSAFE.putDouble(byteArray, ARRAY_BASE_OFFSET + index, value);
        }
    }

    public double getDouble(final int index)
    {
        boundsCheck0(index, SIZE_OF_DOUBLE);

        return UNSAFE.getDouble(byteArray, ARRAY_BASE_OFFSET + index);
    }

    public void putDouble(final int index, final double value)
    {
        ensureCapacity(index, SIZE_OF_DOUBLE);

        UNSAFE.putDouble(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    public float getFloat(final int index, final ByteOrder byteOrder)
    {
        boundsCheck0(index, SIZE_OF_FLOAT);

        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            final int bits = UNSAFE.getInt(byteArray, ARRAY_BASE_OFFSET + index);
            return Float.intBitsToFloat(Integer.reverseBytes(bits));
        }
        else
        {
            return UNSAFE.getFloat(byteArray, ARRAY_BASE_OFFSET + index);
        }
    }

    public void putFloat(final int index, final float value, final ByteOrder byteOrder)
    {
        ensureCapacity(index, SIZE_OF_FLOAT);

        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            final int bits = Integer.reverseBytes(Float.floatToRawIntBits(value));
            UNSAFE.putInt(byteArray, ARRAY_BASE_OFFSET + index, bits);
        }
        else
        {
            UNSAFE.putFloat(byteArray, ARRAY_BASE_OFFSET + index, value);
        }
    }

    public float getFloat(final int index)
    {
        boundsCheck0(index, SIZE_OF_FLOAT);

        return UNSAFE.getFloat(byteArray, ARRAY_BASE_OFFSET + index);
    }

    public void putFloat(final int index, final float value)
    {
        ensureCapacity(index, SIZE_OF_FLOAT);

        UNSAFE.putFloat(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    public short getShort(final int index, final ByteOrder byteOrder)
    {
        boundsCheck0(index, SIZE_OF_SHORT);

        short bits = UNSAFE.getShort(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            bits = Short.reverseBytes(bits);
        }

        return bits;
    }

    public void putShort(final int index, final short value, final ByteOrder byteOrder)
    {
        ensureCapacity(index, SIZE_OF_SHORT);

        short bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            bits = Short.reverseBytes(bits);
        }

        UNSAFE.putShort(byteArray, ARRAY_BASE_OFFSET + index, bits);
    }

    public short getShort(final int index)
    {
        boundsCheck0(index, SIZE_OF_SHORT);

        return UNSAFE.getShort(byteArray, ARRAY_BASE_OFFSET + index);
    }

    public void putShort(final int index, final short value)
    {
        ensureCapacity(index, SIZE_OF_SHORT);

        UNSAFE.putShort(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    public byte getByte(final int index)
    {
        return byteArray[index];
    }

    public void putByte(final int index, final byte value)
    {
        ensureCapacity(index, SIZE_OF_BYTE);

        byteArray[index] = value;
    }

    public void getBytes(final int index, final byte[] dst)
    {
        getBytes(index, dst, 0, dst.length);
    }

    public void getBytes(final int index, final byte[] dst, final int offset, final int length)
    {
        System.arraycopy(byteArray, index, dst, offset, length);
    }

    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length)
    {
        dstBuffer.putBytes(dstIndex, this, index, length);
    }

    public void getBytes(final int index, final ByteBuffer dstBuffer, final int length)
    {
        final int dstOffset = dstBuffer.position();
        getBytes(index, dstBuffer, dstOffset, length);
        dstBuffer.position(dstOffset + length);
    }

    public void getBytes(final int index, final ByteBuffer dstBuffer, final int dstOffset, final int length)
    {
        boundsCheck0(index, length);
        BufferUtil.boundsCheck(dstBuffer, dstOffset, length);

        final byte[] dstByteArray;
        final long dstBaseOffset;
        if (dstBuffer.hasArray())
        {
            dstByteArray = dstBuffer.array();
            dstBaseOffset = ARRAY_BASE_OFFSET + dstBuffer.arrayOffset();
        }
        else
        {
            dstByteArray = null;
            dstBaseOffset = address(dstBuffer);
        }

        UNSAFE.copyMemory(byteArray, ARRAY_BASE_OFFSET + index, dstByteArray, dstBaseOffset + dstOffset, length);
    }

    public void putBytes(final int index, final byte[] src)
    {
        putBytes(index, src, 0, src.length);
    }

    public void putBytes(final int index, final byte[] src, final int offset, final int length)
    {
        ensureCapacity(index, length);
        System.arraycopy(src, offset, byteArray, index, length);
    }

    public void putBytes(final int index, final ByteBuffer srcBuffer, final int length)
    {
        final int srcIndex = srcBuffer.position();
        putBytes(index, srcBuffer, srcIndex, length);
        srcBuffer.position(srcIndex + length);
    }

    public void putBytes(final int index, final ByteBuffer srcBuffer, final int srcIndex, final int length)
    {
        ensureCapacity(index, length);
        BufferUtil.boundsCheck(srcBuffer, srcIndex, length);

        final byte[] srcByteArray;
        final long srcBaseOffset;
        if (srcBuffer.hasArray())
        {
            srcByteArray = srcBuffer.array();
            srcBaseOffset = ARRAY_BASE_OFFSET + srcBuffer.arrayOffset();
        }
        else
        {
            srcByteArray = null;
            srcBaseOffset = address(srcBuffer);
        }

        UNSAFE.copyMemory(srcByteArray, srcBaseOffset + srcIndex, byteArray, ARRAY_BASE_OFFSET + index, length);
    }

    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length)
    {
        ensureCapacity(index, length);
        srcBuffer.boundsCheck(srcIndex, length);

        UNSAFE.copyMemory(
            srcBuffer.byteArray(),
            srcBuffer.addressOffset() + srcIndex,
            byteArray,
            ARRAY_BASE_OFFSET + index,
            length);
    }

    ///////////////////////////////////////////////////////////////////////////

    public char getChar(final int index, final ByteOrder byteOrder)
    {
        boundsCheck0(index, SIZE_OF_SHORT);

        char bits = UNSAFE.getChar(byteArray, ARRAY_BASE_OFFSET + index);
        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            bits = (char)Short.reverseBytes((short)bits);
        }

        return bits;
    }

    public void putChar(final int index, final char value, final ByteOrder byteOrder)
    {
        ensureCapacity(index, SIZE_OF_CHAR);

        char bits = value;
        if (NATIVE_BYTE_ORDER != byteOrder)
        {
            bits = (char)Short.reverseBytes((short)bits);
        }

        UNSAFE.putChar(byteArray, ARRAY_BASE_OFFSET + index, bits);
    }

    public char getChar(final int index)
    {
        boundsCheck0(index, SIZE_OF_CHAR);

        return UNSAFE.getChar(byteArray, ARRAY_BASE_OFFSET + index);
    }

    public void putChar(final int index, final char value)
    {
        ensureCapacity(index, SIZE_OF_CHAR);

        UNSAFE.putChar(byteArray, ARRAY_BASE_OFFSET + index, value);
    }

    ///////////////////////////////////////////////////////////////////////////

    public String getStringUtf8(final int index)
    {
        final int length = getInt(index);

        return getStringUtf8(index, length);
    }

    public String getStringUtf8(final int index, final ByteOrder byteOrder)
    {
        final int length = getInt(index, byteOrder);

        return getStringUtf8(index, length);
    }

    public String getStringUtf8(final int index, final int length)
    {
        final byte[] stringInBytes = new byte[length];
        getBytes(index + SIZE_OF_INT, stringInBytes);

        return new String(stringInBytes, UTF_8);
    }

    public int putStringUtf8(final int index, final String value)
    {
        return putStringUtf8(index, value, Integer.MAX_VALUE);
    }

    public int putStringUtf8(final int index, final String value, final ByteOrder byteOrder)
    {
        return putStringUtf8(index, value, byteOrder, Integer.MAX_VALUE);
    }

    public int putStringUtf8(final int index, final String value, final int maxEncodedSize)
    {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        if (bytes.length > maxEncodedSize)
        {
            throw new IllegalArgumentException("Encoded string larger than maximum size: " + maxEncodedSize);
        }

        putInt(index, bytes.length);
        putBytes(index + SIZE_OF_INT, bytes);

        return SIZE_OF_INT + bytes.length;
    }

    public int putStringUtf8(final int index, final String value, final ByteOrder byteOrder, final int maxEncodedSize)
    {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        if (bytes.length > maxEncodedSize)
        {
            throw new IllegalArgumentException("Encoded string larger than maximum size: " + maxEncodedSize);
        }

        putInt(index, bytes.length, byteOrder);
        putBytes(index + SIZE_OF_INT, bytes);

        return SIZE_OF_INT + bytes.length;
    }

    public String getStringWithoutLengthUtf8(final int index, final int length)
    {
        final byte[] stringInBytes = new byte[length];
        getBytes(index, stringInBytes);

        return new String(stringInBytes, UTF_8);
    }

    public int putStringWithoutLengthUtf8(final int index, final String value)
    {
        final byte[] bytes = value != null ? value.getBytes(UTF_8) : NULL_BYTES;
        putBytes(index, bytes);

        return bytes.length;
    }

    ///////////////////////////////////////////////////////////////////////////

    private void ensureCapacity(final int index, final int length)
    {
        if (index < 0)
        {
            throw new IndexOutOfBoundsException("index cannot be negative: index=" + index);
        }

        final long resultingPosition = index + (long)length;
        final int currentArrayLength = byteArray.length;
        if (resultingPosition > currentArrayLength)
        {
            if (currentArrayLength >= MAX_ARRAY_LENGTH)
            {
                throw new IndexOutOfBoundsException(String.format(
                    "index=%d, length=%d, maxCapacity=%d", index, length, MAX_ARRAY_LENGTH));
            }

            byteArray = Arrays.copyOf(byteArray, calculateExpansion(currentArrayLength, (int)resultingPosition));
        }
    }

    private int calculateExpansion(final int currentLength, final int requiredLength)
    {
        long value = currentLength;

        while (value < requiredLength)
        {
            value = value * 2;

            if (value > Integer.MAX_VALUE)
            {
                value = MAX_ARRAY_LENGTH;
            }
        }

        return (int)value;
    }

    private void boundsCheck0(final int index, final int length)
    {
        final int currentArrayLength = byteArray.length;
        final long resultingPosition = index + (long)length;
        if (index < 0 || resultingPosition > currentArrayLength)
        {
            throw new IndexOutOfBoundsException(String.format(
                "index=%d, length=%d, capacity=%d", index, length, currentArrayLength));
        }
    }

    public void boundsCheck(final int index, final int length)
    {
        boundsCheck0(index, length);
    }

    ///////////////////////////////////////////////////////////////////////////

    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        final ExpandableArrayBuffer that = (ExpandableArrayBuffer)obj;

        return Arrays.equals(this.byteArray, that.byteArray);

    }

    public int hashCode()
    {
        return Arrays.hashCode(byteArray);
    }

    public int compareTo(final DirectBuffer that)
    {
        final int thisCapacity = this.capacity();
        final int thatCapacity = that.capacity();
        final byte[] thisByteArray = this.byteArray;
        final byte[] thatByteArray = that.byteArray();
        final long thisOffset = this.addressOffset();
        final long thatOffset = that.addressOffset();

        for (int i = 0, length = Math.min(thisCapacity, thatCapacity); i < length; i++)
        {
            final int cmp = Byte.compare(
                UNSAFE.getByte(thisByteArray, thisOffset + i),
                UNSAFE.getByte(thatByteArray, thatOffset + i));

            if (0 != cmp)
            {
                return cmp;
            }
        }

        if (thisCapacity != thatCapacity)
        {
            return thisCapacity - thatCapacity;
        }

        return 0;
    }
}
