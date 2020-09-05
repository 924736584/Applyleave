## 设计文档：

### 严格遵守Alibaba开发规范

## 需求：

	允许请假：
		员工申请请假主管批准写上评语然后转交给上级上级经理审批然后允许请假，消息通知到员工邮箱或者短信
	主管拒绝请假:
		员工申请请假主管拒绝必须写上拒绝理由，上级不会收到请假请求但是可以查询主管拒觉绝的请求，消息通知到员工邮箱或者短信
	经理拒绝请假:
		员工申请请假主管同意写上评语然后转发到上级，上级会收到请假请求，上级拒绝后,主管不会收到消息,消息通知到员工邮箱或者短信

## 角色

	员工:
		功能:
		1):申请请假
		2):请假取消
		3):查询请假进度
		4):提醒上级请假请求一天一次 
	主管:
		功能:
		1):查询员工请假请求
		2):批准员工请假
		3):拒绝员工请假(写拒绝原因)
		4):加急处理员工请求(提醒上级处理)
	经理:
		1):查询员工请假请求
		2):批准员工请假请求
		3):拒绝请假(拒绝理由)
		4):查询主管拒绝请求

## 技术支持

	架构:SSM架构
	数据库:mysql,redis,Elasticsearch
	消息队列:rabbitMQ
	模板引擎:thyleaf
	前端:JS+CSS+HTML5+ajax
	数据传递JSON

##  Mysql数据结构

> leave_table(员工请假申请表) 

```bash
-----------------------------------------
Id(编号)                     PKInteger   //<<自增长>>
name(姓名)                   String(20)  //员工姓名
department(部门)             String(20)  //<<唯一索引>>部门
create_date(填表日期)        Date        //default SystemData
leave_type(请假类别)         String(15)  //<<非空>>请假类别 0:事假，2:病假，3:婚假，4:产假，5:丧假,6:年休假，7:其他
leave_reason(请假事由)       String(255) //请假事由
leave_date        VARCHAR(40) 	/*请假时间 请假时间*/,
permit_date       VARCHAR(40)   	/*准假时间 准假时间(经理批阅)*/,
executive_msg(部门主管意见)  String(255) //部门主管意见(主管批阅)
president_msg(总经理意见)    String(255) //总经理意见
remarks(备注)                String(255) //备注*
```

```sql
create table  leave_table
(
       Id                INTEGER /*auto_increment*/ not null 	/*编号*/,
       name              VARCHAR(20) 	/*姓名 员工姓名*/,
       department        VARCHAR(20) 	/*部门 部门*/,
       create_date       DATETIME 	/*填表日期 default SystemData*/,
       leave_type        VARCHAR(15) not null 	/*请假类别 请假类别 0:事假，2:病假，3:婚假，4:产假，5:丧假,6:年休假，7:其他*/,
       leave_reason      VARCHAR(255) 	/*请假事由 请假事由*/,
       leave_date        VARCHAR(40) 	/*请假时间 请假时间*/,
       permit_date       VARCHAR(40)   	/*准假时间 准假时间(经理批阅)*/,
       executive_msg     VARCHAR(255) 	/*部门主管意见 部门主管意见(主管批阅)*/,
       president_msg     VARCHAR(255) 	/*总经理意见 总经理意见*/,
       remarks           VARCHAR(255) 	/*备注 备注*/
);
alter  table Leave_table
       add constraint PK_Leave_table_Id primary key (Id);
create unique index IDU_Leave_tble_department7E3C on Leave_table(department);
```



> user_info(用户信息表)

```bash
	
//用户信息
--------------------------------------
id(编号)                   PKInteger   //<<自增长>>
name(姓名)                 String(30)
nick_name(用户名)          String(34)  //<<唯一索引>>由系统分配用户名
passwd(密码)               String(64)  //<<非空>>Md5加密
department(部门)           String(50)  //<<非空>>
phone_number(电话)         String(12)  //<<非空>>
email(邮箱)                String
idcard_number(身份证号码)  String(20)  //<<非空>>
root_level(级别)           Integer     //<<非空>>default -1:删除该员工,0:员工，1,主管，2:经理
```

