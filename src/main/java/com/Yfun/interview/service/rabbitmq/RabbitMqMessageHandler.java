package com.Yfun.interview.service.rabbitmq;

/**
 * @ClassName : RabbitMqMessageHandler
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-03 10:43
 */
public abstract class RabbitMqMessageHandler {
    protected abstract  void rabbitEvent(String message);
}
