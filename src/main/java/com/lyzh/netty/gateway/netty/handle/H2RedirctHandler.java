package com.lyzh.netty.gateway.netty.handle;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyzh.netty.gateway.bean.Statistics;
import com.lyzh.netty.gateway.netty.RemoteAddressConnection;
import com.lyzh.netty.gateway.netty.connection.ConnectionMonitor;
import com.lyzh.netty.gateway.netty.listener.H2DBChannelFutureListener;
import com.lyzh.netty.gateway.netty.repo.OfflineDataRepo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月20日 - 上午11:09:21
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class H2RedirctHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(H2RedirctHandler.class);

    private OfflineDataRepo offlineDataRepo;

    private final List<SocketAddress> outputs;

    private Channel inboundChannel;

    // As we use inboundChannel.eventLoop() when building the Bootstrap this
    // does not need to be volatile as
    // the outboundChannel will use the same EventLoop (and therefore Thread) as
    // the inboundChannel.

    // private List<Channel> outBoundChannels;
    // 1:1
    private Map<SocketAddress, Channel> channelFork;

    private Statistics statistics;

    public H2RedirctHandler(OfflineDataRepo offlinieDataRepo, Statistics statistics, List<SocketAddress> outputs) {
        this.outputs = outputs;
        this.offlineDataRepo = offlinieDataRepo;
        this.statistics = statistics;
        this.channelFork = new HashMap<>(outputs.size());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        inboundChannel = ctx.channel();
        outputs.forEach(socketAddress -> {
            ChannelFuture future = RemoteAddressConnection.addListener(
                    RemoteAddressConnection.connection(socketAddress, ctx, new H2RedirctOutHandler(inboundChannel)),
                    inboundChannel);
            channelFork.put(socketAddress, future.channel());
        });
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new ConnectionMonitor(ctx, channelFork, inboundChannel, offlineDataRepo), 1, 5, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        channelFork.forEach((socket, channel) -> {
            channel.writeAndFlush(buf.copy())
                    .addListener(new H2DBChannelFutureListener(buf.copy(), statistics, offlineDataRepo, ctx.channel()));
        });
        buf.clear();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        channelFork.forEach((socket, channel) -> {
            closeAndFlush(channel);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("{}", cause.getMessage());
        closeAndFlush(ctx.channel());
    }

    private void closeAndFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
