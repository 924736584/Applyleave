package com.Yfun.interview.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
* @ClassName : UserInfo  
* @Description : ${description} 
* @Author : DeYuan
* @Date: 2020-08-28 20:54  
*/
public class UserInfo {
    private Integer id;

    private String name;

    private String nickName;

    private String passwd;

    private String department;

    private String phoneNumber;

    private String email;

    private String idcardNumber;

    private Integer rootLevel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdcardNumber() {
        return idcardNumber;
    }

    public void setIdcardNumber(String idcardNumber) {
        this.idcardNumber = idcardNumber;
    }

    public Integer getRootLevel() {
        return rootLevel;
    }

    public void setRootLevel(Integer rootLevel) {
        this.rootLevel = rootLevel;
    }

    @Override
    public String toString() {
        String s="";
        try {
            s= new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }
}