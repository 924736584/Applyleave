package com.Yfun.interview.service.impl;

import com.Yfun.interview.beanconfig.rabbitmq.RabbitUtil;
import com.Yfun.interview.service.dependent.ManagementDependent;
import com.Yfun.interview.service.rabbitmq.RabbitMqMessageHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * @ClassName : RabbitHandlerImpl
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-03 16:11
 */
public class RabbitHandlerImpl extends RabbitMqMessageHandler  {
    @Autowired
    ManagementDependent management;
    /**
     * 返回消息与接收消息数据结构如下
     * (type,form_queue,receive_info{email,phone,nickname},body
     */
    @Override
    protected void rabbitEvent(String message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map map = mapper.readValue(message, Map.class);
            String  form_queue = (String) map.get("form_queue");
            SelectProcessor(form_queue,map);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SelectProcessor(String from_queue, Map map){
        ObjectMapper mapper=new ObjectMapper();
        if ("type_email".equals(from_queue)){

        }
    }
}
