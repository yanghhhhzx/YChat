package com.ychat.chat.utils;

import com.ychat.common.utils.transition.Transition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRedis {

    private final RedisTemplate redisTemplate;

    public void addChannel(String channelId, String userId) {
        redisTemplate.opsForValue().set(userId+"_channelId", channelId);
    }

    /**
     * 每个用户在redis会维护一个未读群聊集合，用来存储哪些群聊包含他的未读信息
     * @param Chat
     * @param userId
     */
    public void addChatIntoUnRead(Long Chat, String userId) {
        List<String> list = getUnReadChat(userId);
        Set<String> set = new HashSet<>(list);
        set.add(Chat.toString());
        redisTemplate.opsForValue().set(userId+"_UnReadChat",set.toString());
    }

    /**
     * 获取需要获取信息的群聊
     * @param userId
     * @return
     */
    public List<String> getUnReadChat(String userId) {
        String UnReadChat=(String) redisTemplate.opsForValue().get(userId+"_UnReadChat");
        List<String> list = Transition.StringToList(UnReadChat);
        return list;
    }

    /**
     * 读取用户上次登录的时间
     * @param userId
     * @return
     */
    public String getLastTime(String userId) {
        String lastTime=(String) redisTemplate.opsForValue().get(userId+"_LastTime");
        return lastTime;
    }

    public String getChannelId(String userId) {
        return (String) redisTemplate.opsForValue().get(userId+"_channelId");
    }

    public void removeUnReadChat(String userId) {
        redisTemplate.delete(userId+"_UnReadChat");
    }

    public void setLastTime(String userId) {
        redisTemplate.opsForValue().set(userId+"_UnReadChat",userId);
    }
}


////字符串转date：
//Date time = new Date();
//        try {
//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//time = formatter.parse(lastTime);
//        } catch (Exception e) {
//        System.out.println("日期数据错误！无法转换");
//        }