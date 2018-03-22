package com.lyzh.netty.gateway.file;

import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月16日 - 下午3:19:29
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class BufferFile {

    private Path path;

    private SocketAddress socket;

    private String bufferId;

    private BlockingQueue<String> data;

    public SocketAddress getSocket() {
        return socket;
    }

    public void setSocket(SocketAddress socket) {
        this.socket = socket;
    }

    public String getBufferId() {
        return bufferId;
    }

    public void setBufferId(String bufferId) {
        this.bufferId = bufferId;
    }

    public BlockingQueue<String> getData() {
        return data;
    }

    public void setData(BlockingQueue<String> data) {
        this.data = data;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

}
