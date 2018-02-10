package com.lyzh.netty.gateway.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.lyzh.netty.gateway.netty.handle.HexDumpProxyInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Author Naturn
 * 
 * @Date 2018年1月26日 - 下午4:07:44
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

@Configuration
@EnableConfigurationProperties(NettyPorperties.class)
@Order
public class NettyProxyAutoconfiguration {

    private NettyPorperties properties;

    public NettyProxyAutoconfiguration(NettyPorperties properties) {
        this.properties = properties;
        buildNew();
    }

    private void buildNew() {
        properties.getProxy().parallelStream().forEach(p -> {

            String[] hostPort = p.getInput().split(":");
            SocketAddress input = new InetSocketAddress(hostPort[0], Integer.valueOf(hostPort[1]));

            List<SocketAddress> outputs = Arrays.asList(p.getOutput().split(",")).stream().map(new Function<String, SocketAddress>() {

                @Override
                public SocketAddress apply(String t) {
                    String[] value = t.split(":");                    
                    return new InetSocketAddress(value[0], Integer.valueOf(value[1]));
                }
            }).collect(Collectors.toList());
            
            build(input,outputs);            
        });
    }

    private void build(SocketAddress input, List<SocketAddress> outputs) {
        // Configure the bootstrap.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new HexDumpProxyInitializer(outputs))
                    .childOption(ChannelOption.AUTO_READ, false);
            
            ChannelFuture vehicleFuture = b.bind(input).sync();           
            // Wait until the connection is closed.
            vehicleFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
