package com.lyzh.netty.gateway.netty;

import java.net.SocketAddress;

import com.lyzh.netty.gateway.netty.handle.RedirctInHandler;
import com.lyzh.netty.gateway.netty.listener.SyncChannelFutureListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月13日 - 下午4:28:32
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class RemoteAddressConnection {

    public static ChannelFuture connection(SocketAddress remote, ChannelHandlerContext ctx) {
        return connection(remote, ctx.channel());
    }

    public static ChannelFuture connection(SocketAddress remote, ChannelHandlerContext ctx,
            ChannelInboundHandler channelHandler) {
        return connectionBuild(remote, ctx.channel(), channelHandler);
    }

    public static ChannelFuture connection(SocketAddress remote, Channel channel) {
        return connectionBuild(remote, channel, new RedirctInHandler(channel));
    }

    static ChannelFuture connectionBuild(SocketAddress remote, Channel channel, ChannelInboundHandler channelHandler) {
        Bootstrap b = new Bootstrap();
        b.group(channel.eventLoop()).channel(channel.getClass()).handler(channelHandler).option(ChannelOption.AUTO_READ,
                false);
        return b.connect(remote);
    }

    public static ChannelFuture connection(SocketAddress remote) {
        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup());
        b.channel(NioSocketChannel.class);
        b.handler(new LoggingHandler(LogLevel.INFO));
        b.option(ChannelOption.TCP_NODELAY, true);
        b.option(ChannelOption.AUTO_READ, true);
        return b.connect(remote);
    }

    public static ChannelFuture addListener(ChannelFuture future, Channel inBoundChannel) {
        return future.addListener(new SyncChannelFutureListener(inBoundChannel));
    }

    public static ChannelFuture addListener(ChannelFuture future, ChannelFutureListener listener) {
        return future.addListener(listener);
    }

    public class Builder {
        public Builder(SocketAddress remote, ChannelHandlerContext ctx) {

        }
    }
}
