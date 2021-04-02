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

import com.pacey6.util.CacheMap;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Map;

/**
 * Created by pacey6 on 2021-04-02.
 * Homepage:https://pacey6.com/
 * Mail:support@pacey6.com
 */
public class MultiDatagramSocket<T extends DatagramSocket> implements Closeable {
    private static final int CACHE_CAPACITY = 16;

    private final T datagramSocket;
    private final Map<String, MultiDatagramPacket> cacheMap = new CacheMap<>(CACHE_CAPACITY);
    private final byte[] buf = new byte[MultiDatagramSlice.SIZE];
    private final DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

    public MultiDatagramSocket(T datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void close() {
        datagramSocket.close();
        cacheMap.clear();
    }

    public T getDatagramSocket() {
        return datagramSocket;
    }

    public synchronized MultiDatagramPacket receive() throws IOException {
        while (true) {
            datagramSocket.receive(datagramPacket);
            MultiDatagramSlice multiDatagramSlice = new MultiDatagramSlice(buf, datagramPacket.getLength());
            MultiDatagramPacket multiDatagramPacket = cacheMap.get(multiDatagramSlice.id);
            if (null == multiDatagramPacket) {
                multiDatagramPacket = new MultiDatagramPacket(datagramPacket.getAddress(), datagramPacket.getPort());
                cacheMap.put(multiDatagramSlice.id, multiDatagramPacket);
            }
            if (multiDatagramPacket.append(multiDatagramSlice)) {
                cacheMap.remove(multiDatagramSlice.id);
                return multiDatagramPacket;
            }
        }
    }

    public void send(MultiDatagramPacket packet) throws IOException {
        List<MultiDatagramSlice> slices = packet.slices();
        for (MultiDatagramSlice multiDatagramSlice : slices) {
            byte[] buf = multiDatagramSlice.toBytes();
            datagramSocket.send(new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort()));
        }
    }
}
