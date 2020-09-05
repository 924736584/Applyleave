package com.Yfun.interview.service.rabbitmq;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName : MonitorActionRegister
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-03 11:03
 */
public class MonitorActionRegister {
    private static Vector vector=new Vector<RabbitMqMessageHandler>();

    public static void addRabbitHandler(RabbitMqMessageHandler object){
            if(!vector.contains(object)){
                vector.add(object);
            }
    }
    static Vector<RabbitMqMessageHandler> getHandler(){
        return vector;
    }
}
