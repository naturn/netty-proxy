package com.lyzh.netty.gateway.netty.handle;

import java.net.SocketAddress;
import java.util.List;

import com.lyzh.netty.gateway.bean.Statistics;
import com.lyzh.netty.gateway.netty.repo.OfflineDataRepo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Author Naturn
 * 
 * @Date 2018年1月30日 - 上午9:01:10
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class HexDumpProxyInitializer extends ChannelInitializer<SocketChannel> {
    
    private final List<SocketAddress> outputs;
    
    private OfflineDataRepo offlineDataRepo;
    
    private Statistics statistics;
    
    public HexDumpProxyInitializer(List<SocketAddress> outputs) {
        this.outputs = outputs;
        
    }
    
    public HexDumpProxyInitializer(List<SocketAddress> outputs, OfflineDataRepo offlineDataRepo,Statistics statistics) {
        this.outputs = outputs;
        this.offlineDataRepo = offlineDataRepo;
        this.statistics = statistics;
    }
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {       
        
        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
        if(null != offlineDataRepo) {
            ch.pipeline().addLast(new CounterHandler(statistics),new H2RedirctHandler(offlineDataRepo, statistics, outputs));
        }else {
            ch.pipeline().addLast(new CounterHandler(statistics),new RedirctHandler(outputs));
        }
    }

}
