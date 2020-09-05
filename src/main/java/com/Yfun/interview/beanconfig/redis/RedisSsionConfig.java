package com.Yfun.interview.beanconfig.redis;

import com.Yfun.interview.util.ReadPropertiesResourceUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * @ClassName : RedisSsionConfig
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-04 22:18
 */
@Configuration
public class RedisSsionConfig {
    @Bean
    public RedissonClient redissonClient()
    {
        Properties properties = null;
        RedissonClient redissonClient=null;
        try {
            properties = new ReadPropertiesResourceUtil().getProperties();
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+properties.getProperty("redis_host")+":6379");
            redissonClient= Redisson.create(config);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return redissonClient;
    }
}
