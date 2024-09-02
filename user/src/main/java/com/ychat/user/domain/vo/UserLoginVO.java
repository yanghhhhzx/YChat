package com.ychat.user.domain.vo;

import com.ychat.user.config.JwtProperties;
import com.ychat.user.domain.po.User;
import com.ychat.user.enums.Sex;
import com.ychat.user.utils.JwtTool;
import lombok.Data;

@Data
public class UserLoginVO {
    private String token;
    private Long userId;
    private String username;
    private Sex sex;
    private String image;
    private Integer balance;
    private String code;//"1"为冻结."2"为账号或密码错误

//    封装VO返回
    public static UserLoginVO NewVo(User user, JwtTool jwtTool, JwtProperties jwtProperties){
        // 生成TOKEN
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        // 6.封装VO返回
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setBalance(user.getBalance());
        vo.setImage(user.getImage());
        vo.setSex(user.getSex());
        vo.setToken(token);
        return vo;
    }
}
