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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pacey6 on 2021-04-02.
 * Homepage:https://pacey6.com/
 * Mail:support@pacey6.com
 */
public class MultiDatagramPacket {
    private String id;
    private byte[] data;
    private InetAddress address;
    private int port;
    private Map<Integer, MultiDatagramSlice> sliceMap;

    MultiDatagramPacket(InetAddress address, int port) {
        setAddress(address);
        setPort(port);
    }

    public MultiDatagramPacket(byte[] buf, SocketAddress address) {
        setId(TUUID.randomTUUID());
        setData(buf);
        setSocketAddress(address);
    }

    public MultiDatagramPacket(byte[] buf, InetAddress address, int port) {
        setId(TUUID.randomTUUID());
        setData(buf);
        setAddress(address);
        setPort(port);
    }

    synchronized String getId() {
        return id;
    }

    synchronized void setId(String id) {
        this.id = id;
    }

    public synchronized InetAddress getAddress() {
        return address;
    }

    public synchronized void setAddress(InetAddress address) {
        this.address = address;
    }

    public synchronized byte[] getData() {
        return this.data;
    }

    public synchronized void setData(byte[] data) {
        this.data = data;
    }

    public synchronized int getPort() {
        return this.port;
    }

    public synchronized void setPort(int port) {
        this.port = port;
    }

    public synchronized SocketAddress getSocketAddress() {
        return new InetSocketAddress(getAddress(), getPort());
    }

    public synchronized void setSocketAddress(SocketAddress address) {
        if (!(address instanceof InetSocketAddress))
            throw new IllegalArgumentException("unsupported address type");
        InetSocketAddress inetSocketAddress = (InetSocketAddress) address;
        if (inetSocketAddress.isUnresolved())
            throw new IllegalArgumentException("unresolved address");
        setAddress(inetSocketAddress.getAddress());
        setPort(inetSocketAddress.getPort());
    }

    synchronized boolean append(MultiDatagramSlice slice) {
        if (1 == slice.count) {
            setId(slice.id);
            setData(slice.data);
            return true;
        }
        if (null == sliceMap) {
            sliceMap = new HashMap<>();
            sliceMap.put(slice.index, slice);
            setId(slice.id);
            return false;
        }
        if (slice.id.equals(getId())) {
            sliceMap.put(slice.index, slice);
            if (sliceMap.size() >= slice.count) {
                List<byte[]> sliceData = new ArrayList<>();
                for (int i = 0; i < slice.count; i++) {
                    sliceData.add(sliceMap.get(i).data);
                }
                setData(ByteUtil.merge(sliceData));
                return true;
            }
        }
        return false;
    }

    synchronized List<MultiDatagramSlice> slices() {
        List<MultiDatagramSlice> slices = new ArrayList<>();
        if (null != data) {
            List<byte[]> bytes = ByteUtil.split(data, MultiDatagramSlice.DATA_SIZE);
            for (int i = 0, count = bytes.size(); i < count; i++) {
                slices.add(new MultiDatagramSlice(getId(), i, count, bytes.get(i)));
            }
        }
        return slices;
    }
}
