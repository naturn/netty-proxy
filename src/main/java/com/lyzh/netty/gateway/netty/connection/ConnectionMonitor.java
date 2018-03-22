package com.lyzh.netty.gateway.netty.connection;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.lyzh.netty.gateway.common.Conversion;
import com.lyzh.netty.gateway.netty.RemoteAddressConnection;
import com.lyzh.netty.gateway.netty.bean.OfflineData;
import com.lyzh.netty.gateway.netty.handle.H2RedirctOutHandler;
import com.lyzh.netty.gateway.netty.listener.BufferChannelFutureListener;
import com.lyzh.netty.gateway.netty.repo.OfflineDataRepo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月22日 - 上午10:49:31
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class ConnectionMonitor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionMonitor.class);

    private final ChannelHandlerContext ctx;

    private Map<SocketAddress, Channel> channelFork;

    private Channel inboundChannel;

    private OfflineDataRepo dataRepo;

    public ConnectionMonitor(ChannelHandlerContext ctx, Map<SocketAddress, Channel> channelFork, Channel inboundChannel,
            OfflineDataRepo dataRepo) {
        this.ctx = ctx;
        this.channelFork = channelFork;
        this.inboundChannel = inboundChannel;
        this.dataRepo = dataRepo;
    }

    @Override
    public void run() {
        monitor();
    }

    private void monitor() {
        channelFork.entrySet().stream().filter(p -> {
            return !p.getValue().isActive();
        }).forEach(p -> {
            logger.info("{}={} reconnection .", p.getKey(), p.getValue().hashCode());
            ChannelFuture connection = RemoteAddressConnection.addListener(
                    RemoteAddressConnection.connection(p.getKey(), ctx, new H2RedirctOutHandler(inboundChannel)),
                    new BufferChannelFutureListener(inboundChannel));
            connection.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        int expired = p.getValue().hashCode();
                        send(expired, p.getKey(), connection);
                        channelFork.put(p.getKey(), connection.channel());
                    }
                }
            });

        });
    }

    private void send(int expired, SocketAddress socketAddress, ChannelFuture connection) {

        channelFork.put(socketAddress, connection.channel());
        Sort sort = new Sort(Direction.DESC, "sendingTime");
        PageRequest request = new PageRequest(0, 100, sort);
        // Page<OfflineData> result = dataRepo.findAll(Example.of(query),
        // request);
        Page<OfflineData> result = dataRepo.findBySessionId(expired, request);
        logger.info("Total size {}", result.getTotalElements());
        List<Integer> ids = new ArrayList<>();
        while (result.hasContent()) {

            result.getContent().forEach(p -> {
                ByteBuf temp = Unpooled.copiedBuffer(Conversion.hexStringToBytes(p.getMessage()));
                connection.channel().writeAndFlush(temp).addListener(new ChannelFutureListener() {

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            logger.info("Send Success.");
                            dataRepo.delete(p.getId());
                        }

                    }
                });
                ids.add(p.getId());
            });

            if (result.hasNext()) {
                result = dataRepo.findBySessionId(expired, result.nextPageable());
            }else {
                break;
            }

        }

    }

}
