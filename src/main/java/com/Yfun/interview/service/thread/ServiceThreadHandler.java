package com.Yfun.interview.service.thread;

import com.Yfun.interview.beanconfig.redis.RedisUtil;
import com.Yfun.interview.dao.UserInfo;
import com.Yfun.interview.mapper.UserInfoMapper;
import com.Yfun.interview.util.LogProcessingUtil;
import redis.clients.jedis.Jedis;

import java.util.concurrent.Callable;

/**
 * @ClassName : ServiceThreadHandler
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-08-29 16:37
 */

public class ServiceThreadHandler {

    public static  class UserInfoCheckByEmail implements Callable<UserInfo> {

         UserInfoMapper userInfoMapper;
         String email="";
         public UserInfoCheckByEmail(String email,UserInfoMapper userInfoMapper){
            this.email=email;
            this.userInfoMapper=userInfoMapper;
        }
        @Override
        public UserInfo call() throws Exception {
            return  userInfoMapper.selectUserInfoByEmail(email);
        }
    }

    public static class UserInfoCheckByPhoneNumber implements Callable<UserInfo>{

        UserInfoMapper userInfoMapper;

        String phoneNumber="";
        public UserInfoCheckByPhoneNumber(String phoneNumber,UserInfoMapper userInfoMapper){
            this.phoneNumber=phoneNumber;
            this.userInfoMapper=userInfoMapper;

        }
        @Override
        public UserInfo call() throws Exception {
            UserInfo userInfo =null;
            try {
           userInfo =   userInfoMapper.selectUserInfoByPhoneNumber(phoneNumber);
            }catch (NullPointerException e){
                return null;
            }
            return userInfo;
        }
    }
    public static class UserInfoCheckByIdcardNumber implements Callable<UserInfo> {
         UserInfoMapper userInfoMapper;
        String idcardNumber="";
        public UserInfoCheckByIdcardNumber(String idcardNumber,UserInfoMapper userInfoMapper) {
        this.idcardNumber=idcardNumber;
        this.userInfoMapper=userInfoMapper;
        }

        @Override
        public UserInfo call() throws Exception {
            UserInfo userInfo=null;
            try {
                userInfo =  userInfoMapper.selectUserInfoByIdcardNumber(idcardNumber);
            }catch (NullPointerException e){
                return null;
            }
            return userInfo;
        }
    }
    public static class CacheSaveLoginUserInfo implements Runnable{
        String info="";
        RedisUtil redisUtil;
        String nickName;
        String KEY_V="USER_LOGIN:";
        int timeOut=0;
        public LogProcessingUtil LOGGER= new LogProcessingUtil(CacheSaveLoginUserInfo.class);
        public CacheSaveLoginUserInfo(String nickName,String info, RedisUtil redisUtil){
            this.info=info;
            this.redisUtil=redisUtil;
            this.nickName=nickName;
        }
        public CacheSaveLoginUserInfo(String nickName,String info, RedisUtil redisUtil,int timeOut){
            this.info=info;
            this.redisUtil=redisUtil;
            this.nickName=nickName;
            this.timeOut=timeOut;
        }
        @Override
        public void run() {
            Jedis jedis = redisUtil.getJedis();
            // 数据库为2号库
            // jedis.select(2);
            if(timeOut==0) {
                jedis.set(KEY_V + nickName, info);
            }else {
                jedis.setex(KEY_V+nickName,timeOut,info);
            }
            LOGGER.warn(String.format("Cache account:%s data written successfully", nickName));
            jedis.close();
            LOGGER.info("Redis disconnected");
        }
    }
}
