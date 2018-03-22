package com.lyzh.netty.gateway.netty.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月9日 - 下午2:48:51
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class SyncChannelFutureListener implements ChannelFutureListener {

    private static final Logger logger = LoggerFactory.getLogger(SyncChannelFutureListener.class);

    private final Channel channel;

    public SyncChannelFutureListener(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {

        if (future.isSuccess()) {
            channel.read();
        } else {
            logger.debug("Future faile. Close Sync Future channel. case:{}", future);
            channel.close();
        }

    }

}
