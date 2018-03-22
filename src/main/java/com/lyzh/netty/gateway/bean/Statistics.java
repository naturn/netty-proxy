package com.lyzh.netty.gateway.bean;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月16日 - 下午9:05:29
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

@Entity
public class Statistics {

    @Id
    private int id;

    private SocketAddress socketAddress;

    private final AtomicLong online = new AtomicLong(0);

    private final AtomicLong offline = new AtomicLong(0);

    private final AtomicLong total = new AtomicLong(0);

    private final AtomicLong inputStream = new AtomicLong(0);

    private final AtomicLong outputStream = new AtomicLong(0);

    public Statistics(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public long incrementOnline() {
        return online.incrementAndGet();
    }

    public long incrementOffline() {
        return offline.incrementAndGet();
    }

    public long incrementTotal() {
        return total.incrementAndGet();
    }

    public long decrementOnline() {
        return online.decrementAndGet();
    }

    public long decrementOffline() {
        return offline.decrementAndGet();
    }

    public long decrementTotal() {
        return total.decrementAndGet();
    }

    public long increaseInput(long input) {
        return inputStream.addAndGet(input);
    }

    public long reduceInput(long input) {
        return inputStream.addAndGet(-input);
    }

    public long increaseOutput(long output) {
        return outputStream.addAndGet(output);
    }

    public long reduceOutput(long output) {
        return outputStream.addAndGet(-output);
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }
   
}
