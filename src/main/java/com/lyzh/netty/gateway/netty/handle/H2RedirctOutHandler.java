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
 * @Date 2018年3月21日 - 上午9:59:57
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class H2RedirctOutHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(H2RedirctOutHandler.class);
    
    private final Channel inboundChannel;
    
    public H2RedirctOutHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {        
        inboundChannel.writeAndFlush(msg).addListener(new BufferChannelFutureListener(ctx.channel()));
    }
      
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Disconnection.");
    }
}
