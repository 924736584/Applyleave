package com.Yfun.interview.service.rabbitmq;

import com.Yfun.interview.beanconfig.rabbitmq.RabbitUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName : RabbitmqReceiveClient
 * @Description :持续监听
 * @Author : DeYuan
 * @Date: 2020-09-02 17:56
 */
@Component
public class RabbitmqReceiveClient {
    @Autowired
    RabbitUtil rabbitUtil;
    @PostConstruct
    public void ListenRabbitMq(){
        String EXCHANGE_NAME="message_ex";
        String ROUTER_KEY="type_email";

        if(rabbitUtil==null){
            throw new NullPointerException("rabbitUtil 为空");
        }
        Channel channel = rabbitUtil.createChannel(EXCHANGE_NAME, "direct");
        try {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName,EXCHANGE_NAME,ROUTER_KEY);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                MonitorActionRegister.getHandler().forEach(handler->{
                    handler.rabbitEvent(message);
                });
            };
            channel.basicConsume(queueName,false,deliverCallback,consumerTag -> {
                channel.basicAck(Long.parseLong(consumerTag), false);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
