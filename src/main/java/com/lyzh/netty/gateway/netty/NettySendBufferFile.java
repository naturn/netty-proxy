package com.lyzh.netty.gateway.netty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyzh.netty.gateway.common.Conversion;
import com.lyzh.netty.gateway.file.BufferFile;
import com.lyzh.netty.gateway.file.FileBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月16日 - 上午11:59:16
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class NettySendBufferFile {
    
    private static final Logger logger = LoggerFactory.getLogger(NettySendBufferFile.class);

    private static final String BUFFER_ROOT = "." + File.separator + "buffer" + File.separator;

    public void send() {

        FileBuffer.scandBufferFile(BUFFER_ROOT).forEach(p -> {
            send(p);
        });
    }

    private void send(BufferFile file) {

        ChannelFuture channelFuture = RemoteAddressConnection.connection(file.getSocket());

        channelFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.channel().isActive()) {
                    file.getData().forEach(p -> {
                        ByteBuf temp = Unpooled.copiedBuffer(Conversion.hexStringToBytes(p));
                        channelFuture.channel().writeAndFlush(temp).addListener(new ChannelFutureListener() {

                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if (!future.isSuccess()) {                                    
                                    logger.debug("Buffer file failure,Disconnection {}",channelFuture.channel().remoteAddress());
                                    channelFuture.channel().close();
                                }
                            }
                        });
                    });
                }
            }
        });
        try {
            channelFuture.channel().close().sync();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            Files.delete(file.getPath());
        } catch (IOException e) {
           logger.error("Delete file failure {}",file.getPath());
        }

    }
}
