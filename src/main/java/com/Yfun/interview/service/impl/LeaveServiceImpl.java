package com.Yfun.interview.service.impl;

import com.Yfun.interview.beanconfig.rabbitmq.RabbitUtil;
import com.Yfun.interview.beanconfig.redis.RedisUtil;
import com.Yfun.interview.dao.ApprovalProgress;
import com.Yfun.interview.dao.LeaveTable;
import com.Yfun.interview.dao.UserInfo;
import com.Yfun.interview.mapper.ApprovalProgressMapper;
import com.Yfun.interview.mapper.LeaveTableMapper;
import com.Yfun.interview.mapper.UserInfoMapper;
import com.Yfun.interview.service.LeaveService;
import com.Yfun.interview.service.UserActionService;
import com.Yfun.interview.service.dependent.ManagementDependent;
import com.Yfun.interview.util.LogProcessingUtil;
import com.Yfun.interview.util.ObjectToStringUtil;
import com.Yfun.interview.util.RedisOperationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName : LeaveServiceImpl
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-04 10:42
 */
@Service
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    ManagementDependent management;
    /* logger */
    private static LogProcessingUtil LOGGER = new LogProcessingUtil(UserActionService.class);
    @Override
    @Transactional
    public String leaveApplySaveInfo(Map map) {
        ObjectMapper mapper = new ObjectMapper();
        LeaveTable leaveTable = null;
        Map result = new HashMap();
        Map message = new HashMap();
        try {
            leaveTable = mapper.readValue(mapper.writeValueAsString(map), LeaveTable.class);
            leaveTable.setCreateDate(new Date());
            LOGGER.configParam(ObjectToStringUtil.changeString(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (leaveTable != null) {
            String leaveType = leaveTable.getLeaveType();
            if (StringUtils.isBlank(leaveType)) {
                LOGGER.info("请假类别至少选择一个");
                message.put("leavetype_error", "请假类别至少选择一个");
            }
            //格式:1998-03-04-04:30:30$$1998-04-05
            String leaveDate = leaveTable.getLeaveDate();
            if (StringUtils.isBlank(leaveDate)) {
                LOGGER.error("请假时间不能为空");
                message.put("leavedate_error", "请假时间不能为空");
            }
            if (!leaveDate.contains("$$")) {
                LOGGER.error("时间格式错误");
                message.put("leavedate_error", "时间格式错误");
            }
            String timeProcessing = TimeProcessing(leaveDate);
            if (StringUtils.isNotBlank(timeProcessing)) {
                LOGGER.error(timeProcessing);
                message.put("leavedate_error", timeProcessing);
            }
            if(CollectionUtils.isEmpty(message)){
                // 此时表明一切正常，向数据库写数据了,添加事务
                // 写入进度 数据库
                int leaveTableStatus= management.getLeaveTableMapper().insertSelective(leaveTable);
                ApprovalProgress progress = new ApprovalProgress();
                progress.setName(leaveTable.getName());
                progress.setDepartment(leaveTable.getDepartment());
                progress.setNickname(leaveTable.getNickname());
                progress.setProgress(-1);
                int approvalProgressStatus=management.getApprovalProgressMapper().insertUserProgress(progress);
                if(leaveTableStatus>0 && approvalProgressStatus>0 )
                {
                    result.put("status",200);
                    message.put("verification_results","请假请求提交成功等待批准");
                    LOGGER.warn("请假请求提交成功等待批准");
                    // 启动Mq线程去调用邮件服务短信服务
                    LeaveTable finalLeaveTable = leaveTable;
                    new Thread(()->{
                        // 查询部门主管的邮箱
                        // 1)：先查Redis 2):再查数据库
                        UserInfo userInfo = new RedisOperationUtil().selectCacheAndFromDatabase(management.getRedisUtil(), management.getRedissonClient(), management.getUserInfoMapper(), finalLeaveTable.getDepartment());
                        if(userInfo==null){
                            LOGGER.configParam(finalLeaveTable.getDepartment());
                            LOGGER.error("没有这个部门主管的信息,消息通知发送失败");
                        }else{
                            //  (type,form_queue,receive_info{email,phone,nickname},body
                            Map<String ,String> paramMap=new HashMap<String,String>();
                            paramMap.put("type","email");
                            paramMap.put("form_queue","type_email");
                            HashMap<String, String> leader_info = new HashMap<>();
                            leader_info.put("email",userInfo.getEmail());
                            leader_info.put("nickname",userInfo.getNickName());
                            try {
                                paramMap.put("receive_info",mapper.writeValueAsString(leader_info));
                                //传入模板处理好的模板内容
                                paramMap.put("body","");
                            //发送邮件
                            RabbitUtil rabbitUtil = management.getRabbitUtil();
                            Channel channel = rabbitUtil.createChannel("message_ex", "direct");

                                rabbitUtil.sendMessage(channel,mapper.writeValueAsBytes(paramMap),"message_ex","type_email");
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            }
        }
        if(result.get("status")!=null && (int)result.get("status")==200){
            result.put("message",message);
        }else {
            result.put("status",500);
            result.put("message",message);
        }
        String result_str="";
        try {
            result_str= mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return  result_str;
    }
    private String TimeProcessing(String leaveDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String[] times = leaveDate.split("\\$\\$");
        String start_time = times[0];
        String end_time = times[1];
        long start_time_stamp = 0;
        long end_time_stamp = 0;
        try {
            start_time_stamp=format.parse(start_time).getTime();
            end_time_stamp=format.parse(end_time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(start_time_stamp<new Date().getTime()){
            return "请假时间小于了当前时间请更改请假日期";
        }
        if(end_time_stamp<new Date().getTime()){
            return "请假截至时间小于了当前时间请更改";
        }
        return "";
    }
}
