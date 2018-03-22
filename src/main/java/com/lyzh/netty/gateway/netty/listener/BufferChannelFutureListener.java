package com.lyzh.netty.gateway.netty.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月9日 - 下午2:54:11
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class BufferChannelFutureListener implements ChannelFutureListener {

    private static final Logger logger = LoggerFactory.getLogger(BufferChannelFutureListener.class);

    private final Channel channel;

    public BufferChannelFutureListener(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            channel.read();
        } else {
            logger.warn("{} was unavailable.And Use the buff to storage data.", channel);            
        }

    }

}
