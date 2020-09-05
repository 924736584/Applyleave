package com.Yfun.interview.beanconfig.rabbitmq;

import com.Yfun.interview.util.ReadPropertiesResourceUtil;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName : RabbitConfig
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-01 16:05
 */
@Configuration
public class RabbitConfig {
    RabbitProperties rabbitProperties;
    @Bean
    public RabbitUtil rabbitUtil(){
        RabbitUtil rabbitUtil = new RabbitUtil();
        ReadPropertiesResourceUtil readProPerties=null;
        try {
            readProPerties= new ReadPropertiesResourceUtil();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(readProPerties!=null) {
            Properties properties=readProPerties.getProperties();
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(properties.getProperty("rabbitmq_host"));
            connectionFactory.setVirtualHost(properties.getProperty("rabbitmq_vhost"));
            connectionFactory.setUsername("ums");
            connectionFactory.setPassword("toor");
            try {
                rabbitUtil.initMq(connectionFactory.newConnection());
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        return rabbitUtil;
    }
}
