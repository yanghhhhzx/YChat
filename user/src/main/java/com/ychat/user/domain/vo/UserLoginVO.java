package com.ychat.user.domain.vo;

import com.ychat.user.enums.Sex;
import lombok.Data;

@Data
public class UserLoginVO {
    private String token;
    private Long userId;
    private String username;
    private Sex sex;
    private String image;
    private Integer balance;
}
