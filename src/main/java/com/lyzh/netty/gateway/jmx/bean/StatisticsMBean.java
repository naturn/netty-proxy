package com.lyzh.netty.gateway.jmx.bean;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月22日 - 下午5:04:11
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public interface StatisticsMBean {

    public AtomicLong getOnline();
    
    public AtomicLong getOffline();
    
    public AtomicLong getTotal();
    
    public AtomicLong getInputStream();
    
    public AtomicLong getOutputStream();
    
    public SocketAddress getSocketAddress();
    
}
