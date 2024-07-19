package com.ychat.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ychat.user.domain.po.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户表 Mapper 接口
 */
public interface UserMapper extends BaseMapper<User> {
    @Update("update user set balance = balance - ${totalFee} where id = #{userId}")
    void updateMoney(@Param("userId") Long userId, @Param("totalFee") Integer totalFee);

    @Select("select * from user where username = #{username}")
    User getUserByName(String username);
}
