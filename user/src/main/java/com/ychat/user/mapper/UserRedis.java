package com.ychat.user.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRedis {

    private final RedisTemplate redisTemplate;

    public void addTime(String userid){
        Date now = new Date();
        // 创建一个SimpleDateFormat对象，定义时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 使用SimpleDateFormat对象的format方法将Date对象格式化为字符串
        String dateString = dateFormat.format(now);
        redisTemplate.opsForValue().set(userid+"_LastTime", dateString);
    }

}
