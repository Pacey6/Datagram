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

package com.pacey6.net;

import com.pacey6.util.ByteUtil;
import com.pacey6.util.TUUID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pacey6 on 2021-04-02.
 * Homepage:https://pacey6.com/
 * Mail:support@pacey6.com
 */
class MultiDatagramSlice {
    static final int SIZE = 1024;
    static final int HEADER_SIZE = TUUID.BYTES + Integer.SIZE * 2 / Byte.SIZE;
    static final int DATA_SIZE = SIZE - HEADER_SIZE;

    final String id;
    final int index;
    final int count;
    final byte[] data;

    MultiDatagramSlice(byte[] bytes, int length) {
        if (length < HEADER_SIZE) throw new IndexOutOfBoundsException("Require " + HEADER_SIZE + " bytes at least");
        int intBytes = Integer.SIZE / Byte.SIZE;
        this.id = new String(bytes, 0, TUUID.BYTES);
        this.index = ByteUtil.bytesToInt(ByteUtil.subBytes(bytes, TUUID.BYTES, intBytes));
        this.count = ByteUtil.bytesToInt(ByteUtil.subBytes(bytes, TUUID.BYTES + intBytes, intBytes));
        this.data = ByteUtil.subBytes(bytes, TUUID.BYTES + intBytes * 2, length - TUUID.BYTES - intBytes * 2);
    }

    MultiDatagramSlice(String id, int index, int count, byte[] data) {
        this.id = id;
        this.index = index;
        this.count = count;
        this.data = data;
    }

    byte[] toBytes() {
        List<byte[]> blocks = new ArrayList<>();
        blocks.add(id.getBytes());
        blocks.add(ByteUtil.intToBytes(index));
        blocks.add(ByteUtil.intToBytes(count));
        blocks.add(data);
        return ByteUtil.merge(blocks);
    }
}
