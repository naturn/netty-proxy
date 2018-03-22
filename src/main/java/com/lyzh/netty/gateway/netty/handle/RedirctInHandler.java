package com.lyzh.netty.gateway.netty.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyzh.netty.gateway.netty.listener.BufferChannelFutureListener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author Naturn
 * 
 * @Date 2018年1月30日 - 上午9:07:14
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class RedirctInHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RedirctInHandler.class);

    private final Channel inBoundChannel;

    public RedirctInHandler(Channel inBoundChannel) {
        this.inBoundChannel = inBoundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        logger.debug("{}", ctx.channel().remoteAddress());
        inBoundChannel.writeAndFlush(msg).addListener(new BufferChannelFutureListener(ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) { 
        logger.info("Disconnection.");
        // RedirctHandler.closeOnFlush(inBoundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}
