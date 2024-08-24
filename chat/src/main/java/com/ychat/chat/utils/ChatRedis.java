package com.ychat.chat.utils;

import com.ychat.chat.domain.Message;
import com.ychat.common.utils.transition.Transition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRedis {

    private final RedisTemplate redisTemplate;

    public void addChannel(String channelId, String userId) {
        redisTemplate.opsForValue().set(userId+"_channelId", channelId);
    }
    public void addPost(String userId, String post) {
        redisTemplate.opsForValue().set(userId+"_post", post);
    }

    /**
     * 每个用户在redis会维护一个未读群聊集合，用来存储哪些群聊包含他的未读信息
     */
    public void addChatIntoUnRead(Long Chat, String userId) {
        List<String> list = getUnReadChat(userId);
        Set<String> set = new HashSet<>(list);
        set.add(Chat.toString());
        redisTemplate.opsForValue().set(userId+"_UnReadChat",set.toString());
    }

    /**
     * 获取需要获取信息的群聊
     */
    public List<String> getUnReadChat(String userId) {
        String UnReadChat=(String) redisTemplate.opsForValue().get(userId+"_UnReadChat");
        List<String> list = Transition.StringToList(UnReadChat);
        return list;
    }

    /**
     * 读取用户上次登录的时间
     */
    public String getLastTime(String userId) {
        String lastTime=(String) redisTemplate.opsForValue().get(userId+"_LastTime");
        return lastTime;
    }

    public String getChannelId(String userId) {
        return (String) redisTemplate.opsForValue().get(userId+"_channelId");
    }
    public String getPost(String userId) {
        return (String) redisTemplate.opsForValue().get(userId+"_post");
    }

    public void removeUnReadChat(String userId) {
        redisTemplate.delete(userId+"_UnReadChat");
    }

    public void setLastTime(String userId) {
        redisTemplate.opsForValue().set(userId+"_UnReadChat",userId);
    }



//********************************************************************************************
    //下面两个没用的
    public void saveMessageIntoRedis(Message msg) {
        HashOperations hashOperations = redisTemplate.opsForHash();

        hashOperations.put("message_"+msg.getId(),"content", msg.getContent());
        hashOperations.put("message_"+msg.getId(),"send", msg.getSender());
        hashOperations.put("message_"+msg.getId(),"chat", msg.getChat());
        hashOperations.put("message_"+msg.getId(),"sendTime", msg.getSendTime());
        hashOperations.put("message_"+msg.getId(),"tag", msg.getTag().toString());
        //hash类型数据不支持设置过期时间，所以只能使用rabbitMq延时消息来实现定时删除
    }
    public Message getMessagesFromRedis(String messageId) {
        HashOperations hashOperations = redisTemplate.opsForHash();

        Message message = new Message();
        //将信息读取到一个message里返回
        message.setChat(Long.parseLong(
                        (String) hashOperations.get("message_" + messageId, "chat")
                )
        );
        message.setContent((String) hashOperations.get("message_"+messageId, "content"));
        message.setSender((String) hashOperations.get("message_"+messageId, "send"));
        message.setSendTime((String) hashOperations.get("message_"+messageId, "chat"));
        message.setTag(
                Integer.parseInt(
                        (String) hashOperations.get("message_" + messageId, "chat")));
        return message;
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