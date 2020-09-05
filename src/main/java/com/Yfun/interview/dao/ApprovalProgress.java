package com.Yfun.interview.dao;

/**
* @ClassName : ApprovalProgress  
* @Description : ${description} 
* @Author : DeYuan
* @Date: 2020-08-28 20:54  
*/
public class ApprovalProgress {

    private Integer id;

    private String name;

    private String nickname;

    private String department;

    private Integer progress;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}