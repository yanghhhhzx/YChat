package com.ychat.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ychat.user.enums.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.ychat.user.enums.UserStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("com/ychat/user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码，加密存储
     */
    private String password;
    /**
     * 注册手机号
     */
    private String phone;
    /**
     * 头像
     */
    private String image;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    /**
     * 使用状态（1正常 2冻结）
     */
    private UserStatus status;
    /**
     * 账户余额
     */
    private Integer balance;
    /**
     * 性别
     */
    private Sex sex;
    private String chat;//使用方案2时的群聊

}
