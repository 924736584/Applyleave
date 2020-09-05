package com.Yfun.interview.service.dependent;

import com.Yfun.interview.beanconfig.rabbitmq.RabbitUtil;
import com.Yfun.interview.beanconfig.redis.RedisUtil;
import com.Yfun.interview.mapper.ApprovalProgressMapper;
import com.Yfun.interview.mapper.LeaveTableMapper;
import com.Yfun.interview.mapper.UserInfoMapper;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

/**
 * @ClassName : ManagementDepandent
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-04 16:47
 */
@Component
public class ManagementDependent {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    LeaveTableMapper leaveTableMapper;
    @Autowired
    ApprovalProgressMapper approvalProgressMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    RabbitUtil rabbitUtil;

    public UserInfoMapper getUserInfoMapper() {
        return userInfoMapper;
    }

    public LeaveTableMapper getLeaveTableMapper() {
        return leaveTableMapper;
    }

    public ApprovalProgressMapper getApprovalProgressMapper() {
        return approvalProgressMapper;
    }

    public RedisUtil getRedisUtil() {
        return redisUtil;
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    public RabbitUtil getRabbitUtil() {
        return rabbitUtil;
    }
}
