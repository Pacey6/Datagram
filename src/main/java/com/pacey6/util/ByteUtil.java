/*
 * MIT License
 *
 * Copyright (c) 2021 Pacey Lau
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.pacey6.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pacey6 on 2021-04-02.
 * Homepage:https://pacey6.com/
 * Mail:support@pacey6.com
 */
public abstract class ByteUtil {
    public static byte[] merge(List<byte[]> bytes) {
        int count = 0;
        for (byte[] b : bytes) {
            count += b.length;
        }
        byte[] data = new byte[count];
        int offset = 0;
        for (byte[] b : bytes) {
            System.arraycopy(b, 0, data, offset, b.length);
            offset += b.length;
        }
        return data;
    }

    public static List<byte[]> split(byte[] data, int sliceLength) {
        return split(data, 0, sliceLength);
    }

    public static List<byte[]> split(byte[] data, int dataOffset, int sliceLength) {
        return split(data, dataOffset, data.length, sliceLength);
    }

    public static List<byte[]> split(byte[] data, int dataOffset, int dataLength, int sliceLength) {
        int count = dataLength / sliceLength;
        if (dataLength % sliceLength > 0) count ++;
        List<byte[]> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int offset = sliceLength * i + dataOffset;
            int length = Math.max(0, Math.min((dataOffset + dataLength) - offset, sliceLength));
            byte[] slice = new byte[length];
            System.arraycopy(data, offset, slice, 0, length);
            result.add(slice);
        }
        return result;
    }

    public static byte[] subBytes(byte[] bytes, int offset, int length) {
        byte[] subBytes = new byte[length];
        System.arraycopy(bytes, offset, subBytes, 0, length);
        return subBytes;
    }

    public static byte[] intToBytes(int value) {
        byte[] result = new byte[4];
        result[0] = (byte) (value >> 24);
        result[1] = (byte) (value >> 16);
        result[2] = (byte) (value >> 8);
        result[3] = (byte) (value);
        return result;
    }

    public static int bytesToInt(byte[] value) {
        return ((value[0] & 0xFF) << 24) | ((value[1] & 0xFF) << 16) | ((value[2] & 0xFF) << 8) | (value[3] & 0xFF);
    }
}
