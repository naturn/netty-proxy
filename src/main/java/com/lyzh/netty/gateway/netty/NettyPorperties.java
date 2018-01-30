package com.lyzh.netty.gateway.netty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.lyzh.netty.gateway.netty.bean.Proxy;

/**
 * @Author Naturn
 * 
 * @Date 2018年1月26日 - 下午3:38:09
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

@ConfigurationProperties(prefix = "netty")
public class NettyPorperties {

    private String host = "127.0.0.1";

    private List<Proxy> proxy = new ArrayList<>();;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<Proxy> getProxy() {
        return proxy;
    }

    public void setProxy(List<Proxy> proxy) {
        this.proxy = proxy;
    }    
 
}
