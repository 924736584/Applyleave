package com.Yfun.interview.mapper;

import com.Yfun.interview.dao.UserInfo;
import org.apache.ibatis.annotations.Param;

/**
* @ClassName : UserInfoMapper  
* @Description : ${description} 
* @Author : DeYuan
* @Date: 2020-08-28 20:54  
*/
public interface UserInfoMapper {

    int insertUserInfo(UserInfo userInfo);
    UserInfo selectUserInfoByEmail(String email);

    UserInfo selectUserInfoByPhoneNumber(String phoneNumber);

    UserInfo selectUserInfoByIdcardNumber(String idcardNumber);

    UserInfo selectUserInfoLoginCheck( @Param("nickName") String nickName,@Param("passwd") String passwd);

    UserInfo selectLeaderInfoByDepartment(String department);
}