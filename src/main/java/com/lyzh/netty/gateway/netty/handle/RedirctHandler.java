package com.lyzh.netty.gateway.netty.handle;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyzh.netty.gateway.common.Conversion;
import com.lyzh.netty.gateway.file.AccessFileDao;
import com.lyzh.netty.gateway.netty.RemoteAddressConnection;
import com.lyzh.netty.gateway.netty.listener.BufferChannelFutureListener;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author Naturn
 * 
 * @Date 2018年1月26日 - 下午4:04:24
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class RedirctHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RedirctHandler.class);

    private final List<SocketAddress> outputs;

    private Channel inBoundChannel;

    private Thread monitor = null;

    private boolean flag = true;

    // As we use inboundChannel.eventLoop() when building the Bootstrap this
    // does not need to be volatile as
    // the outboundChannel will use the same EventLoop (and therefore Thread) as
    // the inboundChannel.

    // private List<Channel> outBoundChannels;
    // 1:1
    private Map<SocketAddress, Channel> channelFork;

    public RedirctHandler(List<SocketAddress> outputs) {
        this.outputs = outputs;
        channelFork = new HashMap<>(outputs.size());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        inBoundChannel = ctx.channel();

        // Start the connection attempt.
        outputs.forEach(socketAddress -> {
            System.out.println(socketAddress);
            ChannelFuture future = RemoteAddressConnection
                    .addListener(RemoteAddressConnection.connection(socketAddress, ctx), inBoundChannel);
            channelFork.put(socketAddress, future.channel());
        });
        monitor = new Thread(new ConnectionMonitor(ctx));
        monitor.start();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;

        Queue<ByteBuf> bufQueue = new LinkedBlockingQueue<>(channelFork.size());
        for (int i = 0; i < channelFork.size(); i++) {
            if (bufQueue.isEmpty()) {
                bufQueue.add(buf);
            } else {
                bufQueue.add(buf.copy());
            }
        }
        channelFork.forEach((k, v) -> {
            ByteBuf data = bufQueue.poll();
            if (v.isActive()) {
                v.writeAndFlush(data).addListener(new BufferChannelFutureListener(ctx.channel()));
            } else {
                cacheWrite(k, v, data);
            }
        });

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        flag = false;
        channelFork.forEach((socket, channel) -> {
            if (channel != null) {
                closeOnFlush(channel);
            }
        });
    }
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        flag = false;
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    class ConnectionMonitor implements Runnable {

        private ChannelHandlerContext ctx;

        public ConnectionMonitor(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {

            while (flag) {
                diff();
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    logger.error("Time Unit interrupted exception.");
                }
            }

        }

        private void diff() {
            channelFork.entrySet().stream().filter(p -> {
                return !p.getValue().isActive();
            }).forEach(p -> {

                ChannelFuture future = RemoteAddressConnection
                        .addListener(RemoteAddressConnection.connection(p.getKey(), ctx), inBoundChannel);
                future.addListener(new ChannelFutureListener() {

                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            int expired = p.getValue().hashCode();
                            channelFork.put(p.getKey(), future.channel());
                            List<String> result = AccessFileDao.read(expired, p.getKey());
                            cacheSend(result, future.channel());
                        }
                    }
                });

            });
        }
    }

    private void cacheWrite(SocketAddress k, Channel channel, ByteBuf buf) {

        AccessFileDao.write(k, channel, buf);
    }

    private void cacheSend(List<String> result, Channel channel) {

        if (result != null) {
            result.forEach(p -> {
                ByteBuf temp = Unpooled.copiedBuffer(Conversion.hexStringToBytes(p));
                channel.writeAndFlush(temp).addListener(new ChannelFutureListener() {

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {

                        } else {

                        }

                    }
                });
            });
        }
    }

}
