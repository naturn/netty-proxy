package com.lyzh.netty.gateway.netty.listener;

import com.lyzh.netty.gateway.bean.Statistics;
import com.lyzh.netty.gateway.common.Conversion;
import com.lyzh.netty.gateway.netty.bean.OfflineData;
import com.lyzh.netty.gateway.netty.repo.OfflineDataRepo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月20日 - 下午4:38:43
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class H2DBChannelFutureListener implements ChannelFutureListener { 
    
    private OfflineDataRepo dataRepo;

    private final ByteBuf msg;

    private final Statistics statistics;

    private final Channel channel;

    public H2DBChannelFutureListener(ByteBuf msg, Statistics statistics, OfflineDataRepo dataRepo, Channel channel) {
        this.msg = msg;
        this.statistics = statistics;
        this.dataRepo = dataRepo;
        this.channel = channel;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {

        if (future.isSuccess()) {
            statistics.increaseInput(msg.readableBytes());
            channel.read();
        } else {
            byte[] temp = new byte[msg.readableBytes()];
            msg.readBytes(temp);
            OfflineData data = new OfflineData();
            data.setSessionId(future.channel().hashCode());
            data.setReissue(false);
            data.setSessionId(future.channel().hashCode());
            data.setRemoteAddress(future.channel().remoteAddress());
            data.setMessage(Conversion.bytesToHexString(temp));
            dataRepo.save(data);
        }

    }

}
