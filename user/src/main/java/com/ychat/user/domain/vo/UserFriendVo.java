package com.ychat.user.domain.vo;

import com.ychat.user.enums.Sex;
import lombok.Data;

@Data
public class UserFriendVo {
    private Long userId;
    private String username;
    private Sex sex;
    private String image;

}