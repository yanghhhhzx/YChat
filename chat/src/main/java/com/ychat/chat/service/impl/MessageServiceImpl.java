package com.ychat.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.chat.domain.Message;
import com.ychat.chat.mapper.MessageMapper;
import com.ychat.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void saveMessageIntoMysql(Message message) {
        messageMapper.addMessage(message);
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
