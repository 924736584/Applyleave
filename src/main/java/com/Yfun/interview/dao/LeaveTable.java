package com.Yfun.interview.dao;

import java.util.Date;

/**
 * @ClassName : LeaveTable
 * @Description : ${description}
 * @Author : DeYuan
 * @Date: 2020-08-31 16:44
 */
public class LeaveTable {
    private Integer id;

    private String name;

    private String nickname;

    private String department;

    private Date createDate;

    private String leaveType;

    private String leaveReason;

    private String leaveDate;

    private String permitDate;

    private String executiveMsg;

    private String presidentMsg;

    private String remarks;

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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public String getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(String leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getPermitDate() {
        return permitDate;
    }

    public void setPermitDate(String permitDate) {
        this.permitDate = permitDate;
    }

    public String getExecutiveMsg() {
        return executiveMsg;
    }

    public void setExecutiveMsg(String executiveMsg) {
        this.executiveMsg = executiveMsg;
    }

    public String getPresidentMsg() {
        return presidentMsg;
    }

    public void setPresidentMsg(String presidentMsg) {
        this.presidentMsg = presidentMsg;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}