```sql
create table  user_info
(
       id                INTEGER /*auto_increment*/ not null 	/*编号*/,
       name              VARCHAR(30) 	/*姓名*/,
       nick_name         VARCHAR(34) 	/*用户名 由系统分配用户名*/,
       passwd            VARCHAR(64) not null 	/*密码 Md5加密*/,
       department        VARCHAR(50) not null 	/*部门*/,
       phone_number      VARCHAR(12) not null 	/*电话*/,
       email             VARCHAR(4000) 	/*邮箱*/,
       idcard_number     VARCHAR(20) not null 	/*身份证号码*/,
       root_level        INTEGER not null 	/*级别 default -1:删除该员工,0:员工，1,主管，2:经理*/
);
alter  table user_info
       add constraint PK_user_info_id primary key (id);
create unique index IDU_user_info_nick_name on user_info(nick_name);
```



> Approval_progress(审批进度)

```bash
Approval_progress(审批进度)
-----------------------------
Id(编号)          PKInteger
name(姓名)        String
nickname(账户名)  String(34)
department(部门)  String(40)
progress(进度)    Integer     //-1：未查看,0:主管审核，1:经理审核2:审核通过
```

```sql
create table  Approval_progress
(
       id                INTEGER not null 	/*编号*/,
       name              VARCHAR(4000) 	/*姓名*/,
       nickname          VARCHAR(34) 	/*账户名*/,
       department        VARCHAR(40) 	/*部门*/,
       progress          INTEGER 	/*进度 -1：未查看,0:主管审核，1:经理审核2:审核通过*/
);
alter  table Approval_progress
       add constraint PK_Approval_progress_Id primary key (Id);
```



> leavesystem_log_info(日志系统记录)

```bash
leavesystem_log_info(日志系统记录)
------------------------------------------------
log_date(日志生成日期)             String(50)  //日志生成时间
log_level(日志级别)                String(6)   //日志级别
message(日志详细信息)              String(1000) //日志详细信息
param(异常日志方法传入的具体参数)  String(1000) //异常日志方法传入的具体参数实现异常重现


```

```sql
create table  leavesystem_log_info
(
       log_date          VARCHAR(50) 	/*日志生成日期 日志生成时间*/,
       log_level         VARCHAR(6) 	/*日志级别 日志级别*/,
       message           VARCHAR(1000) 	/*日志详细信息 日志详细信息*/,
       param             VARCHAR(1000) 	/*异常日志方法传入的具体参数 异常日志方法传入的具体参数实现异常重现*/
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
```



## 员工具体实现

### 注册

#### 接口

```java
/**
* Save user registration information
* @param Map
* map this UserInfo value
* @return Map{status,message,userbasic_msg}.toString
*/
 String registerSave(Map map);
/**
* Check user information
* @param Map
* map this UserInfo value
* @return Map{status,message{email_error,phone_error,idcard_error,email_error}}.toString
*/
String registerCheckInfo(Map map);
```



### 登录

####  接口：

```java
/**
*登录检查数据
*@param Map
* This Param is UserInfo value
*@return 
* return Map{status,message{nickname_erorr,passwd_erorr,verification_results}}.toString
*/
public String loginCheckData(Map map);

```

#### redis数据库：

用户登录成功直接将数据存入Redis超时时间设置为3小时

便于后期数据的查询与单点登陆的实现

KEY="USER_LOGIN"+nickName

### 申请请假

数据来源与这个表格

![image-20200831145433526](E:\MdContentText\image\image-20200831145433526.png)

#### 接口

```java
/**
*
*@param map
* this map is LeaveTable value
*@return
* Map{status,message{leavetype_error,leavedate_error,verification_results}}.toString
*/
String leaveApplySaveInfo(Map map);
```

### 消息通知

请假申请成功后会通过RabbitMq进行邮件发送和短信通知到指定部门的主管

搜索Redis数据库没查询到去数据库查询,数据库查询到了就同步Redis数据库

注意处理Redis的缓存穿透缓存雪崩，缓存击穿的问题



提交人员的部门主管的电话,邮箱等基础信息








​	