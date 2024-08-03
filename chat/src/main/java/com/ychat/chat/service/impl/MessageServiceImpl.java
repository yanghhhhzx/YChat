package com.ychat.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.chat.domain.Message;
import com.ychat.chat.mapper.MessageMapper;
import com.ychat.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper,Message> implements MessageService {

    private final RedisTemplate redisTemplate;

    private final MessageMapper messageMapper;

    //因为引入了common，所以可以拿到common中的拦截器，拿到
    //todo 完成下面功能的开发
    @Override
    public void saveMessageIntoRedis(Message msg) {

        HashOperations hashOperations = redisTemplate.opsForHash();

        hashOperations.put("message_"+msg.getId(),"content", msg.getContent());
        hashOperations.put("message_"+msg.getId(),"send", msg.getSender());
        hashOperations.put("message_"+msg.getId(),"chat", msg.getChat());
        hashOperations.put("message_"+msg.getId(),"sendTime", msg.getSendTime());
        hashOperations.put("message_"+msg.getId(),"tag", msg.getTag().toString());
        //hash类型数据不支持设置过期时间，所以只能使用rabbitMq延时消息来实现定时删除

    }

    @Override
    public void saveMessageIntoMysql(Message message) {
        save(message);
    }

    @Override
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

    /**
     * 查找LastTime之后发送的信息，且在对应群聊发送的信息
     * @param ChatIds
     * @param LastTime
     * @return
     */
    @Override
    public List<Message> getNewMessagesFromMysql(List<Long> ChatIds, String LastTime) {
        Date now = new Date();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            now = formatter.parse(LastTime);
        } catch (Exception e) {
            System.out.println("日期数据错误！无法转换");
        }
        List<Message> newMessageList= messageMapper.selectMessageByChatIdAndTime(ChatIds,now);

        return newMessageList;
    }

}
