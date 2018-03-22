package com.lyzh.netty.gateway.test;

import org.junit.Test;

import com.lyzh.netty.gateway.netty.NettySendBufferFile;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月16日 - 下午4:11:10
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class NettyBufferFileSendTest {

    @Test
    public void test() {

        NettySendBufferFile sendBufferFile = new NettySendBufferFile();
        sendBufferFile.send();
    }

}
