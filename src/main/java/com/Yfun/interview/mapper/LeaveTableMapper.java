package com.Yfun.interview.mapper;

import com.Yfun.interview.dao.LeaveTable;

/**
 * @ClassName : LeaveTableMapper
 * @Description : ${description}
 * @Author : DeYuan
 * @Date: 2020-08-31 16:44
 */
public interface LeaveTableMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LeaveTable record);

    int insertSelective(LeaveTable record);

    LeaveTable selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LeaveTable record);

    int updateByPrimaryKey(LeaveTable record);
}