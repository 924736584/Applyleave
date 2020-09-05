package com.Yfun.interview.beanconfig.redis;

import com.Yfun.interview.util.LogProcessingUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Properties;

/**
 * @ClassName : RedisUtil
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-08-31 10:14
 */
public class RedisUtil {

    private Jedis jedis=null;
    /* logger */
   private LogProcessingUtil LOGGER= new LogProcessingUtil(RedisUtil.class);
    void initCache(Jedis jedis, Properties properties){
        LOGGER.warn(String.format("Redis initialization completed, connecting to %s:%s",properties.getProperty("redis_host"), StringUtils.isNotBlank(properties.getProperty("redis_port"))?properties.getProperty("redis_port"):"6379"));
        this .jedis=jedis;
    };

    public Jedis getJedis() {
        if(jedis==null){
            LOGGER.error("未初始化Jedis");
            throw new NullPointerException("未初始化Jedis");
        }
        return jedis;
    }
}
