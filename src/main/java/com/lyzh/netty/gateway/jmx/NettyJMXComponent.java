package com.lyzh.netty.gateway.jmx;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lyzh.netty.gateway.bean.Statistics;
import com.lyzh.netty.gateway.jmx.bean.StatisticsMBean;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月24日 - 上午11:17:59
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

@Component
public class NettyJMXComponent {

    @Autowired
    private MBeanServer mbeanServer;

    private void registed(StandardMBean mbean, ObjectName objectName) {
        try {
            mbeanServer.registerMBean(mbean, objectName);
        } catch (InstanceAlreadyExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void registedStatistics(Statistics statistics) {

        ObjectName name;
        try {
            name = new ObjectName("com.lyzh.netty.gateway.bean:type=Statistics");
            StandardMBean mbean;
            try {
                mbean = new StandardMBean(statistics, StatisticsMBean.class);
                registed(mbean, name);
            } catch (NotCompliantMBeanException e1) {
                e1.printStackTrace();
            }

        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

    }
}
