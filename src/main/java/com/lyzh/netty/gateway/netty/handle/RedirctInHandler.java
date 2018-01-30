package com.lyzh.netty.gateway.netty.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
    
    private final Channel inboundChannel;
    
    public RedirctInHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    ctx.channel().read();
                }else {
                    future.channel().close();
                }
                
            }
        });
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        RedirctHandler.closeOnFlush(inboundChannel);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();        
    }
}
