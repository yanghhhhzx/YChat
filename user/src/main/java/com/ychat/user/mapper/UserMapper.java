package com.ychat.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ychat.user.domain.dto.UserDTO;
import com.ychat.user.domain.po.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<User> {
    @Update("update user_0 set balance = balance - ${totalFee} where id = #{userId}")
    void updateMoney(@Param("userId") Long userId, @Param("totalFee") Integer totalFee);

    @Select("select * from user_0 where username = #{username}")
    User getUserByName(String username);

    @Select("select * from user_0 where phone = #{phone}")
    User getUserByPhone(String phone);

    void updateUser(User user);

    User getUserByObject(UserDTO userDTO);
    @Insert("insert into user_0(username,password,image,sex,create_time,update_time,phone) " +
            "values(#{username},#{password},#{image},#{sex},#{createTime},#{updateTime},#{phone}) ")
    void insertUser(User user1);
}
