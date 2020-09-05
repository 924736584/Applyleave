package com.Yfun.interview.util;

import com.Yfun.interview.beanconfig.redis.RedisUtil;
import com.Yfun.interview.dao.UserInfo;
import com.Yfun.interview.mapper.UserInfoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Random;

/**
 * @ClassName : RedisOperationUtil
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-08-30 22:05
 */
public class RedisOperationUtil {
    final String KEY="LEADER:INFO:";
    /**
     * 查询redis，没有就查询数据库查询部门主管信息
     */
    public UserInfo selectCacheAndFromDatabase(RedisUtil redisUtil, RedissonClient redissonClient, UserInfoMapper mapper, String department){
        Jedis jedis = redisUtil.getJedis();
        String keyName=KEY + department;
        String result = jedis.get(keyName);
        UserInfo userInfo=null;
        if(StringUtils.isBlank(result)){
            //通过JUC锁
            RLock lock = redissonClient.getLock("lock");
            lock.lock();
            // 到数据库查询
            userInfo=mapper.selectLeaderInfoByDepartment(department);
            if(userInfo==null){
                //使用传入空值的方法进行redis缓存穿透问题
               jedis.setex(keyName,60,"null");
            }else {
                //为了防止雪崩使用1—3小时的随机时间
                int randomTime = new Random().nextInt(3);
                jedis.setex(keyName,60*60*randomTime,userInfo.toString());
            }
            lock.unlock();
            }else {
            try {
                userInfo = new ObjectMapper().readValue(result, UserInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userInfo;

    }
}
