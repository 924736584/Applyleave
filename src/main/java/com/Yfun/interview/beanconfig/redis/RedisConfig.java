package com.Yfun.interview.beanconfig.redis;

import com.Yfun.interview.util.ReadPropertiesResourceUtil;
import com.sun.tracing.ProbeName;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * @ClassName : RedisConfig
 * @Description : redis配置
 * @Author : DeYuan
 * @Date: 2020-08-31 10:12
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisUtil redisUtil(){
        Properties properties= null;
        RedisUtil redisUtil = new RedisUtil();
        try {
        properties = new ReadPropertiesResourceUtil().getProperties();
        redisUtil.initCache(RedisClientPoolConfig(),properties);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return redisUtil;
    }
    private Jedis RedisClientPoolConfig(){
        Properties properties=null;
        try {
          properties = new ReadPropertiesResourceUtil().getProperties();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String ADDR = properties.getProperty("redis_host");
        String PORT = properties.getProperty("redis_port");
        String AUTH = properties.getProperty("redis_password");
        String MAX_IDLE = properties.getProperty("redis_max_idle");
        String MAX_WAIT = properties.getProperty("redis_max_wait");
        String TIMEOUT = properties.getProperty("redis_timeout");
         JedisPool jedisPool = null;
        JedisPoolConfig config = new JedisPoolConfig();


         if(StringUtils.isNotBlank(MAX_IDLE)){
             config.setMaxIdle(Integer.parseInt(MAX_IDLE));
         }
        if(StringUtils.isNotBlank(MAX_WAIT)){
            config.setMaxIdle(Integer.parseInt(MAX_WAIT));
        }
        if(StringUtils.isBlank(TIMEOUT)){
          TIMEOUT="2000";
        }
            config.setTestOnBorrow(true);
        if(StringUtils.isBlank(ADDR)){
            throw new NullPointerException("redis地址必须设置");
        }
        if(StringUtils.isBlank(PORT)){
            PORT="6379";
        }
        if(StringUtils.isBlank(AUTH)){
            AUTH=null;
        }
        jedisPool = new JedisPool(config, ADDR, Integer.parseInt(PORT), Integer.parseInt(TIMEOUT));

        return jedisPool.getResource();
    }
}
