package com.lyzh.netty.gateway.netty.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyzh.netty.gateway.bean.Statistics;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月16日 - 下午9:03:15
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class CounterHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CounterHandler.class);

    private Statistics statistics;

    public CounterHandler(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Increment online {}", statistics.incrementOnline());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Decrement online {}", statistics.decrementOnline());
        super.channelInactive(ctx);
    }
}
