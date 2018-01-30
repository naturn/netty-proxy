package com.lyzh.netty.gateway.netty.handle;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

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

    private final List<SocketAddress> outputs;

    // As we use inboundChannel.eventLoop() when building the Bootstrap this
    // does not need to be volatile as
    // the outboundChannel will use the same EventLoop (and therefore Thread) as
    // the inboundChannel.
//    private Channel outboundChannel;
    
    private List<Channel> outboundChannels;

    public RedirctHandler(List<SocketAddress> outputs) {
        this.outputs = outputs;
        outboundChannels = new ArrayList<>(outputs.size());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel inboundChannel = ctx.channel();

        // Start the connection attempt.
        outputs.forEach(socketAddress->{
            System.out.println(socketAddress);
            Bootstrap b = new Bootstrap();
            b.group(inboundChannel.eventLoop()).channel(ctx.channel().getClass())
                    .handler(new RedirctInHandler(inboundChannel)).option(ChannelOption.AUTO_READ, false);
           
            ChannelFuture f = b.connect(socketAddress);
            addListener(f,inboundChannel);
            f.channel().pipeline().addLast(new LoggingHandler(LogLevel.INFO));
        });
    }
    
    public void addListener(ChannelFuture f,Channel inboundChannel) {
        outboundChannels.add(f.channel());
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                } else {
                    // Close the connection if the connection attempt has
                    // failed.
                    inboundChannel.close();
                }
            }
        });
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;     
        
        Queue<ByteBuf> bufQueue = new LinkedBlockingQueue<>(outboundChannels.size());
        for(int i = 0;i<outboundChannels.size();i++) {
            if(bufQueue.isEmpty()) {
                bufQueue.add(buf);
            }else {
                bufQueue.add(buf.copy());
            }
        }
        
        outboundChannels.forEach(outboundChannel->{
            if (outboundChannel.isActive()) {
                outboundChannel.writeAndFlush(bufQueue.poll())
                .addListener(new ChannelFutureListener() {

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            ctx.channel().read();
                        } else {
                            ctx.channel().close();
                        }

                    }
                });
            }
        });
        
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        outboundChannels.forEach(outboundChannel->{
            if (outboundChannel != null) {
                closeOnFlush(outboundChannel);
            }
        });
       
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
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

}
