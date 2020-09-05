package com.Yfun.interview.service;

import java.util.Map;

/**
 * @ClassName : UserActionService
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-08-28 21:04
 */
public interface UserActionService {
    // Save user registration information
     String registerSave(Map map);
    // Check user information
    String registerCheckInfo(Map map);
    // Verify user login
    String loginCheckData(Map map);

}
