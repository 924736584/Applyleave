package com.Yfun.interview.service.impl;

import com.Yfun.interview.beanconfig.redis.RedisUtil;
import com.Yfun.interview.dao.ApprovalProgress;
import com.Yfun.interview.dao.LeaveTable;
import com.Yfun.interview.dao.UserInfo;
import com.Yfun.interview.mapper.ApprovalProgressMapper;
import com.Yfun.interview.mapper.LeaveTableMapper;
import com.Yfun.interview.mapper.UserInfoMapper;
import com.Yfun.interview.service.UserActionService;
import com.Yfun.interview.service.dependent.ManagementDependent;
import com.Yfun.interview.service.thread.ServiceThreadHandler;
import com.Yfun.interview.util.LogProcessingUtil;
import com.Yfun.interview.util.NickNameCreateUtil;
import com.Yfun.interview.util.ObjectToStringUtil;
import com.Yfun.interview.util.StrToMd5Util;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @ClassName : UserActionServiceImpl
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-08-28 21:05
 */
@Service
public class UserActionServiceImpl implements UserActionService {
    @Autowired
    ManagementDependent management;

    /* logger */
    private static LogProcessingUtil LOGGER = new LogProcessingUtil(UserActionService.class);

    @Transactional
    public String registerSave(Map map) {
        ObjectMapper mapper = new ObjectMapper();
        UserInfo userInfo = null;
        Map<String, Object> result = new HashMap<>();
        LOGGER.configParam(ObjectToStringUtil.changeString(map));
        try {
            userInfo = mapper.readValue(mapper.writeValueAsString(map), UserInfo.class);
            if (userInfo == null) {
                throw new NullPointerException("传入参数不能为空");
            }
            String passwd = userInfo.getPasswd();
            userInfo.setPasswd(StrToMd5Util.toMd5(passwd));
            userInfo.setRootLevel(0);
            userInfo.setNickName(new NickNameCreateUtil().create());

        } catch (JsonParseException e) {
            LOGGER.error("Json转换异常 转换的数据是:" + map);
            e.printStackTrace();
        } catch (JsonMappingException e) {
            LOGGER.error("Json映射异常无法将数据:" + map + "映射为:com.Yfun.interview.dao.UserInfo类");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            LOGGER.error("Json处理异常");
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error("IO异常请及时修正");
            e.printStackTrace();
        }
        int i = management.getUserInfoMapper().insertUserInfo(userInfo);
        if (i > 0) {
            result.put("status", 200);
            result.put("message", "用户注册成功跳转登录");

            UserInfo base_info = new UserInfo();
            base_info.setNickName(userInfo.getNickName());
            base_info.setId(userInfo.getId());
            result.put("userbasic_msg", base_info);
            LOGGER.debug("用户:" + base_info.getNickName() + "注册成功");
        } else {
            LOGGER.debug("用户写入数据库失败,检查数据库是否正常");
            result.put("status", 500);
            result.put("message", "用户注册失败详情查看日志");
            result.put("userbasic_msg", null);
        }
        String result_json_str = "";
        try {
            result_json_str = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result_json_str;
    }

    public String registerCheckInfo(Map map) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ObjectMapper mapper = new ObjectMapper();
        Map<Object, Object> result = new HashMap<>();
        Map<String, String> message = new HashMap<String, String>();
        UserInfo userInfo = null;
        try {
            userInfo = mapper.readValue(mapper.writeValueAsString(map), UserInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userInfo != null) {
            String email = userInfo.getEmail();
            if (StringUtils.isBlank(email)) {
                message.put("email_error", "邮箱不能为空");
            }
            //启动后台线程查询邮箱是否注册过了
            Future<UserInfo> email_result = executor.submit(new ServiceThreadHandler.UserInfoCheckByEmail(email, management.getUserInfoMapper()));
            String phoneNumber = userInfo.getPhoneNumber();
            Future<UserInfo> phoneNumber_result = null;
            if (StringUtils.isBlank(phoneNumber)) {
                message.put("phone_error", "电话不为空");
            } else {
                if (phoneNumber.length() == 11) {
                    for (int i = 0; i < phoneNumber.length(); i++) {
                        int ascii = Integer.valueOf(phoneNumber.charAt(i));
                        if (ascii < 48 || ascii > 57) {
                            message.put("phone_error", "格式错误请检查 不能包含字符");
                            LOGGER.warn("格式错误请检查 不能包含字符");
                            break;
                        }
                    }
                    //启动线程 验证该手机号码是否被注册
                    phoneNumber_result = executor.submit(new ServiceThreadHandler.UserInfoCheckByPhoneNumber(phoneNumber, management.getUserInfoMapper()));
                } else {
                    message.put("phone_error", "格式错误请检查");
                    LOGGER.warn("格式错误请检查");
                }

            }
            String idcardNumber = userInfo.getIdcardNumber();
            Future<UserInfo> idcard_result = null;
            if (StringUtils.isBlank(idcardNumber)) {
                message.put(" idcard_error", "身份证不能为空请填写正确的身份证号");
                LOGGER.warn("身份证不能为空请填写正确的身份证号");

            } else {
                if (idcardNumber.length() == 18) {
                    for (int i = 0; i < idcardNumber.length() - 1; i++) {
                        int ascii = Integer.valueOf(idcardNumber.charAt(i));
                        if (ascii < 48 || ascii > 57) {
                            message.put("idcard_error", "格式错误请检查包含特殊字符");
                            LOGGER.warn("格式错误请检查包含特殊字符");
                            break;
                        }
                    }
                    //启动线程查看是否身份证重复注册
                    idcard_result = executor.submit(new ServiceThreadHandler.UserInfoCheckByIdcardNumber(idcardNumber, management.getUserInfoMapper()));

                } else {
                    message.put("idcard_error", "格式错误请检查身份证格式");
                    LOGGER.warn("格式错误请检查身份证格式");

                }
            }
            try {
                if (email_result.get() != null) {
                    message.put("email_error", "邮箱已注册请更换邮箱/n，是否忘记账号，请点击啊忘记密码找回账号");
                    LOGGER.warn("邮箱已注册请更换邮箱/n，是否忘记账号，请点击啊忘记密码找回账号");

                }
                if (phoneNumber_result != null && phoneNumber_result.get() != null) {
                    message.put("phone_error", "手机号已注册请更换手机号/n，是否忘记账号，请点击啊忘记密码找回账号");
                    LOGGER.warn("手机号已注册请更换手机号/n，是否忘记账号，请点击啊忘记密码找回账号");

                }
                if (idcard_result != null && idcard_result.get() != null) {
                    message.put("idcard_error", "身份证号已注册/n，是否忘记账号，请点击啊忘记密码找回账号");
                    LOGGER.warn("身份证号已注册/n，是否忘记账号，请点击啊忘记密码找回账号");

                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (CollectionUtils.isEmpty(message)) {
            result.put("status", 200);
        } else {
            result.put("status", 500);
        }
        result.put("message", message);
        String result_json_str = "";
        try {
            result_json_str = mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result_json_str;
    }

    @Override
    public String loginCheckData(Map map) {
        ObjectMapper mapper = new ObjectMapper();
        Map result = new HashMap<String, Object>();
        Map message = new HashMap<String, String>();
        UserInfo userInfo = null;
        try {
            userInfo = mapper.readValue(mapper.writeValueAsString(map), UserInfo.class);
            userInfo.setPasswd(StrToMd5Util.toMd5(userInfo.getPasswd()));
            LOGGER.configParam(Thread.currentThread().getStackTrace()[1].getMethodName(), ObjectToStringUtil.changeString(userInfo));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userInfo != null) {
            String nickName = userInfo.getNickName();
            if (StringUtils.isBlank(nickName)) {
                message.put("nickname_erorr", "账号不能为空");
                LOGGER.error("账号不能为空");
            } else {
                for (int i = 0; i < nickName.length() - 1; i++) {
                    int ascii = (int) nickName.charAt(i);
                    if (ascii < 48 || ascii > 57) {
                        message.put("nickname_error", "账号格式错误请检查是否包含特殊字符");
                        LOGGER.error("账号格式错误请检查是否包含特殊字符");
                        break;
                    }
                }
                String passwd = userInfo.getPasswd();
                if (StringUtils.isBlank(passwd)) {
                    message.put("passwd_erorr", "密码不能为空");
                    LOGGER.error("密码不能为空");
                } else {
                    UserInfo result_info = management.getUserInfoMapper().selectUserInfoLoginCheck(nickName, passwd);
                    if (result_info != null) {
                        result.put("status", 200);
                        //启动线程进行登录用户Redis数据保存
                        try {
                            //设置时间为3小时
                            new Thread(new ServiceThreadHandler.CacheSaveLoginUserInfo(result_info.getNickName(), mapper.writeValueAsString(result_info), management.getRedisUtil(), 60 * 60 * 3)).start();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        result.put("status", 500);
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(message) && (int) result.get("status") == 200) {
            message.put("verification_results", "验证通过");
            LOGGER.error("验证通过");
        } else if (CollectionUtils.isEmpty(message) && (int) result.get("status") == 500) {
            message.put("verification_results", "用户名密码错误");
            LOGGER.error("用户名密码错误");
        } else {
            message.put("verification_results", "参数错误请检查");
            LOGGER.error("参数错误请检查");
        }
        result.put("message", ObjectToStringUtil.changeString(message));
        return ObjectToStringUtil.changeString(result);
    }

}
