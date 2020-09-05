package com.Yfun.interview.controller;

import com.Yfun.interview.service.UserActionService;
import com.Yfun.interview.service.impl.LeaveServiceImpl;
import com.Yfun.interview.service.impl.UserActionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName : UserActionController
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-08-28 20:58
 */
@RestController
public class UserActionController {
    @Autowired
    UserActionServiceImpl userActionService;
    @Autowired
    LeaveServiceImpl leaveService;
    @RequestMapping(value = "register/register_save",method = RequestMethod.POST)
    public String registerSave(@RequestBody Map map){
        return userActionService.registerSave(map);
    }
    @RequestMapping(value = "register/register_check",method = RequestMethod.POST)
    public String registerCheck(@RequestBody Map map){
        return userActionService.registerCheckInfo(map);
    }
    @RequestMapping(value = "login/login_check",method = RequestMethod.POST)
    public String loginCheck(@RequestBody Map map){
        return userActionService.loginCheckData(map);
    }
    @RequestMapping(value = "apply/leave_apply",method = RequestMethod.POST)
    public String leaveApplySaveInfo(@RequestBody Map map){
        return leaveService.leaveApplySaveInfo(map);
    }
}
