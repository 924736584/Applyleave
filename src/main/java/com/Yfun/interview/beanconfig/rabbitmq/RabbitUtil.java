package com.Yfun.interview.beanconfig.rabbitmq;

import com.Yfun.interview.util.LogProcessingUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName : RabbitUtil
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-01 16:06
 */
public class RabbitUtil {
    private static Connection connection;
    private String exchangeName;
    /* logger */
    private static LogProcessingUtil LOGGER = new LogProcessingUtil(RabbitUtil.class);

    void initMq(Connection connection) {
        RabbitUtil.connection = connection;

    }

    public static Connection getConnect() {
        return connection;
    }

    public Channel createChannel(String exchangeName, String type) {
        Channel channel = null;
        this.exchangeName = exchangeName;
        try {
            channel = connection.createChannel();
            LOGGER.warn("Rabbitmq initialization complete");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (channel != null) {
            try {
                channel.exchangeDeclare(exchangeName, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return channel;
    }
    public void sendMessage(Channel channel,String Msg,String exchangeName,String routerKey){
        sendMessage(channel,Msg.getBytes(StandardCharsets.UTF_8),exchangeName,routerKey);
    }
    public void sendMessage(Channel channel,byte[] Msgs,String exchangeName,String routerKey){
        if(channel==null){
            throw new NullPointerException("请初始化channel");
        }if(StringUtils.isNotBlank(exchangeName) && StringUtils.isBlank(this.exchangeName)) {
            //开启消息confirm模式

            try {
                channel.confirmSelect();
                channel.basicPublish(exchangeName,routerKey, MessageProperties.PERSISTENT_BASIC,Msgs);
                channel.waitForConfirmsOrDie();
                LOGGER.warn("消息已经发送到了目标服务器等待被消费");
            } catch (IOException | InterruptedException e) {
                LOGGER.warn("消息发送失败具体原因请联系开发人员");
            }
        }
    }
}
