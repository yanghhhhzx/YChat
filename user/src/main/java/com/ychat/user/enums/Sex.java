package com.ychat.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.ychat.common.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum Sex {
    FROZEN(0, "女"),
    NORMAL(1, "男"),
    ;
    @EnumValue
    int value;
    String desc;

    Sex(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static Sex of(int value) {
        if (value == 0) {
            return FROZEN;
        }
        if (value == 1) {
            return NORMAL;
        }
        throw new BadRequestException("性别错误");
    }
}