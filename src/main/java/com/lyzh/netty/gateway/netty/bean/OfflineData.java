package com.lyzh.netty.gateway.netty.bean;

import java.net.SocketAddress;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月20日 - 上午10:59:00
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

@Entity
public class OfflineData {

    @Id
    @GeneratedValue
    private int id;
    
    private int sessionId;

    private SocketAddress remoteAddress;

    private boolean reissue;

    private String message;
    
    private Long sendingTime =  System.nanoTime();

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public boolean isReissue() {
        return reissue;
    }

    public void setReissue(boolean reissue) {
        this.reissue = reissue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
            
    public Long getSendingTime() {
        return sendingTime;
    }    

    public void setSendingTime(Long sendingTime) {
        this.sendingTime = sendingTime;
    }
    
    public int getId() {
        return id;
    }

    @Override
    public String toString() {        
        return String.format("session:%s", sessionId);
    }
}
