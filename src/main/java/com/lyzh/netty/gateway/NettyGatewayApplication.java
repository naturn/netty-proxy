package com.lyzh.netty.gateway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @Author Naturn
 * 
 * @Date 2018年1月26日 - 下午3:33:31
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */
@SpringBootApplication
public class NettyGatewayApplication {

    public static void main(String[] args) {
        
        new SpringApplicationBuilder(NettyGatewayApplication.class).run(args);
    }
}
